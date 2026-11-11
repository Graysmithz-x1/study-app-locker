package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LockedAppDao {
    @Query("SELECT * FROM locked_apps ORDER BY appName ASC")
    fun getAllLockedAppsFlow(): Flow<List<LockedAppEntity>>

    @Query("SELECT * FROM locked_apps WHERE isLocked = 1")
    suspend fun getActiveLockedApps(): List<LockedAppEntity>

    @Query("SELECT * FROM locked_apps WHERE packageName = :packageName LIMIT 1")
    suspend fun getLockedApp(packageName: String): LockedAppEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(app: LockedAppEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(apps: List<LockedAppEntity>)

    @Query("UPDATE locked_apps SET isLocked = :isLocked, updatedAt = :updatedAt WHERE packageName = :packageName")
    suspend fun updateLockStatus(packageName: String, isLocked: Boolean, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE locked_apps SET unlockedUntil = :timestamp, updatedAt = :updatedAt WHERE packageName = :packageName")
    suspend fun setUnlockedUntil(packageName: String, timestamp: Long, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE locked_apps SET unlockedUntil = :timestamp, updatedAt = :updatedAt WHERE isLocked = 1")
    suspend fun setAllUnlockedUntil(timestamp: Long, updatedAt: Long = System.currentTimeMillis())

    @Query("DELETE FROM locked_apps WHERE packageName = :packageName")
    suspend fun deleteByPackageName(packageName: String)
}
