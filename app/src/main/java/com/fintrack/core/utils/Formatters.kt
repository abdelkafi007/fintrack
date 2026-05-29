package com.fintrack.core.utils

import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Currency
import java.util.Locale

/**
 * Utility for formatting currency amounts.
 */
object CurrencyFormatter {
    fun format(amount: Double, currencyCode: String = "USD", locale: Locale = Locale.getDefault()): String {
        val formatter = NumberFormat.getCurrencyInstance(locale)
        try {
            formatter.currency = Currency.getInstance(currencyCode)
        } catch (_: Exception) {
            // Fallback to default currency
        }
        return formatter.format(amount)
    }

    fun formatCompact(amount: Double): String {
        return when {
            amount >= 1_000_000 -> String.format("%.1fM", amount / 1_000_000)
            amount >= 1_000 -> String.format("%.1fK", amount / 1_000)
            else -> String.format("%.2f", amount)
        }
    }
}

/**
 * Utility for formatting dates.
 */
object DateFormatter {
    private val fullFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
    private val shortFormatter = DateTimeFormatter.ofPattern("MMM dd")
    private val monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    private val dayOfWeekFormatter = DateTimeFormatter.ofPattern("EEE, MMM dd")

    fun formatFull(date: LocalDate): String = date.format(fullFormatter)
    fun formatShort(date: LocalDate): String = date.format(shortFormatter)
    fun formatMonthYear(date: LocalDate): String = date.format(monthYearFormatter)
    fun formatDayOfWeek(date: LocalDate): String = date.format(dayOfWeekFormatter)

    fun getRelativeDate(date: LocalDate): String {
        val today = LocalDate.now()
        return when (date) {
            today -> "Today"
            today.minusDays(1) -> "Yesterday"
            today.plusDays(1) -> "Tomorrow"
            else -> formatDayOfWeek(date)
        }
    }
}

/**
 * Utility for getting month date ranges.
 */
object DateRangeHelper {
    fun getCurrentMonthRange(): Pair<LocalDate, LocalDate> {
        val now = LocalDate.now()
        val start = now.withDayOfMonth(1)
        val end = now.withDayOfMonth(now.lengthOfMonth())
        return Pair(start, end)
    }

    fun getLastMonthRange(): Pair<LocalDate, LocalDate> {
        val now = LocalDate.now().minusMonths(1)
        val start = now.withDayOfMonth(1)
        val end = now.withDayOfMonth(now.lengthOfMonth())
        return Pair(start, end)
    }

    fun getCurrentWeekRange(firstDayOfWeek: java.time.DayOfWeek = java.time.DayOfWeek.MONDAY): Pair<LocalDate, LocalDate> {
        val now = LocalDate.now()
        val start = now.with(java.time.temporal.TemporalAdjusters.previousOrSame(firstDayOfWeek))
        val end = start.plusDays(6)
        return Pair(start, end)
    }
}
