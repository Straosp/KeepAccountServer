package cn.straosp.keepaccount.server.service

import cn.straosp.keepaccount.server.vo.*
import java.time.LocalDate

interface WorkRecordsService {

    fun addWorkRecord(phone: String,addWorkRecords: AddWorkRecords):Result<Int>
    fun addWorkRecords(phone: String,workRecords: List<AddWorkRecords>):Result<Int>
    fun updateWorkRecords(updateWorkRecords: UpdateWorkRecords):Result<Int>
    fun deleteWorkRecordById(id:Int)

    fun getCurrentMonthWorkRecords(phone: String):List<WorkRecordsResult>

    fun getMonthWorkRecords(phone: String,monthDate:LocalDate):List<WorkRecordsResult>

    fun getWorkRecordsInRangeDate(phone: String, type:Int,startDate: LocalDate, endDate: LocalDate): List<WorkRecordsInRangeDate>

}