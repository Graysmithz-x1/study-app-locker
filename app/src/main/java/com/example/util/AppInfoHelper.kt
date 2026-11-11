package com.example.util

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class InstalledAppItem(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val isSystemApp: Boolean
)

object AppInfoHelper {
    suspend fun getInstalledApps(context: Context): List<InstalledAppItem> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = pm.queryIntentActivities(mainIntent, 0)
        val selfPackage = context.packageName

        val apps = mutableListOf<InstalledAppItem>()
        val seen = mutableSetOf<String>()

        for (resolveInfo in resolveInfos) {
            val pkg = resolveInfo.activityInfo.packageName
            if (pkg == selfPackage || seen.contains(pkg)) continue
            seen.add(pkg)

            val appName = resolveInfo.loadLabel(pm).toString()
            val icon = try {
                resolveInfo.loadIcon(pm)
            } catch (_: Exception) {
                null
            }
            val isSystem = (resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

            apps.add(
                InstalledAppItem(
                    packageName = pkg,
                    appName = appName,
                    icon = icon,
                    isSystemApp = isSystem
                )
            )
        }

        // Sort: user apps first, then alphabetical
        apps.sortedWith(compareBy({ it.isSystemApp }, { it.appName.lowercase() }))
    }
}
