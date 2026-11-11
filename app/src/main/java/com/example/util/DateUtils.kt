package com.example.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    fun getCurrentDateString(): String {
        return dateFormat.format(Date())
    }

    fun formatDate(timestamp: Long): String {
        return displayFormat.format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long): String {
        return dateTimeFormat.format(Date(timestamp))
    }

    fun formatDisplayDate(dateStr: String): String {
        return try {
            val parsed = dateFormat.parse(dateStr)
            if (parsed != null) displayFormat.format(parsed) else dateStr
        } catch (_: Exception) {
            dateStr
        }
    }

    /**
     * Calculates days remaining until expiry.
     * Negative value means already expired.
     */
    fun calculateDaysRemaining(expiryDateStr: String): Long {
        return try {
            val expiryDate = dateFormat.parse(expiryDateStr) ?: return 999L
            val todayStr = getCurrentDateString()
            val todayDate = dateFormat.parse(todayStr) ?: Date()
            val diffInMillis = expiryDate.time - todayDate.time
            TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)
        } catch (_: Exception) {
            999L
        }
    }

    fun formatCurrency(amount: Double): String {
        return String.format(Locale.getDefault(), "₹%.2f", amount)
    }
}
