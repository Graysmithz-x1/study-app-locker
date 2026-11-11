package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.data.DefaultQuestions
import com.example.data.SettingsRepository
import com.example.data.StudyLockerDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudyLockerApplication : Application() {

    lateinit var database: StudyLockerDatabase
        private set

    lateinit var settingsRepository: SettingsRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = StudyLockerDatabase.getInstance(this)
        settingsRepository = SettingsRepository(this)

        createNotificationChannel()

        // Ensure default questions exist
        CoroutineScope(Dispatchers.IO).launch {
            if (database.questionDao().getQuestionsCount() == 0) {
                database.questionDao().insertAll(DefaultQuestions.getList())
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_desc)
                setShowBadge(false)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "study_locker_channel"
        const val NOTIFICATION_ID = 1001
    }
}
