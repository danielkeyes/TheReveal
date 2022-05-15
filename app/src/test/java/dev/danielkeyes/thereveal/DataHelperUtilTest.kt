package dev.danielkeyes.thereveal

import dev.danielkeyes.thereveal.util.DateHelperUtil.getLongMonth
import dev.danielkeyes.thereveal.util.DateHelperUtil.getShortMonth
import org.junit.Test

import org.junit.Assert.*
import java.time.LocalDateTime

class DataHelperUtilTest {
    @Test
    fun test_get_short_month_localdatetime_month_10_returns_Oct() {
        val shortMonth = getShortMonth(LocalDateTime.now().withMonth(10))

        assertEquals(shortMonth, "Oct")
    }

    @Test
    fun test_get_short_month_10_returns_Oct() {
        val shortMonth = getShortMonth(10)

        assertEquals(shortMonth, "Oct")
    }

    @Test
    fun test_get_long_month_localdatetime_month_10_returns_October() {
        val shortMonth = getLongMonth(LocalDateTime.now().withMonth(10))

        assertEquals(shortMonth, "October")
    }

    @Test
    fun test_get_long_month_0_returns_October() {
        val shortMonth = getLongMonth(10)

        assertEquals(shortMonth, "October")
    }
}
