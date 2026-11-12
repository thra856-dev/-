package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.entity.MonthlyAdjustmentEntity
import com.example.util.FormatUtil

@Composable
fun AdjustmentsDialog(
    currentAdjustments: MonthlyAdjustmentEntity,
    hourlyRate: Double,
    currency: String,
    onDismiss: () -> Unit,
    onSave: (
        penaltyHours: Double,
        delayHours: Double,
        finesDinar: Double,
        advancesDinar: Double,
        socialSecurityDinar: Double,
        bonusDinar: Double,
        notes: String
    ) -> Unit
) {
    var penaltyHoursText by remember { mutableStateOf(if (currentAdjustments.penaltyHours > 0) currentAdjustments.penaltyHours.toString() else "") }
    var delayHoursText by remember { mutableStateOf(if (currentAdjustments.delayHours > 0) currentAdjustments.delayHours.toString() else "") }
    var finesText by remember { mutableStateOf(if (currentAdjustments.finesDinar > 0) currentAdjustments.finesDinar.toLong().toString() else "") }
    var advancesText by remember { mutableStateOf(if (currentAdjustments.advancesDinar > 0) currentAdjustments.advancesDinar.toLong().toString() else "") }
    var socialSecurityText by remember { mutableStateOf(if (currentAdjustments.socialSecurityDinar > 0) currentAdjustments.socialSecurityDinar.toLong().toString() else "") }
    var bonusText by remember { mutableStateOf(if (currentAdjustments.bonusDinar > 0) currentAdjustments.bonusDinar.toLong().toString() else "") }
    var notesText by remember { mutableStateOf(currentAdjustments.notes) }

    // Live preview of deductions
    val pHours = penaltyHoursText.toDoubleOrNull() ?: 0.0
    val dHours = delayHoursText.toDoubleOrNull() ?: 0.0
    val fines = finesText.toDoubleOrNull() ?: 0.0
    val advances = advancesText.toDoubleOrNull() ?: 0.0
    val ss = socialSecurityText.toDoubleOrNull() ?: 0.0
    val bonus = bonusText.toDoubleOrNull() ?: 0.0

    val hoursCashDeduction = (pHours + dHours) * hourlyRate
    val totalDirectDeductions = hoursCashDeduction + fines + advances + ss

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("adjustments_dialog"),
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
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF43F5E).copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.MoneyOff,
                                    contentDescription = null,
                                    tint = Color(0xFFF43F5E),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "العقوبات، السلف والضمان",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "تسجيل الخصومات والاستقطاعات لهذا الشهر",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إلغاء")
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 1. العقوبات بساعة (Penalties in hours)
                OutlinedTextField(
                    value = penaltyHoursText,
                    onValueChange = { penaltyHoursText = it },
                    label = { Text("عقوبات بالساعات (ساعة)") },
                    placeholder = { Text("مثال: 2") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("penalty_hours_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 2. التأخيريات بساعة (Delays in hours)
                OutlinedTextField(
                    value = delayHoursText,
                    onValueChange = { delayHoursText = it },
                    label = { Text("تأخيريات بالساعات (ساعة)") },
                    placeholder = { Text("مثال: 1.5") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.HourglassBottom, contentDescription = null, tint = Color(0xFFF97316))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("delay_hours_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 3. الغرامات بدينار (Fines in dinars)
                OutlinedTextField(
                    value = finesText,
                    onValueChange = { finesText = it },
                    label = { Text("غرامات مالية ($currency)") },
                    placeholder = { Text("مثال: 25000") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.CreditCard, contentDescription = null, tint = Color(0xFFDC2626))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fines_dinar_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 4. السلف بدينار (Advances in dinars)
                OutlinedTextField(
                    value = advancesText,
                    onValueChange = { advancesText = it },
                    label = { Text("سلف مستقطعة ($currency)") },
                    placeholder = { Text("مثال: 100000") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null, tint = Color(0xFF8B5CF6))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("advances_dinar_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 5. كلفة الضمان بدينار (Social security in dinars)
                OutlinedTextField(
                    value = socialSecurityText,
                    onValueChange = { socialSecurityText = it },
                    label = { Text("كلفة الضمان الاجتماعي ($currency)") },
                    placeholder = { Text("مثال: 35000") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = Color(0xFF0284C7))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("social_security_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 6. مكافآت وبدلات إضافية بدينار (Bonus in dinars)
                OutlinedTextField(
                    value = bonusText,
                    onValueChange = { bonusText = it },
                    label = { Text("مكافآت وبدلات إضافية (+) ($currency)") },
                    placeholder = { Text("مثال: 50000") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Deductions live preview box
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFF43F5E).copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "خصم ساعات العقوبة والتأخير:", fontSize = 12.sp)
                            Text(
                                text = "-${FormatUtil.formatMoney(hoursCashDeduction, currency)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFFEF4444)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "إجمالي الخصومات المستقطعة:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                text = "-${FormatUtil.formatMoney(totalDirectDeductions, currency)}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = Color(0xFFF43F5E)
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
                                pHours,
                                dHours,
                                fines,
                                advances,
                                ss,
                                bonus,
                                notesText
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("save_adjustments_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تطبيق وحفظ", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
