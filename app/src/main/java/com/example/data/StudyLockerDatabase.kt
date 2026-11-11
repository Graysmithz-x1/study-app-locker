package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [QuestionEntity::class, LockedAppEntity::class],
    version = 1,
    exportSchema = false
)
abstract class StudyLockerDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
    abstract fun lockedAppDao(): LockedAppDao

    companion object {
        @Volatile
        private var INSTANCE: StudyLockerDatabase? = null

        fun getInstance(context: Context): StudyLockerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StudyLockerDatabase::class.java,
                    "study_locker_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate default questions on first database creation
                            CoroutineScope(Dispatchers.IO).launch {
                                getInstance(context).questionDao().insertAll(DefaultQuestions.getList())
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
