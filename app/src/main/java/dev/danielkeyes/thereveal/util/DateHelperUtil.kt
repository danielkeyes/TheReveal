package dev.danielkeyes.thereveal.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


object DateHelperUtil {
    /**
     * Takes in LocalDateTime and returns it months as short name ie 10 -> Oct
     * param LocalDateTime
     * @return String of month of LocalDateTime to short name
     */
    fun getShortMonth(dob: LocalDateTime): String {
        return getMonth(dob, "MMM")
    }

    /**
     * Takes in month as int and returns it months as short name ie 10 -> Oct
     * @param month [Int] from range 1-12
     * @return String
     */
    fun getShortMonth(month: Int): String {
        return getMonth(LocalDateTime.now().withMonth(month), "MMM")
    }

    /**
     * Takes in LocalDateTime and returns it months as short name ie 10 -> October
     * param LocalDateTime
     * @return String
     */
    fun getLongMonth(dob: LocalDateTime): String {
        return getMonth(dob, "MMMM")
    }

    /**
     * Takes in month as int and returns it months as short name ie 10 -> October
     * @param month [Int] from range 1-12
     * @return String
     */
    fun getLongMonth(month: Int): String {
        return getMonth(LocalDateTime.now().withMonth(month), "MMMM")
    }

    private fun getMonth(dob: LocalDateTime, pattern: String): String {
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(pattern)

        return dob.format(dateTimeFormatter)
    }

    fun getDayWithOrdinal(day: Int): String {
        return when (day) {
            1, 21, 31 -> "${day}st"
            2, 22 -> "${day}nd"
            3, 23 -> "${day}rd"
            else -> "${day}th"
        }
    }
}
