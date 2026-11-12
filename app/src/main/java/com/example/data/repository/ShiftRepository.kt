package com.example.data.repository

import com.example.data.db.MonthlyAdjustmentDao
import com.example.data.db.SettingsDao
import com.example.data.db.ShiftDao
import com.example.data.entity.MonthlyAdjustmentEntity
import com.example.data.entity.ShiftRecord
import com.example.data.entity.UserSettingsEntity
import com.example.data.model.ShiftType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ShiftRepository(
    private val shiftDao: ShiftDao,
    private val settingsDao: SettingsDao,
    private val adjustmentDao: MonthlyAdjustmentDao
) {
    val allShifts: Flow<List<ShiftRecord>> = shiftDao.getAllShifts()

    fun getShiftsForMonth(monthPrefix: String): Flow<List<ShiftRecord>> {
        return shiftDao.getShiftsForMonth(monthPrefix)
    }

    val settings: Flow<UserSettingsEntity> = settingsDao.getSettings().map { existing ->
        existing ?: UserSettingsEntity()
    }

    suspend fun getSettingsDirect(): UserSettingsEntity {
        return settingsDao.getSettingsDirect() ?: UserSettingsEntity().also {
            settingsDao.saveSettings(it)
        }
    }

    suspend fun saveSettings(settings: UserSettingsEntity) {
        settingsDao.saveSettings(settings)
    }

    fun getAdjustmentsForMonth(monthPrefix: String): Flow<MonthlyAdjustmentEntity> {
        return adjustmentDao.getAdjustmentsForMonth(monthPrefix).map { existing ->
            existing ?: MonthlyAdjustmentEntity(monthPrefix = monthPrefix)
        }
    }

    suspend fun getAdjustmentsDirect(monthPrefix: String): MonthlyAdjustmentEntity {
        return adjustmentDao.getAdjustmentsDirect(monthPrefix)
            ?: MonthlyAdjustmentEntity(monthPrefix = monthPrefix).also {
                adjustmentDao.saveAdjustments(it)
            }
    }

    suspend fun saveAdjustments(adjustments: MonthlyAdjustmentEntity) {
        adjustmentDao.saveAdjustments(adjustments)
    }

    suspend fun saveShift(shift: ShiftRecord): Long {
        return shiftDao.insertShift(shift)
    }

    suspend fun deleteShift(shift: ShiftRecord) {
        shiftDao.deleteShift(shift)
    }

    suspend fun deleteShiftById(id: Long) {
        shiftDao.deleteShiftById(id)
    }

    suspend fun getShiftForDate(dateString: String): ShiftRecord? {
        return shiftDao.getShiftForDate(dateString)
    }

    /**
     * Calculates paid hours and earned amount based on shift type and settings.
     * Special rule: Night shift has +15 minutes (+0.25h) for each 1 hour worked.
     */
    fun calculateShiftValues(
        shiftType: ShiftType,
        actualHours: Double,
        hourlyRate: Double,
        nightBonusMinutesPerHour: Int = 15
    ): Pair<Double, Double> {
        if (shiftType == ShiftType.OFF || actualHours <= 0.0) {
            return Pair(0.0, 0.0)
        }

        val paidHours = if (shiftType == ShiftType.NIGHT) {
            val bonusFraction = nightBonusMinutesPerHour / 60.0 // 15 / 60 = 0.25 (quarter hour)
            actualHours * (1.0 + bonusFraction)
        } else {
            actualHours
        }

        val earnedAmount = paidHours * hourlyRate
        return Pair(paidHours, earnedAmount)
    }
}
