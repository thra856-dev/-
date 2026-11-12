package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldGain
import com.example.ui.theme.EveningPurple
import com.example.ui.theme.MorningAmber
import com.example.ui.theme.NightMoonGlow
import com.example.ui.viewmodel.MonthlyStats
import com.example.util.FormatUtil
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

data class PieSlice(
    val id: String,
    val title: String,
    val value: Double,
    val color: Color
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SalaryPieChartCard(
    stats: MonthlyStats,
    currency: String,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: ملخص الراتب مقابل الخصومات, 1: تفصيلي شامل
    var selectedSliceIndex by remember { mutableIntStateOf(-1) }

    // Build slices according to selected tab
    val d = stats.deductions
    val slices = remember(stats, selectedTab) {
        if (selectedTab == 0) {
            // High-level: Net Salary vs Total Deductions
            listOfNotNull(
                if (stats.netSalary > 0) PieSlice("net", "صافي الراتب المستحق", stats.netSalary, EmeraldGain) else null,
                if (d.penaltyDeduction + d.delayDeduction > 0) {
                    PieSlice("penalties", "عقوبات وتأخيرات", d.penaltyDeduction + d.delayDeduction, Color(0xFFF97316))
                } else null,
                if (d.finesDinar > 0) PieSlice("fines", "غرامات بدينار", d.finesDinar, Color(0xFFDC2626)) else null,
                if (d.advancesDinar > 0) PieSlice("advances", "سلف مستقطعة", d.advancesDinar, Color(0xFF8B5CF6)) else null,
                if (d.socialSecurityDinar > 0) PieSlice("ss", "كلفة الضمان", d.socialSecurityDinar, Color(0xFF0284C7)) else null
            )
        } else {
            // Detailed: Morning, Evening, Night (Base + Bonus) vs each deduction
            val morningEarned = stats.morningHours * (stats.grossSalary / (if (stats.totalPaidHours > 0) stats.totalPaidHours else 1.0))
            val eveningEarned = stats.eveningHours * (stats.grossSalary / (if (stats.totalPaidHours > 0) stats.totalPaidHours else 1.0))
            val nightBaseEarned = stats.nightHours * (stats.grossSalary / (if (stats.totalPaidHours > 0) stats.totalPaidHours else 1.0))
            listOfNotNull(
                if (morningEarned > 0) PieSlice("m", "دوام صباحي", morningEarned, MorningAmber) else null,
                if (eveningEarned > 0) PieSlice("e", "دوام مسائي", eveningEarned, EveningPurple) else null,
                if (nightBaseEarned > 0) PieSlice("n", "دوام ليلي أساسي", nightBaseEarned, Color(0xFF38BDF8)) else null,
                if (stats.nightBonusEarned > 0) PieSlice("nb", "بونص ليلي (+ربع س)", stats.nightBonusEarned, NightMoonGlow) else null,
                if (d.totalDeductions > 0) PieSlice("ded", "إجمالي الاستقطاعات", d.totalDeductions, Color(0xFFF43F5E)) else null
            )
        }
    }

    val totalValue = remember(slices) { slices.sumOf { it.value } }

    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(slices) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing)
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("salary_pie_chart_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PieChart,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "مخطط الراتب والاستقطاعات",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "رسم بياني تفاعلي يوضح نسبة الاستحقاق والخصم",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Switcher Tabs
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    val tab0Selected = selectedTab == 0
                    val tab1Selected = selectedTab == 1

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (tab0Selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedTab = 0
                                selectedSliceIndex = -1
                            }
                    ) {
                        Text(
                            text = "الراتب مقابل الخصومات",
                            color = if (tab0Selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                            fontWeight = if (tab0Selected) FontWeight.Bold else FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (tab1Selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedTab = 1
                                selectedSliceIndex = -1
                            }
                    ) {
                        Text(
                            text = "تفصيل الشفتات والبونص",
                            color = if (tab1Selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                            fontWeight = if (tab1Selected) FontWeight.Bold else FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (totalValue <= 0.0) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "لا توجد مبالغ أو ساعات مسجلة بعد لعرض المخطط",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            } else {
                // Interactive Donut Chart Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier
                            .size(200.dp)
                            .testTag("interactive_pie_chart_canvas")
                            .pointerInput(slices, totalValue) {
                                detectTapGestures { tapOffset ->
                                    val center = Offset(size.width / 2f, size.height / 2f)
                                    val dx = tapOffset.x - center.x
                                    val dy = tapOffset.y - center.y
                                    var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                                    if (angle < 0) angle += 360f

                                    // Rotate angle by +90 because chart starts at -90deg (top)
                                    var chartAngle = (angle + 90f) % 360f

                                    var currentAngle = 0f
                                    var matchedIndex = -1
                                    for (i in slices.indices) {
                                        val sweep = ((slices[i].value / totalValue) * 360f).toFloat()
                                        if (chartAngle >= currentAngle && chartAngle <= currentAngle + sweep) {
                                            matchedIndex = i
                                            break
                                        }
                                        currentAngle += sweep
                                    }
                                    selectedSliceIndex = if (selectedSliceIndex == matchedIndex) -1 else matchedIndex
                                }
                            }
                    ) {
                        val strokeWidth = 36.dp.toPx()
                        val selectedExtra = 8.dp.toPx()
                        val canvasSize = size.minDimension
                        val radius = (canvasSize - strokeWidth - selectedExtra) / 2f
                        val arcSize = Size(radius * 2, radius * 2)
                        val topLeft = Offset((size.width - radius * 2) / 2f, (size.height - radius * 2) / 2f)

                        var startAngle = -90f
                        val animatedProgress = animationProgress.value

                        slices.forEachIndexed { index, slice ->
                            val sweepAngle = ((slice.value / totalValue) * 360f).toFloat() * animatedProgress
                            val isSelected = index == selectedSliceIndex
                            val currentStroke = if (isSelected) strokeWidth + selectedExtra else strokeWidth

                            drawArc(
                                color = slice.color,
                                startAngle = startAngle + 1.5f,
                                sweepAngle = (sweepAngle - 3f).coerceAtLeast(0.1f),
                                useCenter = false,
                                topLeft = if (isSelected) topLeft - Offset(selectedExtra / 2, selectedExtra / 2) else topLeft,
                                size = if (isSelected) Size(arcSize.width + selectedExtra, arcSize.height + selectedExtra) else arcSize,
                                style = Stroke(width = currentStroke, cap = StrokeCap.Round)
                            )

                            startAngle += sweepAngle
                        }
                    }

                    // Center Interactive Label
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        if (selectedSliceIndex in slices.indices) {
                            val active = slices[selectedSliceIndex]
                            val pct = (active.value / totalValue) * 100.0
                            Text(
                                text = active.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = active.color,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = FormatUtil.formatMoney(active.value, currency),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "${String.format("%.1f", pct)}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = "المجموع الكلي",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = FormatUtil.formatMoney(totalValue, currency),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "اضغط للمعاينة",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive Legend Chips
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    slices.forEachIndexed { index, slice ->
                        val isSelected = index == selectedSliceIndex
                        val percentage = (slice.value / totalValue) * 100.0

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) slice.color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) slice.color else slice.color.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.clickable {
                                selectedSliceIndex = if (selectedSliceIndex == index) -1 else index
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(slice.color, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = slice.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${String.format("%.0f", percentage)}%",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = slice.color
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
