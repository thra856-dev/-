package com.example.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtil {
    private val arabicMonths = arrayOf(
        "يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو",
        "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"
    )

    private val arabicDaysOfWeek = mapOf(
        "MONDAY" to "الإثنين",
        "TUESDAY" to "الثلاثاء",
        "WEDNESDAY" to "الأربعاء",
        "THURSDAY" to "الخميس",
        "FRIDAY" to "الجمعة",
        "SATURDAY" to "السبت",
        "SUNDAY" to "الأحد"
    )

    fun todayDateString(): String {
        return LocalDate.now().toString() // YYYY-MM-DD
    }

    fun currentMonthPrefix(): String {
        val now = LocalDate.now()
        return "%04d-%02d".format(now.year, now.monthValue)
    }

    fun formatMonthArabic(monthPrefix: String): String {
        return try {
            val parts = monthPrefix.split("-")
            val year = parts[0].toInt()
            val month = parts[1].toInt()
            val monthName = arabicMonths.getOrElse(month - 1) { "" }
            "$monthName $year"
        } catch (e: Exception) {
            monthPrefix
        }
    }

    fun getPreviousMonthPrefix(monthPrefix: String): String {
        return try {
            val ym = YearMonth.parse(monthPrefix)
            val prev = ym.minusMonths(1)
            "%04d-%02d".format(prev.year, prev.monthValue)
        } catch (e: Exception) {
            monthPrefix
        }
    }

    fun getNextMonthPrefix(monthPrefix: String): String {
        return try {
            val ym = YearMonth.parse(monthPrefix)
            val next = ym.plusMonths(1)
            "%04d-%02d".format(next.year, next.monthValue)
        } catch (e: Exception) {
            monthPrefix
        }
    }

    fun formatDateArabic(dateString: String): String {
        return try {
            val date = LocalDate.parse(dateString)
            val dayName = arabicDaysOfWeek[date.dayOfWeek.name] ?: ""
            val monthName = arabicMonths[date.monthValue - 1]
            "$dayName، ${date.dayOfMonth} $monthName ${date.year}"
        } catch (e: Exception) {
            dateString
        }
    }

    fun formatShortDate(dateString: String): String {
        return try {
            val date = LocalDate.parse(dateString)
            val dayName = arabicDaysOfWeek[date.dayOfWeek.name] ?: ""
            val monthName = arabicMonths[date.monthValue - 1]
            "$dayName ${date.dayOfMonth} $monthName"
        } catch (e: Exception) {
            dateString
        }
    }

    /**
     * Calculates duration in hours between startTime and endTime in HH:mm format.
     * Accurately handles overnight shifts (e.g. 23:00 to 07:00).
     */
    fun calculateHours(startTime: String, endTime: String, breakMinutes: Int = 0): Double {
        return try {
            val start = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"))
            val end = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"))

            var startMinutes = start.hour * 60 + start.minute
            var endMinutes = end.hour * 60 + end.minute

            if (endMinutes <= startMinutes) {
                // Crosses midnight
                endMinutes += 24 * 60
            }

            val totalWorkingMinutes = (endMinutes - startMinutes) - breakMinutes
            if (totalWorkingMinutes <= 0) 0.0 else totalWorkingMinutes / 60.0
        } catch (e: Exception) {
            8.0 // default fallback
        }
    }
}

object FormatUtil {
    private val symbols = DecimalFormatSymbols(Locale.US)
    private val moneyFormatter = DecimalFormat("#,###.##", symbols)
    private val hoursFormatter = DecimalFormat("0.#", symbols)

    fun formatMoney(amount: Double, currency: String): String {
        return "${moneyFormatter.format(amount)} $currency"
    }

    fun formatHours(hours: Double): String {
        return "${hoursFormatter.format(hours)} س"
    }

    fun formatMinutesToHoursText(minutes: Int): String {
        val h = minutes / 60
        val m = minutes % 60
        return when {
            h > 0 && m > 0 -> "$h س و $m د"
            h > 0 -> "$h س"
            else -> "$m د"
        }
    }
}
