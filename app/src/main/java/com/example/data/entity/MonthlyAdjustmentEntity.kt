package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores monthly penalties, fines, delays, advances, and social security per month.
 * Specifically requested:
 * - عقوبات بساعة (Penalties in hours)
 * - غرامات بدينار (Fines in dinars)
 * - تأخيريات بساعة (Delays in hours)
 * - سلف بدينار (Salary advances / Loans in dinars)
 * - كلفة الضمان بدينار (Social security cost in dinars)
 * - مكافآت وبدلات إضافية بدينار (Monthly allowances/bonuses)
 */
@Entity(tableName = "monthly_adjustments")
data class MonthlyAdjustmentEntity(
    @PrimaryKey
    val monthPrefix: String, // Format: YYYY-MM e.g. "2026-11"
    val penaltyHours: Double = 0.0,       // عقوبات بساعة
    val delayHours: Double = 0.0,         // تأخيريات بساعة
    val finesDinar: Double = 0.0,         // غرامات بدينار
    val advancesDinar: Double = 0.0,      // سلف بدينار
    val socialSecurityDinar: Double = 0.0,// كلفة الضمان بدينار
    val bonusDinar: Double = 0.0,         // مكافآت إضافية بدينار
    val notes: String = ""
)
