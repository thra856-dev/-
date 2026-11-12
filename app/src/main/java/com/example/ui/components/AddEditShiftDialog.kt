package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.entity.ShiftRecord
import com.example.data.entity.UserSettingsEntity
import com.example.data.model.ShiftType
import com.example.ui.theme.EmeraldGain
import com.example.ui.theme.NightMoonGlow
import com.example.util.DateUtil
import com.example.util.FormatUtil
import java.time.LocalDate

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditShiftDialog(
    existingShift: ShiftRecord? = null,
    settings: UserSettingsEntity,
    onDismiss: () -> Unit,
    onSave: (
        id: Long,
        dateString: String,
        shiftType: ShiftType,
        startTime: String,
        endTime: String,
        breakMinutes: Int,
        customHours: Double?,
        notes: String
    ) -> Unit
) {
    var selectedType by remember {
        mutableStateOf(existingShift?.shiftType ?: ShiftType.MORNING)
    }
    var dateString by remember {
        mutableStateOf(existingShift?.dateString ?: DateUtil.todayDateString())
    }
    var startTime by remember {
        mutableStateOf(existingShift?.startTime ?: selectedType.defaultStartTime)
    }
    var endTime by remember {
        mutableStateOf(existingShift?.endTime ?: selectedType.defaultEndTime)
    }
    var breakMinutes by remember {
        mutableIntStateOf(existingShift?.breakMinutes ?: 0)
    }
    var hoursInput by remember {
        mutableStateOf(existingShift?.actualHours?.toString() ?: settings.defaultShiftDurationHours.toString())
    }
    var notes by remember {
        mutableStateOf(existingShift?.notes ?: "")
    }

    // Calculated values for live preview
    val parsedHours = hoursInput.toDoubleOrNull() ?: 8.0
    val paidHours = if (selectedType == ShiftType.NIGHT) {
        parsedHours * settings.nightBonusFactor
    } else if (selectedType == ShiftType.OFF) {
        0.0
    } else {
        parsedHours
    }
    val estimatedEarned = paidHours * settings.hourlyRate

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("add_edit_shift_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (existingShift == null) "تسجيل شفت جديد" else "تعديل الشفت",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إلغاء")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Shift Type Selection
                Text(
                    text = "نوع الشفت:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ShiftType.entries.forEach { type ->
                        val isSelected = selectedType == type
                        OutlinedCard(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedType = type
                                    startTime = type.defaultStartTime
                                    endTime = type.defaultEndTime
                                    if (type == ShiftType.OFF) {
                                        hoursInput = "0"
                                    } else if (hoursInput == "0") {
                                        hoursInput = settings.defaultShiftDurationHours.toString()
                                    }
                                },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) type.color else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            ),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = if (isSelected) type.color.copy(alpha = 0.15f) else Color.Transparent
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = type.icon,
                                    contentDescription = type.titleArabic,
                                    tint = if (isSelected) type.color else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = when (type) {
                                        ShiftType.MORNING -> "صباحي"
                                        ShiftType.EVENING -> "مسائي"
                                        ShiftType.NIGHT -> "ليلي"
                                        ShiftType.OFF -> "راحة"
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) type.color else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Night shift special bonus explanation
                if (selectedType == ShiftType.NIGHT) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NightMoonGlow.copy(alpha = 0.12f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = NightMoonGlow,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ميزة الشفت الليلي: يتم احتساب ربع ساعة إضافية (15 دقيقة) أجر عن كل ساعة عمل! أي كل 1 ساعة = 1.25 ساعة أجر.",
                                fontSize = 12.sp,
                                color = NightMoonGlow,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Date Picker field
                OutlinedTextField(
                    value = dateString,
                    onValueChange = { dateString = it },
                    label = { Text("تاريخ الشفت (YYYY-MM-DD)") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                // Quick Date Helpers (اليوم، أمس)
                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val today = DateUtil.todayDateString()
                    val yesterday = LocalDate.now().minusDays(1).toString()
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable { dateString = today }
                    ) {
                        Text(
                            text = "اليوم",
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable { dateString = yesterday }
                    ) {
                        Text(
                            text = "أمس",
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (selectedType != ShiftType.OFF) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Hours Input
                    OutlinedTextField(
                        value = hoursInput,
                        onValueChange = { hoursInput = it },
                        label = { Text("عدد ساعات العمل الفعلية") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.AccessTime, contentDescription = null)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("shift_hours_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    // Quick duration presets
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "خيارات ساعات سريعة:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(6.0, 7.0, 8.0, 10.0, 12.0).forEach { h ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (hoursInput == h.toString()) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { hoursInput = h.toString() }
                            ) {
                                Text(
                                    text = "${h.toInt()} س",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (hoursInput == h.toString()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Start and End time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = { startTime = it },
                            label = { Text("وقت البدء") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = endTime,
                            onValueChange = { endTime = it },
                            label = { Text("وقت الانتهاء") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Notes input
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات (اختياري)") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Notes, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Real-time calculation summary card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "معاينة أجر هذا الشفت:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "ساعات العمل الفعلية:", fontSize = 13.sp)
                            Text(text = FormatUtil.formatHours(parsedHours), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        if (selectedType == ShiftType.NIGHT && parsedHours > 0) {
                            val bonusH = (paidHours - parsedHours).coerceAtLeast(0.0)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "🌙 بونص ليلي (+ربع ساعة):",
                                    fontSize = 13.sp,
                                    color = NightMoonGlow,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "+${FormatUtil.formatHours(bonusH)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = NightMoonGlow
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "إجمالي الساعات المحتسبة:", fontSize = 13.sp)
                                Text(text = FormatUtil.formatHours(paidHours), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "الأجر المستحق للشفت:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                text = FormatUtil.formatMoney(estimatedEarned, settings.currency),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = EmeraldGain
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions: Save / Cancel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("إلغاء")
                    }

                    Button(
                        onClick = {
                            onSave(
                                existingShift?.id ?: 0,
                                dateString,
                                selectedType,
                                startTime,
                                endTime,
                                breakMinutes,
                                parsedHours,
                                notes
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_shift_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("حفظ الشفت", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
