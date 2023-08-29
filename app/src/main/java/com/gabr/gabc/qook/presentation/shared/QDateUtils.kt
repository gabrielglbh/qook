package com.gabr.gabc.qook.presentation.shared

import com.gabr.gabc.qook.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class QDateUtils {
    companion object {
        fun Date.formatDate(): String {
            val formatted = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
            return formatted.format(this)
        }

        fun getWeekDayStringRes(num: Int): Int {
            return when (num) {
                0 -> R.string.monday
                1 -> R.string.tuesday
                2 -> R.string.wednesday
                3 -> R.string.thursday
                4 -> R.string.friday
                5 -> R.string.saturday
                6 -> R.string.sunday
                else -> throw IllegalArgumentException("Invalid number")
            }
        }

        fun getDayOfWeekIndex(): Int {
            val calendar = Calendar.getInstance(Locale.getDefault())
            return when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> 0
                Calendar.TUESDAY -> 1
                Calendar.WEDNESDAY -> 2
                Calendar.THURSDAY -> 3
                Calendar.FRIDAY -> 4
                Calendar.SATURDAY -> 5
                Calendar.SUNDAY -> 6
                else -> throw IllegalArgumentException("Invalid day of week")
            }
        }
    }
}