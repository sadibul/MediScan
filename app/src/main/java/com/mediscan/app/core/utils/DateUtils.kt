package com.mediscan.app.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Smart date parser — handles various date formats from AI extraction
 * and user input. Returns epoch millis (Long) for Firestore storage,
 * which makes sorting/graphing trivial regardless of input format.
 *
 * Supported formats:
 *   "21 FEB 2026", "21 Feb 2026"
 *   "22-04-2025", "22/04/2025"
 *   "2025-04-22" (ISO)
 *   "22 April 2025"
 *   "April 22, 2025"
 *   "22.04.2025"
 *   "2 1 FEB 2026" (AI sometimes adds spaces in day)
 */
object DateUtils {

    private val FORMATS = listOf(
        "dd MMM yyyy",       // 21 FEB 2026, 21 Feb 2026
        "dd MMMM yyyy",      // 21 February 2026
        "dd-MM-yyyy",        // 22-04-2025
        "dd/MM/yyyy",        // 22/04/2025
        "dd.MM.yyyy",        // 22.04.2025
        "yyyy-MM-dd",        // 2025-04-22 (ISO)
        "MMMM dd, yyyy",     // April 22, 2025
        "MMM dd, yyyy",      // Apr 22, 2025
        "dd-MMM-yyyy",       // 22-Feb-2026
        "MM/dd/yyyy",        // 04/22/2025
        "MM-dd-yyyy",        // 04-22-2025
    )

    /**
     * Parse a date string into epoch millis.
     * Returns null if no format matches.
     */
    fun parseDate(input: String): Long? {
        if (input.isBlank()) return null

        // Clean up: trim, remove extra spaces (AI sometimes gives "2 1 FEB 2026")
        val cleaned = input.trim()
            .replace(Regex("\\s+"), " ")             // collapse multiple spaces
            .replace(Regex("(\\d)\\s(\\d)"), "$1$2") // "2 1" → "21"

        for (format in FORMATS) {
            try {
                val sdf = SimpleDateFormat(format, Locale.ENGLISH)
                sdf.isLenient = false
                val date = sdf.parse(cleaned)
                if (date != null) return date.time
            } catch (_: Exception) {
                // Try next format
            }
        }
        return null
    }

    /**
     * Format epoch millis to a display string: "21 Feb 2026"
     */
    fun formatDate(millis: Long): String {
        return SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(Date(millis))
    }

    /**
     * Format epoch millis for graph X-axis: "Feb 2026"
     */
    fun formatForGraph(millis: Long): String {
        return SimpleDateFormat("MMM yyyy", Locale.ENGLISH).format(Date(millis))
    }

    /**
     * Format epoch millis for short display: "21 Feb"
     */
    fun formatShort(millis: Long): String {
        return SimpleDateFormat("dd MMM", Locale.ENGLISH).format(Date(millis))
    }

    /**
     * Format epoch millis to time: "10:30 AM"
     */
    fun formatTime(millis: Long): String {
        return SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(millis))
    }

    /**
     * Format epoch millis for date + time: "21 Feb 2026, 10:30 AM"
     */
    fun formatDateTime(millis: Long): String {
        return SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH).format(Date(millis))
    }

    /**
     * Check if a date string is in a valid parseable format.
     */
    fun isValidDate(input: String): Boolean {
        return parseDate(input) != null
    }
}
