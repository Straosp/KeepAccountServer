package cn.straosp.keepaccount.service

import cn.straosp.keepaccount.db.WorkRecords

interface WorkRecordsService {

    fun addWorkRecord(workRecords: WorkRecords)
    fun addWorkRecords(workRecords: List<WorkRecords>)
    fun getWorkRecordByDate(phone:String,date:String):WorkRecords
    fun getAllWorkRecords():List<WorkRecords>

}