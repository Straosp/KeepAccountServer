package cn.straosp.keepaccount.server.service

import cn.straosp.keepaccount.server.vo.*

interface WorkRecordsService {

    fun addWorkRecord(phone: String,addWorkRecords: AddWorkRecords):Result<Int>
    fun addWorkRecords(phone: String,workRecords: List<AddWorkRecords>):Result<Int>
    fun updateWorkRecords(updateWorkRecords: UpdateWorkRecords):Result<Int>
    fun deleteWorkRecordById(id:Int)
    fun getCurrentMonthWorkRecords(phone: String):List<WorkRecordsResult>
    fun getWorkRecordRangeYear(phone: String,startDate: String,endDate: String):List<WorkRecordsLineChart>
    fun getWorkRecordRangeMonth(phone: String,startDate: String,endDate: String):List<WorkRecordsLineChart>
    fun getWorkRecordRangeDay(phone: String,startDate: String,endDate: String):List<WorkRecordsLineChart>
    fun getWorkRecordsByYearMonth(phone: String,year:Int,month:Int): List<HistoryWorkRecords>
    fun getTotalSalaryByYear(phone: String,year: Int):YearSalaryResult

}