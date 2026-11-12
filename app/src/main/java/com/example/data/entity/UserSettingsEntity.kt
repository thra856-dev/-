package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val hourlyRate: Double = 5000.0,
    val currency: String = "د.ع",
    val nightBonusMinutesPerHour: Int = 15, // 15 mins per hour = 0.25 (quarter hour)
    val defaultShiftDurationHours: Double = 8.0,
    val monthlyFixedBonus: Double = 0.0,
    val monthlyFixedDeductions: Double = 0.0
) {
    val nightBonusFactor: Double
        get() = 1.0 + (nightBonusMinutesPerHour / 60.0)
}
