package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.ShiftRecord
import com.example.ui.components.AddEditShiftDialog
import com.example.ui.components.AdjustmentsDialog
import com.example.ui.components.DeductionsSummaryCard
import com.example.ui.components.MonthlyReportDialog
import com.example.ui.components.QuickCalculatorDialog
import com.example.ui.components.QuickShiftLogger
import com.example.ui.components.SalaryHeroCard
import com.example.ui.components.SalaryPieChartCard
import com.example.ui.components.SettingsDialog
import com.example.ui.components.ShiftBreakdownCards
import com.example.ui.components.ShiftItemCard
import com.example.ui.theme.EmeraldGain
import com.example.ui.theme.NightMoonGlow
import com.example.ui.viewmodel.ShiftViewModel
import com.example.util.DateUtil
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ShiftViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val currentMonthPrefix by viewModel.currentMonthPrefix.collectAsStateWithLifecycle()
    val monthlyShifts by viewModel.monthlyShifts.collectAsStateWithLifecycle()
    val monthlyStats by viewModel.monthlyStats.collectAsStateWithLifecycle()
    val monthlyAdjustments by viewModel.monthlyAdjustments.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostStateStateWrapper() }
    val scope = rememberCoroutineScope()

    // Dialog visibility states
    var showAddDialog by remember { mutableStateOf(false) }
    var editingShift by remember { mutableStateOf<ShiftRecord?>(null) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showAdjustmentsDialog by remember { mutableStateOf(false) }
    var showCalculatorDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = NightMoonGlow.copy(alpha = 0.15f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Nightlight,
                                    contentDescription = null,
                                    tint = NightMoonGlow,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "حساب الراتب والشفتات",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showCalculatorDialog = true },
                        modifier = Modifier.testTag("open_calculator_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = "الحاسبة الفورية",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { showSettingsDialog = true },
                        modifier = Modifier.testTag("open_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "الإعدادات",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingShift = null
                    showAddDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.testTag("add_shift_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة شفت")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "تسجيل شفت", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Month Selector Bar (< Month Year >)
            item {
                MonthSelectorBar(
                    monthTitle = DateUtil.formatMonthArabic(currentMonthPrefix),
                    onPrev = { viewModel.previousMonth() },
                    onNext = { viewModel.nextMonth() },
                    onCurrent = { viewModel.currentMonth() }
                )
            }

            // 1. Master Hero Card (Net Salary, days worked, actual vs paid hours, night bonus gain)
            item {
                SalaryHeroCard(
                    stats = monthlyStats,
                    settings = settings,
                    monthName = DateUtil.formatMonthArabic(currentMonthPrefix)
                )
            }

            // 2. Quick Today's Shift Logger (Morning, Evening, Night +15m/h, Off)
            item {
                QuickShiftLogger(
                    onQuickLog = { shiftType ->
                        viewModel.quickLogToday(shiftType)
                    },
                    onCustomAdd = {
                        editingShift = null
                        showAddDialog = true
                    }
                )
            }

            // 3. User Requested Deductions Card: Penalties, delays, fines, advances, social security, leaves
            item {
                DeductionsSummaryCard(
                    deductions = monthlyStats.deductions,
                    currency = settings.currency,
                    paidLeaveDays = monthlyStats.paidLeaveDaysCount,
                    unpaidLeaveDays = monthlyStats.unpaidLeaveDaysCount,
                    onEditAdjustments = { showAdjustmentsDialog = true }
                )
            }

            // 4. Shift Types Distribution (Morning, Evening, Night)
            item {
                ShiftBreakdownCards(
                    stats = monthlyStats,
                    settings = settings
                )
            }

            // 5. Interactive Pie Chart (Salary vs Deductions)
            item {
                SalaryPieChartCard(
                    stats = monthlyStats,
                    currency = settings.currency
                )
            }

            // 5. Section Header for Shift Log List + Share Report button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "سجل الشفتات (${monthlyShifts.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (monthlyShifts.isNotEmpty()) {
                        OutlinedButton(
                            onClick = { showReportDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("open_report_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("كشف الحساب", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 6. Shifts List or Empty State
            if (monthlyShifts.isEmpty()) {
                item {
                    EmptyShiftsCard(
                        onAddShift = {
                            editingShift = null
                            showAddDialog = true
                        }
                    )
                }
            } else {
                items(monthlyShifts, key = { it.id }) { shift ->
                    ShiftItemCard(
                        shift = shift,
                        currency = settings.currency,
                        onEdit = {
                            editingShift = it
                            showAddDialog = true
                        },
                        onDelete = {
                            viewModel.deleteShift(it)
                        }
                    )
                }
            }
        }
    }

    // Add / Edit Shift Dialog
    if (showAddDialog) {
        AddEditShiftDialog(
            existingShift = editingShift,
            settings = settings,
            onDismiss = { showAddDialog = false },
            onSave = { id, dateString, shiftType, startTime, endTime, breakMinutes, customHours, notes ->
                viewModel.saveShift(
                    existingId = id,
                    dateString = dateString,
                    shiftType = shiftType,
                    startTime = startTime,
                    endTime = endTime,
                    breakMinutes = breakMinutes,
                    customHours = customHours,
                    notes = notes
                )
                showAddDialog = false
            }
        )
    }

    // Adjustments Dialog (Penalties in hours, delays, fines, advances, social security)
    if (showAdjustmentsDialog) {
        AdjustmentsDialog(
            currentAdjustments = monthlyAdjustments,
            hourlyRate = settings.hourlyRate,
            currency = settings.currency,
            onDismiss = { showAdjustmentsDialog = false },
            onSave = { pHours, dHours, fines, advances, ss, bonus, notes ->
                viewModel.updateMonthlyAdjustments(
                    penaltyHours = pHours,
                    delayHours = dHours,
                    finesDinar = fines,
                    advancesDinar = advances,
                    socialSecurityDinar = ss,
                    bonusDinar = bonus,
                    notes = notes
                )
                showAdjustmentsDialog = false
            }
        )
    }

    // Settings Dialog
    if (showSettingsDialog) {
        SettingsDialog(
            currentSettings = settings,
            onDismiss = { showSettingsDialog = false },
            onSave = { rate, curr, nightBonus, defDur, bonus, deductions ->
                viewModel.updateSettings(
                    hourlyRate = rate,
                    currency = curr,
                    nightBonusMinutes = nightBonus,
                    defaultDuration = defDur,
                    fixedBonus = bonus,
                    fixedDeductions = deductions
                )
                showSettingsDialog = false
            }
        )
    }

    // Quick Calculator Dialog
    if (showCalculatorDialog) {
        QuickCalculatorDialog(
            defaultHourlyRate = settings.hourlyRate,
            currency = settings.currency,
            nightBonusMinutes = settings.nightBonusMinutesPerHour,
            onDismiss = { showCalculatorDialog = false }
        )
    }

    // Monthly Report Statement Dialog
    if (showReportDialog) {
        MonthlyReportDialog(
            reportText = viewModel.generateMonthlySummaryReport(),
            onDismiss = { showReportDialog = false }
        )
    }
}

@Composable
private fun MonthSelectorBar(
    monthTitle: String,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onCurrent: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNext,
                modifier = Modifier.testTag("next_month_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "الشهر التالي"
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.testTag("current_month_badge")
            ) {
                Text(
                    text = monthTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            IconButton(
                onClick = onPrev,
                modifier = Modifier.testTag("prev_month_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "الشهر السابق"
                )
            }
        }
    }
}

@Composable
private fun EmptyShiftsCard(onAddShift: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.EventNote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "لا توجد شفتات مسجلة لهذا الشهر",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "سجل دوامك اليوم بضغطة زر أعلاه أو أضف شفت جديد لحساب الراتب تلقائياً",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

private class SnackbarHostStateStateWrapper {
    val hostState = SnackbarHostState()
}
