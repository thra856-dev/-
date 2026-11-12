package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.entity.MonthlyAdjustmentEntity
import com.example.data.entity.ShiftRecord
import com.example.data.entity.UserSettingsEntity
import com.example.data.model.ShiftType
import com.example.data.repository.ShiftRepository
import com.example.util.DateUtil
import com.example.util.FormatUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DeductionsSummary(
    val penaltyHours: Double = 0.0,
    val penaltyDeduction: Double = 0.0,
    val delayHours: Double = 0.0,
    val delayDeduction: Double = 0.0,
    val finesDinar: Double = 0.0,
    val advancesDinar: Double = 0.0,
    val socialSecurityDinar: Double = 0.0,
    val totalDeductions: Double = 0.0
)

data class MonthlyStats(
    val totalWorkedDays: Int = 0,
    val totalActualHours: Double = 0.0,
    val totalPaidHours: Double = 0.0,
    val grossSalary: Double = 0.0,
    val bonusAmount: Double = 0.0,
    val netSalary: Double = 0.0,
    val morningShiftsCount: Int = 0,
    val morningHours: Double = 0.0,
    val eveningShiftsCount: Int = 0,
    val eveningHours: Double = 0.0,
    val nightShiftsCount: Int = 0,
    val nightHours: Double = 0.0,
    val nightBonusHours: Double = 0.0,
    val nightBonusEarned: Double = 0.0,
    val offDaysCount: Int = 0,
    val paidLeaveDaysCount: Int = 0,
    val unpaidLeaveDaysCount: Int = 0,
    val deductions: DeductionsSummary = DeductionsSummary()
)

data class QuickCalcState(
    val morningHours: String = "40",
    val eveningHours: String = "40",
    val nightHours: String = "40",
    val penaltyHours: String = "0",
    val delayHours: String = "0",
    val finesDinar: String = "0",
    val advancesDinar: String = "0",
    val socialSecurityDinar: String = "0",
    val bonusDinar: String = "0"
)

class ShiftViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val repository = ShiftRepository(
        database.shiftDao(),
        database.settingsDao(),
        database.monthlyAdjustmentDao()
    )

    val settings: StateFlow<UserSettingsEntity> = repository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserSettingsEntity()
    )

    private val _currentMonthPrefix = MutableStateFlow(DateUtil.currentMonthPrefix())
    val currentMonthPrefix: StateFlow<String> = _currentMonthPrefix.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val monthlyShifts: StateFlow<List<ShiftRecord>> = _currentMonthPrefix.flatMapLatest { prefix ->
        repository.getShiftsForMonth(prefix)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val monthlyAdjustments: StateFlow<MonthlyAdjustmentEntity> = _currentMonthPrefix.flatMapLatest { prefix ->
        repository.getAdjustmentsForMonth(prefix)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MonthlyAdjustmentEntity(monthPrefix = DateUtil.currentMonthPrefix())
    )

    val monthlyStats: StateFlow<MonthlyStats> = combine(
        monthlyShifts,
        settings,
        monthlyAdjustments
    ) { shifts, currentSettings, adjustments ->
        var actualHours = 0.0
        var paidHours = 0.0
        var gross = 0.0
        var morningCount = 0
        var morningH = 0.0
        var eveningCount = 0
        var eveningH = 0.0
        var nightCount = 0
        var nightH = 0.0
        var nightBonusH = 0.0
        var nightBonusCash = 0.0
        var offCount = 0
        var paidLeaveCount = 0
        var unpaidLeaveCount = 0

        var shiftPenaltyHours = 0.0
        var shiftDelayHours = 0.0
        var shiftFines = 0.0

        for (shift in shifts) {
            shiftPenaltyHours += shift.penaltyHours
            shiftDelayHours += shift.delayHours
            shiftFines += shift.fineAmount

            when (shift.shiftType) {
                ShiftType.MORNING -> {
                    morningCount++
                    morningH += shift.actualHours
                    actualHours += shift.actualHours
                    paidHours += shift.paidHours
                    gross += shift.earnedAmount
                }
                ShiftType.EVENING -> {
                    eveningCount++
                    eveningH += shift.actualHours
                    actualHours += shift.actualHours
                    paidHours += shift.paidHours
                    gross += shift.earnedAmount
                }
                ShiftType.NIGHT -> {
                    nightCount++
                    nightH += shift.actualHours
                    val bonusH = (shift.paidHours - shift.actualHours).coerceAtLeast(0.0)
                    nightBonusH += bonusH
                    nightBonusCash += bonusH * shift.hourlyRateSnapshot
                    actualHours += shift.actualHours
                    paidHours += shift.paidHours
                    gross += shift.earnedAmount
                }
                ShiftType.OFF -> {
                    offCount++
                    if (shift.isPaidLeave) {
                        paidLeaveCount++
                        actualHours += shift.actualHours
                        paidHours += shift.paidHours
                        gross += shift.earnedAmount
                    } else {
                        unpaidLeaveCount++
                    }
                }
            }
        }

        // Deductions calculation
        val totalPenaltyHours = adjustments.penaltyHours + shiftPenaltyHours
        val penaltyCashDeduction = totalPenaltyHours * currentSettings.hourlyRate

        val totalDelayHours = adjustments.delayHours + shiftDelayHours
        val delayCashDeduction = totalDelayHours * currentSettings.hourlyRate

        val totalFinesDinar = adjustments.finesDinar + shiftFines
        val totalAdvancesDinar = adjustments.advancesDinar
        val socialSecurityDinar = adjustments.socialSecurityDinar

        val totalDeductionsAmount = penaltyCashDeduction +
                delayCashDeduction +
                totalFinesDinar +
                totalAdvancesDinar +
                socialSecurityDinar

        val totalBonusAmount = adjustments.bonusDinar + currentSettings.monthlyFixedBonus

        val net = (gross + totalBonusAmount - totalDeductionsAmount).coerceAtLeast(0.0)

        val totalDaysWorked = morningCount + eveningCount + nightCount

        MonthlyStats(
            totalWorkedDays = totalDaysWorked,
            totalActualHours = actualHours,
            totalPaidHours = paidHours,
            grossSalary = gross,
            bonusAmount = totalBonusAmount,
            netSalary = net,
            morningShiftsCount = morningCount,
            morningHours = morningH,
            eveningShiftsCount = eveningCount,
            eveningHours = eveningH,
            nightShiftsCount = nightCount,
            nightHours = nightH,
            nightBonusHours = nightBonusH,
            nightBonusEarned = nightBonusCash,
            offDaysCount = offCount,
            paidLeaveDaysCount = paidLeaveCount,
            unpaidLeaveDaysCount = unpaidLeaveCount,
            deductions = DeductionsSummary(
                penaltyHours = totalPenaltyHours,
                penaltyDeduction = penaltyCashDeduction,
                delayHours = totalDelayHours,
                delayDeduction = delayCashDeduction,
                finesDinar = totalFinesDinar,
                advancesDinar = totalAdvancesDinar,
                socialSecurityDinar = socialSecurityDinar,
                totalDeductions = totalDeductionsAmount
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MonthlyStats()
    )

    init {
        viewModelScope.launch {
            repository.getSettingsDirect()
            repository.getAdjustmentsDirect(DateUtil.currentMonthPrefix())
        }
    }

    fun selectMonth(prefix: String) {
        _currentMonthPrefix.value = prefix
    }

    fun previousMonth() {
        _currentMonthPrefix.value = DateUtil.getPreviousMonthPrefix(_currentMonthPrefix.value)
    }

    fun nextMonth() {
        _currentMonthPrefix.value = DateUtil.getNextMonthPrefix(_currentMonthPrefix.value)
    }

    fun currentMonth() {
        _currentMonthPrefix.value = DateUtil.currentMonthPrefix()
    }

    fun quickLogToday(shiftType: ShiftType) {
        val today = DateUtil.todayDateString()
        viewModelScope.launch {
            val existing = repository.getShiftForDate(today)
            val currentSettings = settings.value

            val actualHours = if (shiftType == ShiftType.OFF) 0.0 else currentSettings.defaultShiftDurationHours
            val (paidHours, earned) = repository.calculateShiftValues(
                shiftType = shiftType,
                actualHours = actualHours,
                hourlyRate = currentSettings.hourlyRate,
                nightBonusMinutesPerHour = currentSettings.nightBonusMinutesPerHour
            )

            val record = ShiftRecord(
                id = existing?.id ?: 0,
                dateString = today,
                shiftType = shiftType,
                startTime = shiftType.defaultStartTime,
                endTime = shiftType.defaultEndTime,
                breakMinutes = 0,
                actualHours = actualHours,
                paidHours = paidHours,
                hourlyRateSnapshot = currentSettings.hourlyRate,
                earnedAmount = earned,
                penaltyHours = 0.0,
                delayHours = 0.0,
                fineAmount = 0.0,
                isPaidLeave = false,
                notes = if (shiftType == ShiftType.NIGHT) "زيادة ربع ساعة لكل ساعة ليلي (+25%)" else ""
            )

            repository.saveShift(record)
        }
    }

    fun saveShift(
        existingId: Long = 0,
        dateString: String,
        shiftType: ShiftType,
        startTime: String,
        endTime: String,
        breakMinutes: Int,
        customHours: Double?,
        penaltyHours: Double = 0.0,
        delayHours: Double = 0.0,
        fineAmount: Double = 0.0,
        isPaidLeave: Boolean = false,
        notes: String
    ) {
        viewModelScope.launch {
            val currentSettings = settings.value
            val actualHours = customHours ?: if (shiftType == ShiftType.OFF && !isPaidLeave) {
                0.0
            } else {
                DateUtil.calculateHours(startTime, endTime, breakMinutes)
            }

            val (paidHours, earned) = repository.calculateShiftValues(
                shiftType = shiftType,
                actualHours = actualHours,
                hourlyRate = currentSettings.hourlyRate,
                nightBonusMinutesPerHour = currentSettings.nightBonusMinutesPerHour
            )

            val record = ShiftRecord(
                id = existingId,
                dateString = dateString,
                shiftType = shiftType,
                startTime = startTime,
                endTime = endTime,
                breakMinutes = breakMinutes,
                actualHours = actualHours,
                paidHours = paidHours,
                hourlyRateSnapshot = currentSettings.hourlyRate,
                earnedAmount = earned,
                penaltyHours = penaltyHours,
                delayHours = delayHours,
                fineAmount = fineAmount,
                isPaidLeave = isPaidLeave,
                notes = notes
            )
            repository.saveShift(record)
        }
    }

    fun deleteShift(shift: ShiftRecord) {
        viewModelScope.launch {
            repository.deleteShift(shift)
        }
    }

    fun updateMonthlyAdjustments(
        penaltyHours: Double,
        delayHours: Double,
        finesDinar: Double,
        advancesDinar: Double,
        socialSecurityDinar: Double,
        bonusDinar: Double,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val entity = MonthlyAdjustmentEntity(
                monthPrefix = _currentMonthPrefix.value,
                penaltyHours = penaltyHours,
                delayHours = delayHours,
                finesDinar = finesDinar,
                advancesDinar = advancesDinar,
                socialSecurityDinar = socialSecurityDinar,
                bonusDinar = bonusDinar,
                notes = notes
            )
            repository.saveAdjustments(entity)
        }
    }

    fun updateSettings(
        hourlyRate: Double,
        currency: String,
        nightBonusMinutes: Int,
        defaultDuration: Double,
        fixedBonus: Double,
        fixedDeductions: Double
    ) {
        viewModelScope.launch {
            val updated = UserSettingsEntity(
                id = 1,
                hourlyRate = hourlyRate,
                currency = currency,
                nightBonusMinutesPerHour = nightBonusMinutes,
                defaultShiftDurationHours = defaultDuration,
                monthlyFixedBonus = fixedBonus,
                monthlyFixedDeductions = fixedDeductions
            )
            repository.saveSettings(updated)
        }
    }

    fun generateMonthlySummaryReport(): String {
        val currentSettings = settings.value
        val stats = monthlyStats.value
        val d = stats.deductions
        val monthName = DateUtil.formatMonthArabic(currentMonthPrefix.value)

        return buildString {
            appendLine("📋 **كشف حساب الراتب والشفتات - $monthName**")
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine("💵 صافي الراتب المستحق: ${FormatUtil.formatMoney(stats.netSalary, currentSettings.currency)}")
            appendLine("💰 إجمالي أجر الشفتات: ${FormatUtil.formatMoney(stats.grossSalary, currentSettings.currency)}")
            appendLine("⏱️ ساعات العمل الفعلية: ${FormatUtil.formatHours(stats.totalActualHours)}")
            appendLine("📈 الساعات المحتسبة للأجر: ${FormatUtil.formatHours(stats.totalPaidHours)}")
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine("🌙 **شفتات العمل الليلي (+ربع ساعة/ساعة):**")
            appendLine("• عدد الشفتات الليلية: ${stats.nightShiftsCount}")
            appendLine("• ساعات العمل الفعلي: ${FormatUtil.formatHours(stats.nightHours)}")
            appendLine("• ساعات البونص الإضافية: +${FormatUtil.formatHours(stats.nightBonusHours)}")
            appendLine("• المكافأة الإضافية لليلي: +${FormatUtil.formatMoney(stats.nightBonusEarned, currentSettings.currency)}")
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine("☀️ **شفتات العمل الصباحي والمسائي والإجازات:**")
            appendLine("• شفتات صباحية: ${stats.morningShiftsCount} (${FormatUtil.formatHours(stats.morningHours)})")
            appendLine("• شفتات مسائية: ${stats.eveningShiftsCount} (${FormatUtil.formatHours(stats.eveningHours)})")
            appendLine("• أيام الإجازة والراحة: ${stats.offDaysCount} (مدفوعة: ${stats.paidLeaveDaysCount}، بدون راتب: ${stats.unpaidLeaveDaysCount})")
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine("⚠️ **الاستقطاعات والعقوبات والسلف والضمان:**")
            appendLine("• عقوبات بالساعات: ${d.penaltyHours} س = -${FormatUtil.formatMoney(d.penaltyDeduction, currentSettings.currency)}")
            appendLine("• تأخيريات بالساعات: ${d.delayHours} س = -${FormatUtil.formatMoney(d.delayDeduction, currentSettings.currency)}")
            appendLine("• غرامات نقدية: -${FormatUtil.formatMoney(d.finesDinar, currentSettings.currency)}")
            appendLine("• سلف مستقطعة: -${FormatUtil.formatMoney(d.advancesDinar, currentSettings.currency)}")
            appendLine("• كلفة الضمان الاجتماعي: -${FormatUtil.formatMoney(d.socialSecurityDinar, currentSettings.currency)}")
            appendLine("• إجمالي الاستقطاعات: -${FormatUtil.formatMoney(d.totalDeductions, currentSettings.currency)}")
            if (stats.bonusAmount > 0) {
                appendLine("➕ إضافات ومكافآت وبدلات: +${FormatUtil.formatMoney(stats.bonusAmount, currentSettings.currency)}")
            }
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine("⚙️ سعر الساعة المعتمد: ${FormatUtil.formatMoney(currentSettings.hourlyRate, currentSettings.currency)}")
            appendLine("\nتم احتساب الراتب آلياً بدقة عبر تطبيق حساب الراتب والشفتات")
        }
    }
}
