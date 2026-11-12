package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.entity.UserSettingsEntity
import com.example.ui.theme.NightMoonGlow

@Composable
fun SettingsDialog(
    currentSettings: UserSettingsEntity,
    onDismiss: () -> Unit,
    onSave: (
        hourlyRate: Double,
        currency: String,
        nightBonusMinutes: Int,
        defaultDuration: Double,
        fixedBonus: Double,
        fixedDeductions: Double
    ) -> Unit
) {
    var hourlyRateInput by remember { mutableStateOf(currentSettings.hourlyRate.toLong().toString()) }
    var selectedCurrency by remember { mutableStateOf(currentSettings.currency) }
    var nightBonusMinutes by remember { mutableStateOf(currentSettings.nightBonusMinutesPerHour.toString()) }
    var defaultDurationInput by remember { mutableStateOf(currentSettings.defaultShiftDurationHours.toString()) }
    var fixedBonusInput by remember { mutableStateOf(currentSettings.monthlyFixedBonus.toLong().toString()) }
    var fixedDeductionsInput by remember { mutableStateOf(currentSettings.monthlyFixedDeductions.toLong().toString()) }

    val commonCurrencies = listOf("د.ع", "$", "ر.س", "ج.م", "د.إ", "د.ك")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("settings_dialog"),
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "إعدادات الراتب والشفتات",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إلغاء")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hourly Rate Input
                OutlinedTextField(
                    value = hourlyRateInput,
                    onValueChange = { hourlyRateInput = it },
                    label = { Text("أجر الساعة الأساسي") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Payments, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("setting_hourly_rate_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Currency Selector
                Text(
                    text = "العملة المستخدمة:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    commonCurrencies.forEach { curr ->
                        val isSelected = selectedCurrency == curr
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedCurrency = curr }
                        ) {
                            Text(
                                text = curr,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Night Shift Extra Minutes Rule
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = NightMoonGlow.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bedtime,
                                contentDescription = null,
                                tint = NightMoonGlow,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "مكافأة الشفت الليلي الإضافية",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = NightMoonGlow
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "حسب طلبك: زيادة ربع ساعة (15 دقيقة) أجر لكل ساعة عمل ليلي. يمكنك التعديل إذا تغير النظام.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = nightBonusMinutes,
                            onValueChange = { nightBonusMinutes = it },
                            label = { Text("دقائق الزيادة لكل ساعة ليلي (افتراضي 15)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("night_bonus_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Default shift hours
                OutlinedTextField(
                    value = defaultDurationInput,
                    onValueChange = { defaultDurationInput = it },
                    label = { Text("ساعات الشفت الافتراضية") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Schedule, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Monthly Allowances & Deductions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = fixedBonusInput,
                        onValueChange = { fixedBonusInput = it },
                        label = { Text("بدلات ثابتة (+)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = fixedDeductionsInput,
                        onValueChange = { fixedDeductionsInput = it },
                        label = { Text("استقطاعات (-)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

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
                            val rate = hourlyRateInput.toDoubleOrNull() ?: 5000.0
                            val nightMins = nightBonusMinutes.toIntOrNull() ?: 15
                            val defDuration = defaultDurationInput.toDoubleOrNull() ?: 8.0
                            val bonus = fixedBonusInput.toDoubleOrNull() ?: 0.0
                            val deductions = fixedDeductionsInput.toDoubleOrNull() ?: 0.0

                            onSave(
                                rate,
                                selectedCurrency,
                                nightMins,
                                defDuration,
                                bonus,
                                deductions
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_settings_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("حفظ التغييرات", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
