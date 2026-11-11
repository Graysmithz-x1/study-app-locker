package com.example.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.StudyLockerApplication
import com.example.data.LockedAppEntity
import com.example.ui.lock.LockQuizActivity
import com.example.util.PermissionHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AppLockService : Service() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Default + serviceJob)

    private var lastInterceptedPackage: String? = null
    private var lastInterceptTime: Long = 0L

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(
            StudyLockerApplication.NOTIFICATION_ID,
            buildNotification("Study App Locker Aktif", "Memantau aplikasi yang dikunci...")
        )
        startMonitoringLoop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_STOP_SERVICE) {
            stopSelf()
            return START_NOT_STICKY
        }
        return START_STICKY
    }

    private fun startMonitoringLoop() {
        val app = application as StudyLockerApplication
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager

        serviceScope.launch {
            while (isActive) {
                try {
                    val settings = app.settingsRepository.settings.value
                    if (!settings.isServiceEnabled) {
                        delay(2000)
                        continue
                    }

                    if (!PermissionHelper.hasUsageStatsPermission(this@AppLockService)) {
                        delay(3000)
                        continue
                    }

                    val lockedApps = app.database.lockedAppDao().getActiveLockedApps()
                    val lockedMap = lockedApps.associateBy { it.packageName }

                    val topPackage = getForegroundPackage(usageStatsManager)
                    if (topPackage != null &&
                        topPackage != packageName &&
                        topPackage != "com.google.android.packageinstaller" &&
                        topPackage != "com.android.packageinstaller" &&
                        topPackage != "com.android.settings"
                    ) {
                        val lockedEntity = lockedMap[topPackage]
                        if (lockedEntity != null) {
                            val currentTime = System.currentTimeMillis()
                            val isStillUnlocked = currentTime < lockedEntity.unlockedUntil

                            if (!isStillUnlocked) {
                                // Locked and time expired or not yet unlocked
                                val timeSinceLastIntercept = currentTime - lastInterceptTime
                                if (topPackage != lastInterceptedPackage || timeSinceLastIntercept > 4000) {
                                    lastInterceptedPackage = topPackage
                                    lastInterceptTime = currentTime
                                    launchLockQuiz(lockedEntity)
                                }
                            }
                        } else {
                            // Switched away to an unlocked app or launcher
                            if (topPackage != lastInterceptedPackage) {
                                lastInterceptedPackage = null
                            }
                        }
                    }
                } catch (_: Exception) {
                    // Ignore transient errors
                }
                delay(650)
            }
        }
    }

    private fun getForegroundPackage(usageStatsManager: UsageStatsManager?): String? {
        if (usageStatsManager == null) return null
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 10_000

        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
        var lastTopPackage: String? = null
        val event = UsageEvents.Event()

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED ||
                event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND
            ) {
                lastTopPackage = event.packageName
            }
        }

        if (lastTopPackage != null) {
            return lastTopPackage
        }

        // Fallback to queryUsageStats
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )
        return stats?.maxByOrNull { it.lastTimeUsed }?.packageName
    }

    private fun launchLockQuiz(lockedApp: LockedAppEntity) {
        val intent = Intent(this, LockQuizActivity::class.java).apply {
            putExtra(LockQuizActivity.EXTRA_PACKAGE_NAME, lockedApp.packageName)
            putExtra(LockQuizActivity.EXTRA_APP_NAME, lockedApp.appName)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
    }

    private fun buildNotification(title: String, content: String): Notification {
        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        return NotificationCompat.Builder(this, StudyLockerApplication.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        serviceJob.cancel()
        super.onDestroy()
    }

    companion object {
        const val ACTION_STOP_SERVICE = "com.example.service.STOP_SERVICE"
    }
}
