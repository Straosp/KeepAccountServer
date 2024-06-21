package cn.straosp.keepaccount.service

import cn.straosp.keepaccount.vo.AddWorkRecords
import cn.straosp.keepaccount.vo.UpdateWorkRecords
import cn.straosp.keepaccount.vo.WorkRecordsLineChart
import cn.straosp.keepaccount.vo.WorkRecordsResult

interface WorkRecordsService {

    fun addWorkRecord(phone: String,addWorkRecords: AddWorkRecords):Result<Int>
    fun addWorkRecords(phone: String,workRecords: List<AddWorkRecords>):Result<Int>
    fun updateWorkRecords(updateWorkRecords: UpdateWorkRecords):Result<Int>
    fun deleteWorkRecordById(id:Int)
    fun getCurrentMonthWorkRecords(phone: String):List<WorkRecordsResult>
    fun getWorkRecordRangeYear(phone: String,startDate: String,endDate: String):List<WorkRecordsLineChart>
    fun getWorkRecordRangeMonth(phone: String,startDate: String,endDate: String):List<WorkRecordsLineChart>
    fun getWorkRecordRangeDay(phone: String,startDate: String,endDate: String):List<WorkRecordsLineChart>


}