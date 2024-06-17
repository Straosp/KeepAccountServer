package cn.straosp.keepaccount.util

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * 2024-05-01 To LocalDate
 */
fun String.toLocalDate(): LocalDate = if (this.isEmpty()) LocalDate.now() else LocalDate.parse(this, DateTimeFormatter.ISO_DATE)

fun LocalDate.toTimestampString(): String {
    val localDateTime = this.atStartOfDay()
    // 将 LocalDateTime 转换为时间戳
    return (localDateTime.toEpochSecond(ZoneId.systemDefault().rules.getOffset(localDateTime)) * 1000L).toString()
}
fun LocalDate.toISODateString() = "${this.year}-${this.monthValue}-${this.dayOfMonth}"