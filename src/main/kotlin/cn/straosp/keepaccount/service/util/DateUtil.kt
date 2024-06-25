package cn.straosp.keepaccount.service.util

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * 2024-05-01 To LocalDate
 */
fun String.toLocalDate(): LocalDate = if (this.isEmpty()) LocalDate.now() else LocalDate.parse(this, DateTimeFormatter.ISO_DATE)

fun LocalDate.toTimestampString(): String = this.toTimestamp().toString()

fun LocalDate.toTimestamp():Long {
    val localDateTime = this.atStartOfDay()
    return localDateTime.toEpochSecond(ZoneId.systemDefault().rules.getOffset(localDateTime)) * 1000L
}
fun LocalDate.toISODateString() = "${this.year}-${this.monthValue.toString().padStart(2,'0')}-${this.dayOfMonth.toString().padStart(2,'0')}"

private val dayOfMonth = intArrayOf(0,31,28,31,30,31,30,31,31,30,31,30,31)
fun Int.dayOfMonth(year:Int): Int {
    if (this == 2) return isLeapYearAndFebruaryDays(year).second
    if (this < 1 || this > 12) return 0
    return dayOfMonth[this]
}
fun isLeapYearAndFebruaryDays(year: Int): Pair<Boolean, Int> {
    val isLeapYear = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
    val februaryDays = if (isLeapYear) 29 else 28
    return Pair(isLeapYear, februaryDays)
}

fun getCurrentMonthLocalDate():Pair<LocalDate,LocalDate> {
    val localDate = LocalDate.now()
    val year = localDate.year
    val monthV = localDate.monthValue
    return  Pair(first = LocalDate.of(year,monthV, 1), second = LocalDate.of(year, monthV,monthV.dayOfMonth(year)))
}