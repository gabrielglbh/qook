package com.gabr.gabc.qook.presentation.shared

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateFormatters {
    companion object {
        fun Date.formatDate(): String {
            val formatted = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
            return formatted.format(this)
        }
    }
}