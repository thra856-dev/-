package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ShiftType
import com.example.ui.theme.EveningPurple
import com.example.ui.theme.MorningAmber
import com.example.ui.theme.NightMoonGlow
import com.example.ui.theme.OffGray

@Composable
fun QuickShiftLogger(
    onQuickLog: (ShiftType) -> Unit,
    onCustomAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("quick_shift_logger"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "تسجيل دوام اليوم سريعاً",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.clickable { onCustomAdd() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "مخصص",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "تحديد ساعات",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3 main shift buttons + 1 off button in a responsive 2x2 or 4-item row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickShiftButton(
                    title = "صباحي",
                    badge = "8 س",
                    icon = Icons.Default.WbSunny,
                    tint = MorningAmber,
                    bgColor = Color(0xFFFEF3C7),
                    modifier = Modifier.weight(1f),
                    testTag = "quick_morning_button",
                    onClick = { onQuickLog(ShiftType.MORNING) }
                )

                QuickShiftButton(
                    title = "مسائي",
                    badge = "8 س",
                    icon = Icons.Default.Brightness5,
                    tint = EveningPurple,
                    bgColor = Color(0xFFEDE9FE),
                    modifier = Modifier.weight(1f),
                    testTag = "quick_evening_button",
                    onClick = { onQuickLog(ShiftType.EVENING) }
                )

                QuickShiftButton(
                    title = "ليلي",
                    badge = "+15د/س",
                    icon = Icons.Default.Bedtime,
                    tint = NightMoonGlow,
                    bgColor = Color(0xFFE0E7FF),
                    isSpecial = true,
                    modifier = Modifier.weight(1f),
                    testTag = "quick_night_button",
                    onClick = { onQuickLog(ShiftType.NIGHT) }
                )

                QuickShiftButton(
                    title = "راحة",
                    badge = "إجازة",
                    icon = Icons.Default.Weekend,
                    tint = OffGray,
                    bgColor = Color(0xFFF1F5F9),
                    modifier = Modifier.weight(1f),
                    testTag = "quick_off_button",
                    onClick = { onQuickLog(ShiftType.OFF) }
                )
            }
        }
    }
}

@Composable
private fun QuickShiftButton(
    title: String,
    badge: String,
    icon: ImageVector,
    tint: Color,
    bgColor: Color,
    modifier: Modifier = Modifier,
    isSpecial: Boolean = false,
    testTag: String,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = modifier
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = bgColor.copy(alpha = 0.5f)
        ),
        border = BorderStroke(
            width = if (isSpecial) 1.5.dp else 1.dp,
            color = if (isSpecial) tint else tint.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = tint.copy(alpha = 0.2f)
            ) {
                Text(
                    text = badge,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = tint,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
