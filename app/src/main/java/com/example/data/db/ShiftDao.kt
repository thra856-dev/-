package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.MonthlyAdjustmentEntity
import com.example.data.entity.ShiftRecord
import com.example.data.entity.UserSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShiftDao {
    @Query("SELECT * FROM shifts ORDER BY dateString DESC, id DESC")
    fun getAllShifts(): Flow<List<ShiftRecord>>

    @Query("SELECT * FROM shifts WHERE dateString LIKE :monthPrefix || '%' ORDER BY dateString ASC, id ASC")
    fun getShiftsForMonth(monthPrefix: String): Flow<List<ShiftRecord>>

    @Query("SELECT * FROM shifts WHERE dateString = :dateString LIMIT 1")
    suspend fun getShiftForDate(dateString: String): ShiftRecord?

    @Query("SELECT * FROM shifts WHERE id = :id LIMIT 1")
    suspend fun getShiftById(id: Long): ShiftRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShift(shift: ShiftRecord): Long

    @Update
    suspend fun updateShift(shift: ShiftRecord)

    @Delete
    suspend fun deleteShift(shift: ShiftRecord)

    @Query("DELETE FROM shifts WHERE id = :id")
    suspend fun deleteShiftById(id: Long)

    @Query("DELETE FROM shifts")
    suspend fun clearAllShifts()
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<UserSettingsEntity?>

    @Query("SELECT * FROM user_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsDirect(): UserSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: UserSettingsEntity)
}

@Dao
interface MonthlyAdjustmentDao {
    @Query("SELECT * FROM monthly_adjustments WHERE monthPrefix = :monthPrefix LIMIT 1")
    fun getAdjustmentsForMonth(monthPrefix: String): Flow<MonthlyAdjustmentEntity?>

    @Query("SELECT * FROM monthly_adjustments WHERE monthPrefix = :monthPrefix LIMIT 1")
    suspend fun getAdjustmentsDirect(monthPrefix: String): MonthlyAdjustmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAdjustments(adjustments: MonthlyAdjustmentEntity)
}
