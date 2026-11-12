package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.entity.MonthlyAdjustmentEntity
import com.example.data.entity.ShiftRecord
import com.example.data.entity.UserSettingsEntity

@Database(
    entities = [
        ShiftRecord::class,
        UserSettingsEntity::class,
        MonthlyAdjustmentEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shiftDao(): ShiftDao
    abstract fun settingsDao(): SettingsDao
    abstract fun monthlyAdjustmentDao(): MonthlyAdjustmentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shifts_salary_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
