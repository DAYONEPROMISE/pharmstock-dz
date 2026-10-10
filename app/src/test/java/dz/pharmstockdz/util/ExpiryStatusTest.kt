package dz.pharmstockdz.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class ExpiryStatusTest {
    private val today = LocalDate.of(2026, 10, 10)

    @Test
    fun `expired yesterday`() {
        val info = classifyExpiry(today.minusDays(1), today)
        assertEquals(ExpiryStatus.EXPIRED, info.status)
        assertEquals(-1L, info.daysUntilExpiry)
    }

    @Test
    fun `expired long ago`() {
        val info = classifyExpiry(today.minusDays(400), today)
        assertEquals(ExpiryStatus.EXPIRED, info.status)
        assertEquals(-400L, info.daysUntilExpiry)
    }

    @Test
    fun `expires today is critical with zero days`() {
        val info = classifyExpiry(today, today)
        assertEquals(ExpiryStatus.CRITICAL, info.status)
        assertEquals(0L, info.daysUntilExpiry)
    }

    @Test
    fun `expires in 30 days is critical boundary`() {
        val info = classifyExpiry(today.plusDays(30), today)
        assertEquals(ExpiryStatus.CRITICAL, info.status)
    }

    @Test
    fun `expires in 31 days is warning`() {
        val info = classifyExpiry(today.plusDays(31), today)
        assertEquals(ExpiryStatus.WARNING, info.status)
    }

    @Test
    fun `expires in 90 days is warning boundary`() {
        val info = classifyExpiry(today.plusDays(90), today)
        assertEquals(ExpiryStatus.WARNING, info.status)
    }

    @Test
    fun `expires in 91 days is ok`() {
        val info = classifyExpiry(today.plusDays(91), today)
        assertEquals(ExpiryStatus.OK, info.status)
    }

    @Test
    fun `custom thresholds are respected`() {
        val info = classifyExpiry(today.plusDays(10), today, criticalDays = 7, warningDays = 14)
        assertEquals(ExpiryStatus.WARNING, info.status)
    }

    @Test
    fun `epoch day round trip`() {
        val date = LocalDate.of(2027, 3, 15)
        assertEquals(date, epochDayToLocalDate(localDateToEpochDay(date)))
    }

    @Test
    fun `leap day expiry handled`() {
        val info = classifyExpiry(LocalDate.of(2028, 2, 29), LocalDate.of(2028, 2, 28))
        assertEquals(ExpiryStatus.CRITICAL, info.status)
        assertEquals(1L, info.daysUntilExpiry)
    }
}
