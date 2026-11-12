package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material.icons.filled.WbSunny
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.UserSettingsEntity
import com.example.ui.theme.EveningPurple
import com.example.ui.theme.MorningAmber
import com.example.ui.theme.NightMoonGlow
import com.example.ui.viewmodel.MonthlyStats
import com.example.util.FormatUtil

@Composable
fun ShiftBreakdownCards(
    stats: MonthlyStats,
    settings: UserSettingsEntity,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "توزيع الشفتات لهذا الشهر",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Morning Card
            ShiftStatTile(
                title = "صباحي",
                count = stats.morningShiftsCount,
                hours = stats.morningHours,
                icon = Icons.Default.WbSunny,
                color = MorningAmber,
                bgColor = Color(0xFFFEF3C7),
                modifier = Modifier.weight(1f)
            )

            // Evening Card
            ShiftStatTile(
                title = "مسائي",
                count = stats.eveningShiftsCount,
                hours = stats.eveningHours,
                icon = Icons.Default.Brightness5,
                color = EveningPurple,
                bgColor = Color(0xFFEDE9FE),
                modifier = Modifier.weight(1f)
            )

            // Night Card
            ShiftStatTile(
                title = "ليلي",
                count = stats.nightShiftsCount,
                hours = stats.nightHours,
                extraBadge = if (stats.nightBonusHours > 0) "+${FormatUtil.formatHours(stats.nightBonusHours)}" else null,
                icon = Icons.Default.Bedtime,
                color = NightMoonGlow,
                bgColor = Color(0xFFE0E7FF),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ShiftStatTile(
    title: String,
    count: Int,
    hours: Double,
    icon: ImageVector,
    color: Color,
    bgColor: Color,
    extraBadge: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = bgColor
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier
                            .padding(6.dp)
                            .size(16.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = color.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "$count شفت",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = color,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = FormatUtil.formatHours(hours),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (extraBadge != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF818CF8).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "بونص: $extraBadge",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NightMoonGlow,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
