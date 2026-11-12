package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.EveningPurple
import com.example.ui.theme.MorningAmber
import com.example.ui.theme.NightMoonGlow
import com.example.ui.theme.OffGray

enum class ShiftType(
    val titleArabic: String,
    val subtitleArabic: String,
    val defaultStartTime: String,
    val defaultEndTime: String,
    val color: Color,
    val isNight: Boolean = false
) {
    MORNING(
        titleArabic = "شفت صباحي",
        subtitleArabic = "دوام نهاري اعتيادي",
        defaultStartTime = "08:00",
        defaultEndTime = "16:00",
        color = MorningAmber,
        isNight = false
    ),
    EVENING(
        titleArabic = "شفت مسائي",
        subtitleArabic = "دوام بعد الظهر والمساء",
        defaultStartTime = "16:00",
        defaultEndTime = "00:00",
        color = EveningPurple,
        isNight = false
    ),
    NIGHT(
        titleArabic = "شفت ليلي",
        subtitleArabic = "زيادة ربع ساعة لكل ساعة عمل (+25%)",
        defaultStartTime = "00:00",
        defaultEndTime = "08:00",
        color = NightMoonGlow,
        isNight = true
    ),
    OFF(
        titleArabic = "يوم راحة / إجازة",
        subtitleArabic = "يوم عطلة بدون ساعات عمل",
        defaultStartTime = "00:00",
        defaultEndTime = "00:00",
        color = OffGray,
        isNight = false
    );

    val icon: ImageVector
        get() = when (this) {
            MORNING -> Icons.Default.WbSunny
            EVENING -> Icons.Default.Brightness5
            NIGHT -> Icons.Default.Bedtime
            OFF -> Icons.Default.Weekend
        }
}
