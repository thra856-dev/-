package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.theme.EmeraldGain
import com.example.ui.theme.NightMoonGlow
import com.example.util.FormatUtil

@Composable
fun QuickCalculatorDialog(
    defaultHourlyRate: Double,
    currency: String,
    nightBonusMinutes: Int = 15,
    onDismiss: () -> Unit
) {
    var rateInput by remember { mutableStateOf(defaultHourlyRate.toLong().toString()) }
    var morningHours by remember { mutableStateOf("48") }
    var eveningHours by remember { mutableStateOf("48") }
    var nightHours by remember { mutableStateOf("48") }
    var penaltyHours by remember { mutableStateOf("0") }
    var delayHours by remember { mutableStateOf("0") }
    var finesDinar by remember { mutableStateOf("0") }
    var advancesDinar by remember { mutableStateOf("0") }
    var socialSecurityDinar by remember { mutableStateOf("0") }

    val rate = rateInput.toDoubleOrNull() ?: defaultHourlyRate
    val mH = morningHours.toDoubleOrNull() ?: 0.0
    val eH = eveningHours.toDoubleOrNull() ?: 0.0
    val nH = nightHours.toDoubleOrNull() ?: 0.0
    val pH = penaltyHours.toDoubleOrNull() ?: 0.0
    val dH = delayHours.toDoubleOrNull() ?: 0.0
    val fines = finesDinar.toDoubleOrNull() ?: 0.0
    val advances = advancesDinar.toDoubleOrNull() ?: 0.0
    val ss = socialSecurityDinar.toDoubleOrNull() ?: 0.0

    // Night shift calculations (+15 min / hour = 1.25x)
    val nightBonusFactor = 1.0 + (nightBonusMinutes / 60.0)
    val nPaidH = nH * nightBonusFactor
    val nBonusH = (nPaidH - nH).coerceAtLeast(0.0)

    val totalActualH = mH + eH + nH
    val totalPaidH = mH + eH + nPaidH
    val grossEarnings = totalPaidH * rate
    val nightBonusCash = nBonusH * rate

    val penaltyCash = (pH + dH) * rate
    val totalDeductions = penaltyCash + fines + advances + ss
    val netSalary = (grossEarnings - totalDeductions).coerceAtLeast(0.0)

    // Rough worked days estimate assuming 8 hours per day
    val estimatedDays = if (totalActualH > 0) Math.ceil(totalActualH / 8.0).toInt() else 0

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("quick_calculator_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Calculate,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "حاسبة الراتب والشفتات الفورية",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "محاكاة فورية لحساب الراتب والزيادة والاستقطاعات",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rate Input
                OutlinedTextField(
                    value = rateInput,
                    onValueChange = { rateInput = it },
                    label = { Text("أجر الساعة ($currency)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "ساعات الشفتات:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Hours Inputs: Morning, Evening, Night
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = morningHours,
                        onValueChange = { morningHours = it },
                        label = { Text("صباحي (س)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = eveningHours,
                        onValueChange = { eveningHours = it },
                        label = { Text("مسائي (س)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = nightHours,
                        onValueChange = { nightHours = it },
                        label = { Text("ليلي (س)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "العقوبات، التأخيريات والاستقطاعات:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFFEF4444)
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = penaltyHours,
                        onValueChange = { penaltyHours = it },
                        label = { Text("عقوبة (س)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = delayHours,
                        onValueChange = { delayHours = it },
                        label = { Text("تأخير (س)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedTextField(
                        value = finesDinar,
                        onValueChange = { finesDinar = it },
                        label = { Text("غرامات") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = advancesDinar,
                        onValueChange = { advancesDinar = it },
                        label = { Text("سلف") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = socialSecurityDinar,
                        onValueChange = { socialSecurityDinar = it },
                        label = { Text("الضمان") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Calculated Results Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "أيام الدوام المقدرة:", fontSize = 13.sp)
                            Text(text = "$estimatedDays يوم", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "ساعات العمل الفعلية:", fontSize = 13.sp)
                            Text(text = FormatUtil.formatHours(totalActualH), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "الساعات المحتسبة للأجر:", fontSize = 13.sp)
                            Text(text = FormatUtil.formatHours(totalPaidH), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        if (nBonusH > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "🌙 بونص الشفت الليلي (+ربع ساعة):",
                                    fontSize = 12.sp,
                                    color = NightMoonGlow,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "+${FormatUtil.formatHours(nBonusH)} (+${FormatUtil.formatMoney(nightBonusCash, currency)})",
                                    color = NightMoonGlow,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        if (totalDeductions > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "إجمالي الخصومات:", fontSize = 12.sp, color = Color(0xFFEF4444))
                                Text(
                                    text = "-${FormatUtil.formatMoney(totalDeductions, currency)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFFEF4444)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "صافي الراتب المستحق:", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            Text(
                                text = FormatUtil.formatMoney(netSalary, currency),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = EmeraldGain
                            )
                        }
                    }
                }
            }
        }
    }
}
