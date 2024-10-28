package cn.straosp.keepaccount.server.vo

import cn.straosp.keepaccount.server.db.User
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
data class SelectWorkRecordsByRangeDate(val startDate:String,val endDate:String)

@Serializable
data class WorkRecordsLineChart(
    val salary:Double,
    val monthQuantity:Double,
    val singleWorkProductQuantity:Double,
    val workDate: String,
)

@Serializable
data class WorkRecordsResult(
    val id:Int,
    val teamSize:Int,
    val productQuantity:Int,
    val productPrice:Double,
    val singleQuantity: Int,
    val workDate:String,
    val user: User
)

@Serializable
data class AddWorkRecords(
    val teamSize:Int,
    val productQuantity:Int,
    val productPrice:Double,
    val singleQuantity:Int,
    val workDate:String,
)

@Serializable
data class UpdateWorkRecords(
    val id:Int,
    val teamSize:Int,
    val productQuantity:Int,
    val productPrice:Double,
    val singleQuantity: Int,
    val workDate:String,
)

@Serializable
data class DeleteWorkRecordById(
    val id: Int
)

@Serializable
data class SelectWorkRecordsByYearMonth(
    val year:Int,
    val month:Int
)
@Serializable
data class YearSalary(
    val year: Int
)
@Serializable
data class YearSalaryResult(
    val salary: Double
)