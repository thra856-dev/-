package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ShiftType

@Entity(tableName = "shifts")
data class ShiftRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateString: String, // Format: YYYY-MM-DD
    val shiftType: ShiftType,
    val startTime: String = "08:00",
    val endTime: String = "16:00",
    val breakMinutes: Int = 0,
    val actualHours: Double = 8.0,
    val paidHours: Double = 8.0,
    val hourlyRateSnapshot: Double = 5000.0,
    val earnedAmount: Double = 40000.0,
    // Dedicated penalty and delay fields per shift
    val penaltyHours: Double = 0.0,     // عقوبات بساعة
    val delayHours: Double = 0.0,       // تأخيريات بساعة
    val fineAmount: Double = 0.0,       // غرامات بدينار
    val isPaidLeave: Boolean = false,   // إجازة اعتيادية مدفوعة أم بدون راتب
    val notes: String = ""
) {
    val nightBonusHours: Double
        get() = if (shiftType == ShiftType.NIGHT) (paidHours - actualHours).coerceAtLeast(0.0) else 0.0

    val nightBonusEarned: Double
        get() = nightBonusHours * hourlyRateSnapshot

    // Net earnings for this single shift after per-shift penalties and fines
    val netShiftEarned: Double
        get() {
            if (shiftType == ShiftType.OFF && !isPaidLeave) return 0.0
            val penaltyDeduction = (penaltyHours + delayHours) * hourlyRateSnapshot
            return (earnedAmount - penaltyDeduction - fineAmount).coerceAtLeast(0.0)
        }
}
