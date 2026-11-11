package com.example.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.StudyLockerApplication
import com.example.data.LockedAppEntity
import com.example.ui.lock.LockQuizActivity
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TertiaryAmber
import com.example.util.AppInfoHelper
import com.example.util.InstalledAppItem
import com.example.util.PermissionHelper
import kotlinx.coroutines.launch

@Composable
fun AppLockerScreen(
    onLaunchTestQuiz: (String, String) -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as StudyLockerApplication
    val scope = rememberCoroutineScope()

    val settings by app.settingsRepository.settings.collectAsState()
    val lockedAppsFromDb by app.database.lockedAppDao().getAllLockedAppsFlow().collectAsState(initial = emptyList())

    var installedApps by remember { mutableStateOf<List<InstalledAppItem>>(emptyList()) }
    var isLoadingApps by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") } // ALL, LOCKED, UNLOCKED

    var hasUsageAccess by remember { mutableStateOf(PermissionHelper.hasUsageStatsPermission(context)) }
    var hasOverlay by remember { mutableStateOf(PermissionHelper.hasOverlayPermission(context)) }

    // Recheck permissions periodically or on resume
    LaunchedEffect(Unit) {
        hasUsageAccess = PermissionHelper.hasUsageStatsPermission(context)
        hasOverlay = PermissionHelper.hasOverlayPermission(context)
        installedApps = AppInfoHelper.getInstalledApps(context)
        isLoadingApps = false
    }

    val lockedMap = remember(lockedAppsFromDb) {
        lockedAppsFromDb.associateBy { it.packageName }
    }

    val filteredApps = remember(installedApps, lockedMap, searchQuery, selectedFilter) {
        installedApps.filter { appItem ->
            val matchesQuery = appItem.appName.contains(searchQuery, ignoreCase = true) ||
                    appItem.packageName.contains(searchQuery, ignoreCase = true)
            val isLocked = lockedMap[appItem.packageName]?.isLocked == true

            val matchesFilter = when (selectedFilter) {
                "LOCKED" -> isLocked
                "UNLOCKED" -> !isLocked
                else -> true
            }

            matchesQuery && matchesFilter
        }
    }

    val lockedCount = lockedMap.values.count { it.isLocked }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("app_locker_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Service Status & Toggle Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (settings.isServiceEnabled) PrimaryIndigo.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (settings.isServiceEnabled) PrimaryIndigo else Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (settings.isServiceEnabled) "Layanan Pengunci Aktif" else "Layanan Tidak Aktif",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (settings.isServiceEnabled) "$lockedCount aplikasi dikunci oleh kuis" else "Aktifkan agar kuis memblokir aplikasi",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = settings.isServiceEnabled,
                        onCheckedChange = { app.settingsRepository.setServiceEnabled(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = PrimaryIndigo),
                        modifier = Modifier.testTag("switch_service_toggle")
                    )
                }
            }
        }

        // Permission Alert Banner if required permissions missing
        if (!hasUsageAccess || !hasOverlay) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = TertiaryAmber.copy(alpha = 0.12f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = TertiaryAmber,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Izin Sistem Diperlukan",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Agar layar kuis dapat mendeteksi dan mengunci aplikasi secara otomatis, mohon aktifkan izin berikut:",
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (!hasUsageAccess) {
                            Button(
                                onClick = {
                                    PermissionHelper.openUsageAccessSettings(context)
                                    hasUsageAccess = PermissionHelper.hasUsageStatsPermission(context)
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_grant_usage_stats")
                            ) {
                                Text("1. Berikan Izin Akses Penggunaan (Usage Access)")
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }

                        if (!hasOverlay) {
                            Button(
                                onClick = {
                                    PermissionHelper.openOverlaySettings(context)
                                    hasOverlay = PermissionHelper.hasOverlayPermission(context)
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_grant_overlay")
                            ) {
                                Text("2. Berikan Izin Display Over Other Apps")
                            }
                        }
                    }
                }
            }
        }

        // Quick Test Simulation Action
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Uji Coba Kuis Sekarang",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Coba alur kuis, penalti kesalahan & achievement",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = {
                            val sampleApp = installedApps.firstOrNull()
                            val pkg = sampleApp?.packageName ?: "com.sample.target"
                            val name = sampleApp?.appName ?: "Aplikasi Demo"
                            onLaunchTestQuiz(pkg, name)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                        modifier = Modifier.testTag("btn_test_quiz_now")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tes Kuis")
                    }
                }
            }
        }

        // Search Bar & Filter Chips
        item {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari aplikasi...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_installed_apps")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" },
                        label = { Text("Semua (${installedApps.size})") }
                    )
                    FilterChip(
                        selected = selectedFilter == "LOCKED",
                        onClick = { selectedFilter = "LOCKED" },
                        label = { Text("Terkunci ($lockedCount)") }
                    )
                    FilterChip(
                        selected = selectedFilter == "UNLOCKED",
                        onClick = { selectedFilter = "UNLOCKED" },
                        label = { Text("Bebas (${installedApps.size - lockedCount})") }
                    )
                }
            }
        }

        // Loading or List of Apps
        if (isLoadingApps) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryIndigo)
                }
            }
        } else if (filteredApps.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tidak ada aplikasi yang cocok.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(filteredApps, key = { it.packageName }) { appItem ->
                val lockedEntity = lockedMap[appItem.packageName]
                val isLocked = lockedEntity?.isLocked == true
                val currentTime = System.currentTimeMillis()
                val isTemporarilyUnlocked = isLocked && (lockedEntity?.unlockedUntil ?: 0L) > currentTime

                AppLockItemRow(
                    appItem = appItem,
                    isLocked = isLocked,
                    isTemporarilyUnlocked = isTemporarilyUnlocked,
                    remainingMinutes = if (isTemporarilyUnlocked) {
                        ((lockedEntity!!.unlockedUntil - currentTime) / (60 * 1000L)).toInt() + 1
                    } else 0,
                    onToggleLock = { newLockedState ->
                        scope.launch {
                            app.database.lockedAppDao().insertOrUpdate(
                                LockedAppEntity(
                                    packageName = appItem.packageName,
                                    appName = appItem.appName,
                                    isLocked = newLockedState,
                                    unlockedUntil = 0L
                                )
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AppLockItemRow(
    appItem: InstalledAppItem,
    isLocked: Boolean,
    isTemporarilyUnlocked: Boolean,
    remainingMinutes: Int,
    onToggleLock: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(1.5.dp),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Icon
            val iconBitmap = remember(appItem.icon) {
                appItem.icon?.let { drawableToBitmap(it) }
            }

            if (iconBitmap != null) {
                Image(
                    bitmap = iconBitmap.asImageBitmap(),
                    contentDescription = appItem.appName,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Android,
                        contentDescription = null,
                        tint = PrimaryIndigo,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // App Name & Status
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appItem.appName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isLocked) {
                        if (isTemporarilyUnlocked) {
                            Text(
                                text = "🔓 Terbuka sisa ${remainingMinutes}m",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        } else {
                            Text(
                                text = "🔒 Terkunci (Wajib Kuis)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ErrorRed
                            )
                        }
                    } else {
                        Text(
                            text = "Bebas dibuka",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Lock Switch
            Switch(
                checked = isLocked,
                onCheckedChange = { onToggleLock(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = PrimaryIndigo,
                    checkedTrackColor = PrimaryIndigo.copy(alpha = 0.5f)
                ),
                modifier = Modifier.testTag("toggle_lock_${appItem.packageName}")
            )
        }
    }
}

fun drawableToBitmap(drawable: Drawable): Bitmap {
    if (drawable is BitmapDrawable && drawable.bitmap != null) {
        return drawable.bitmap
    }
    val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 96
    val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 96
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}
