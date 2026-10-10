package dz.pharmstockdz.util

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** Expiry bucket for a stock lot. Thresholds are in days. */
enum class ExpiryStatus {
    EXPIRED,
    CRITICAL,
    WARNING,
    OK
}

data class ExpiryInfo(val status: ExpiryStatus, val daysUntilExpiry: Long)

/**
 * Pure, testable expiry classifier. All date math lives here so the UI,
 * the worker and the tests share one implementation.
 */
fun classifyExpiry(
    expiryDate: LocalDate,
    today: LocalDate = LocalDate.now(),
    criticalDays: Long = 30,
    warningDays: Long = 90
): ExpiryInfo {
    val days = ChronoUnit.DAYS.between(today, expiryDate)
    val status = when {
        days < 0 -> ExpiryStatus.EXPIRED
        days <= criticalDays -> ExpiryStatus.CRITICAL
        days <= warningDays -> ExpiryStatus.WARNING
        else -> ExpiryStatus.OK
    }
    return ExpiryInfo(status, days)
}

fun epochDayToLocalDate(epochDay: Long): LocalDate = LocalDate.ofEpochDay(epochDay)

fun localDateToEpochDay(date: LocalDate): Long = date.toEpochDay()
