package cn.straosp.keepaccount.server.vo

import cn.straosp.keepaccount.server.db.User
import kotlinx.serialization.Serializable


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
data class WorkRecordsInRangeDate(
    val totalSalary: Double,     // Year: 2023: 115.0 2024 345.0 Month: 1: 12.0 2: 23.0  Day: 1 /2/3 每天的每月的每年的总工资
    val singleQuantity: Double,     // 在大区间内的单个区间内的个人产品总数
    val teamQuantity: Double,       // 在大区间内的单个区间内团队产品总数
    val workDate: String            // 时间:2024 / 2024-01 / 2024-01-01
)

@Serializable
data class WorkRecordsResult(
    val id:Int,
    val teamSize:Int,
    val productQuantity:Double,
    val productPrice:Double,
    val singleQuantity: Double,
    val workDate:String,
    val user: User
)

@Serializable
data class AddWorkRecords(
    val teamSize:Int? = 0,
    val productQuantity:Double = .0,
    val productPrice:Double,
    val singleQuantity:Double = .0,
    val workDate:String,
)

@Serializable
data class UpdateWorkRecords(
    val id:Int,
    val teamSize:Int? = 0,
    val productQuantity:Double? = .0,
    val productPrice:Double,
    val singleQuantity: Double? = .0
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