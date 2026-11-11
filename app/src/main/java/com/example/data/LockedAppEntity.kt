package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locked_apps")
data class LockedAppEntity(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val isLocked: Boolean = true,
    val unlockedUntil: Long = 0L,
    val updatedAt: Long = System.currentTimeMillis()
)
