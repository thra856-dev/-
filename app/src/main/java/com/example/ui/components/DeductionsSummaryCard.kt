package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.DeductionsSummary
import com.example.util.FormatUtil

@Composable
fun DeductionsSummaryCard(
    deductions: DeductionsSummary,
    currency: String,
    paidLeaveDays: Int,
    unpaidLeaveDays: Int,
    onEditAdjustments: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("deductions_summary_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFF43F5E).copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF43F5E).copy(alpha = 0.12f),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.MoneyOff,
                                contentDescription = null,
                                tint = Color(0xFFF43F5E),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "العقوبات، الاستقطاعات والضمان",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "إجمالي الخصومات: ${FormatUtil.formatMoney(deductions.totalDeductions, currency)}",
                            fontSize = 12.sp,
                            color = Color(0xFFF43F5E),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF43F5E).copy(alpha = 0.1f),
                    modifier = Modifier
                        .clickable { onEditAdjustments() }
                        .testTag("edit_deductions_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "تعديل",
                            tint = Color(0xFFF43F5E),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "تعديل",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF43F5E)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 6-Grid Breakdown requested by user:
            // 1. عقوبات بساعة
            // 2. تأخيريات بساعة
            // 3. غرامات بدينار
            // 4. سلف بدينار
            // 5. كلفة الضمان بدينار
            // 6. الإجازات
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DeductionMiniPill(
                        title = "عقوبات (ساعة)",
                        value = if (deductions.penaltyHours > 0) "${deductions.penaltyHours} س (-${FormatUtil.formatMoney(deductions.penaltyDeduction, currency)})" else "لا يوجد",
                        icon = Icons.Default.Warning,
                        accentColor = Color(0xFFEF4444),
                        modifier = Modifier.weight(1f)
                    )

                    DeductionMiniPill(
                        title = "تأخيريات (ساعة)",
                        value = if (deductions.delayHours > 0) "${deductions.delayHours} س (-${FormatUtil.formatMoney(deductions.delayDeduction, currency)})" else "لا يوجد",
                        icon = Icons.Default.HourglassBottom,
                        accentColor = Color(0xFFF97316),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DeductionMiniPill(
                        title = "غرامات (دينار)",
                        value = if (deductions.finesDinar > 0) FormatUtil.formatMoney(deductions.finesDinar, currency) else "0 $currency",
                        icon = Icons.Default.CreditCard,
                        accentColor = Color(0xFFDC2626),
                        modifier = Modifier.weight(1f)
                    )

                    DeductionMiniPill(
                        title = "سلف (دينار)",
                        value = if (deductions.advancesDinar > 0) FormatUtil.formatMoney(deductions.advancesDinar, currency) else "0 $currency",
                        icon = Icons.Default.AccountBalance,
                        accentColor = Color(0xFF8B5CF6),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DeductionMiniPill(
                        title = "كلفة الضمان (دينار)",
                        value = if (deductions.socialSecurityDinar > 0) FormatUtil.formatMoney(deductions.socialSecurityDinar, currency) else "0 $currency",
                        icon = Icons.Default.Security,
                        accentColor = Color(0xFF0284C7),
                        modifier = Modifier.weight(1f)
                    )

                    DeductionMiniPill(
                        title = "الإجازات",
                        value = "$paidLeaveDays مدفوعة / $unpaidLeaveDays بلا راتب",
                        icon = Icons.Default.EventBusy,
                        accentColor = Color(0xFF64748B),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DeductionMiniPill(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = value,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}
