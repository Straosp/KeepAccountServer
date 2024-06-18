package cn.straosp.keepaccount.vo

import kotlinx.serialization.Serializable

@Serializable
data class SelectWorkRecordsByDate(val date:String){
    companion object{
        fun parameterDescription():String {
            return "date:[String,2024-06-07]"
        }
    }
}

@Serializable
data class SelectWorkRecordsByRangeDate(val startDate:String,val endDate:String){
    companion object{
        fun parameterDescription():String {
            return "startDate:[String,2024-06-07],endDate:[String,2024-06-08]"
        }
    }
}