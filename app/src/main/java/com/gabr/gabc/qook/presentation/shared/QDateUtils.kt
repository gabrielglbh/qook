package com.gabr.gabc.qook.presentation.shared

import com.gabr.gabc.qook.R
import java.text.SimpleDateFormat
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
    }
}