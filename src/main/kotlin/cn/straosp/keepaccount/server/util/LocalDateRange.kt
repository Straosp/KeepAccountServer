package cn.straosp.keepaccount.server.util

import java.time.LocalDate

class LocalDateRange(
    override val start: LocalDate,
    override val endInclusive: LocalDate
) : ClosedRange<LocalDate> {

    override fun contains(value: LocalDate): Boolean {
        val startLocalDate = start.toTimestamp()
        val endLocalDate = endInclusive.toTimestamp()
        val valueLocalDate = value.toTimestamp()
        return valueLocalDate in startLocalDate..endLocalDate
    }

    override fun isEmpty(): Boolean {
        val startLocalDate = start.toTimestamp()
        val endLocalDate = endInclusive.toTimestamp()
        return startLocalDate > endLocalDate
    }

}