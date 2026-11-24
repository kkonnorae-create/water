package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DailyRecordEntity::class], version = 1, exportSchema = false)
abstract class SeniorHealthDatabase : RoomDatabase() {
  abstract fun dailyRecordDao(): DailyRecordDao

  companion object {
    @Volatile private var instance: SeniorHealthDatabase? = null

    fun getInstance(context: Context): SeniorHealthDatabase {
      return instance
        ?: synchronized(this) {
          instance
            ?: Room.databaseBuilder(
                context.applicationContext,
                SeniorHealthDatabase::class.java,
                "senior_health_db",
              )
              .build()
              .also { instance = it }
        }
    }
  }
}
