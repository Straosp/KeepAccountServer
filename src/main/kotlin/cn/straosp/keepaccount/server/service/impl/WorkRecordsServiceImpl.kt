package cn.straosp.keepaccount.server.service.impl

import cn.straosp.keepaccount.server.service.WorkRecordsService
import cn.straosp.keepaccount.server.vo.*
import cn.straosp.keepaccount.server.db.*
import org.ktorm.database.asIterable
import org.ktorm.dsl.*
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import java.time.LocalDate
import cn.straosp.keepaccount.server.util.*
import kotlin.math.sin

class WorkRecordsServiceImpl : WorkRecordsService {

    override fun addWorkRecord(phone: String,addWorkRecords: AddWorkRecords): Result<Int> {
        AppDatabase.database.useTransaction {
            val result = AppDatabase.database.sequenceOf(WorkRecordsTables).find { it.workDate eq addWorkRecords.workDate.toLocalDate() }
            if (result != null){
                return Result.failure(OperationMessage(20,"数据已存在"))
            }
            val loginUser = AppDatabase.database.sequenceOf(UserTables).find { it.phone eq phone }?.toUser()
            if (loginUser == null){
                return Result.failure(OperationMessage(21,"用户异常"))
            }
            val workId = AppDatabase.database.insertAndGenerateKey(WorkRecordsTables){
                set(WorkRecordsTables.teamSize,addWorkRecords.teamSize)
                set(WorkRecordsTables.productPrice,addWorkRecords.productPrice)
                set(WorkRecordsTables.productQuantity,addWorkRecords.productQuantity)
                set(WorkRecordsTables.workDate,addWorkRecords.workDate.toLocalDate())
                set(WorkRecordsTables.userId,loginUser.id)
                set(WorkRecordsTables.singleQuantity,addWorkRecords.singleQuantity)
            } as Int
            assert(workId != 0)
            return  Result.success(workId)
        }
    }

    override fun addWorkRecords(phone: String,workRecords: List<AddWorkRecords>) :Result<Int> {
        val loginUser = AppDatabase.database.sequenceOf(UserTables).find { it.phone eq phone}
        if (null == loginUser){
            return Result.failure(OperationMessage(20,"用户不存在"))
        }
        AppDatabase.database.batchInsert(WorkRecordsTables){
            workRecords.map {wr ->
                item {
                    set(WorkRecordsTables.teamSize,wr.teamSize)
                    set(WorkRecordsTables.productPrice,wr.productPrice)
                    set(WorkRecordsTables.productQuantity,wr.productQuantity)
                    set(WorkRecordsTables.workDate,wr.workDate.toLocalDate())
                    set(WorkRecordsTables.userId,loginUser.id)
                    set(WorkRecordsTables.singleQuantity,wr.singleQuantity)
                }
            }
        }
        return Result.success(1)
    }

    override fun updateWorkRecords(updateWorkRecords: UpdateWorkRecords): Result<Int> {
        AppDatabase.database.update(WorkRecordsTables){
            set(WorkRecordsTables.teamSize,updateWorkRecords.teamSize)
            set(WorkRecordsTables.productPrice,updateWorkRecords.productPrice)
            set(WorkRecordsTables.productQuantity,updateWorkRecords.productQuantity)
            set(WorkRecordsTables.singleQuantity,updateWorkRecords.singleQuantity)
            where {
                WorkRecordsTables.id eq updateWorkRecords.id
            }
        }
        return Result.success(1)
    }

    override fun deleteWorkRecordById(id: Int) {
        AppDatabase.database.delete(WorkRecordsTables){
            it.id eq  id
        }
    }

    override fun getCurrentMonthWorkRecords(phone: String): List<WorkRecordsResult> {
        val workRecords = AppDatabase.database.from(WorkRecordsTables)
            .innerJoin(UserTables, on = UserTables.id eq WorkRecordsTables.userId )
            .select(
                WorkRecordsTables.id,
                WorkRecordsTables.teamSize,
                WorkRecordsTables.productPrice,
                WorkRecordsTables.productQuantity,
                WorkRecordsTables.singleQuantity,
                WorkRecordsTables.workDate,
                UserTables.id,
                UserTables.phone,
                UserTables.username,
                UserTables.status
            )
            .where {
                val localDate = getCurrentMonthLocalDate()
                (UserTables.phone eq phone) and WorkRecordsTables.workDate.between(LocalDateRange(localDate.first,localDate.second))
            }
            .orderBy(WorkRecordsTables.workDate.desc())
            .map {row ->
                WorkRecordsResult(
                    id = row[WorkRecordsTables.id] ?: 0,
                    teamSize = row[WorkRecordsTables.teamSize] ?: 0,
                    productPrice = row[WorkRecordsTables.productPrice] ?: .0,
                    productQuantity = row[WorkRecordsTables.productQuantity] ?: .0,
                    singleQuantity = row[WorkRecordsTables.singleQuantity] ?: .0,
                    workDate = (row[WorkRecordsTables.workDate] ?: LocalDate.now()).toISODateString(),
                    user = User(
                        id = row[UserTables.id] ?: 0,
                        username = row[UserTables.username] ?: "",
                        phone = row[UserTables.phone] ?: "",
                        status = row[UserTables.status] ?: 0
                    )
                )
            }
        return workRecords
    }

    override fun getMonthWorkRecords(phone: String, monthDate: LocalDate): List<WorkRecordsResult> {
        val workRecords = AppDatabase.database.from(WorkRecordsTables)
            .innerJoin(UserTables, on = UserTables.id eq WorkRecordsTables.userId )
            .select(
                WorkRecordsTables.id,
                WorkRecordsTables.teamSize,
                WorkRecordsTables.productPrice,
                WorkRecordsTables.productQuantity,
                WorkRecordsTables.singleQuantity,
                WorkRecordsTables.workDate,
                UserTables.id,
                UserTables.phone,
                UserTables.username,
                UserTables.status
            )
            .where {
                val startDate = LocalDate.of(monthDate.year,monthDate.monthValue,1)
                val endDate = LocalDate.of(monthDate.year,monthDate.monthValue,monthDate.monthValue.dayOfMonth(monthDate.year))
                (UserTables.phone eq phone) and WorkRecordsTables.workDate.between(LocalDateRange(startDate,endDate))
            }
            .orderBy(WorkRecordsTables.workDate.desc())
            .map {row ->
                WorkRecordsResult(
                    id = row[WorkRecordsTables.id] ?: 0,
                    teamSize = row[WorkRecordsTables.teamSize] ?: 0,
                    productPrice = row[WorkRecordsTables.productPrice] ?: .0,
                    productQuantity = row[WorkRecordsTables.productQuantity] ?: .0,
                    singleQuantity = row[WorkRecordsTables.singleQuantity] ?: .0,
                    workDate = (row[WorkRecordsTables.workDate] ?: LocalDate.now()).toISODateString(),
                    user = User(
                        id = row[UserTables.id] ?: 0,
                        username = row[UserTables.username] ?: "",
                        phone = row[UserTables.phone] ?: "",
                        status = row[UserTables.status] ?: 0
                    )
                )
            }
        return workRecords
    }


    override fun getWorkRecordsInRangeDate(phone: String, type:Int, startDate: LocalDate, endDate: LocalDate): List<WorkRecordsInRangeDate> {

        val workRecordsRangeDate = AppDatabase.database.from(WorkRecordsTables)
            .innerJoin(UserTables, on =  UserTables.id eq WorkRecordsTables.userId)
            .select(
                WorkRecordsTables.workDate,
                WorkRecordsTables.teamSize,
                WorkRecordsTables.productPrice,
                WorkRecordsTables.singleQuantity,
                WorkRecordsTables.productQuantity
            )
            .where {
                WorkRecordsTables.workDate.between(LocalDateRange(startDate,endDate)) and (UserTables.phone eq phone)
            }
            .orderBy(WorkRecordsTables.workDate.asc())
            .map { row ->
                WorkRecords(
                    id = 0,
                    workDate = (row[WorkRecordsTables.workDate])?.toISODateString() ?: "",
                    teamSize = row[WorkRecordsTables.teamSize] ?: 0,
                    productPrice = row[WorkRecordsTables.productPrice] ?: .0,
                    singleQuantity = row[WorkRecordsTables.singleQuantity] ?: .0,
                    productQuantity = row[WorkRecordsTables.productQuantity] ?: .0,
                    userId = 0
                )
            }

        if (workRecordsRangeDate.isEmpty()){
            return emptyList()
        }
        when (type) {
            3 -> {
                return workRecordsRangeDate.map { workRecord ->
                    val totalSalary = (workRecord.singleQuantity * workRecord.productPrice) + (workRecord.productQuantity * workRecord.productPrice).div(workRecord.teamSize)
                    WorkRecordsInRangeDate(
                        totalSalary = totalSalary,
                        singleQuantity = workRecord.singleQuantity,
                        teamQuantity = workRecord.productQuantity,
                        workDate = workRecord.workDate
                    )
                }
            }
            2 -> {
                val workRecordsByMonth = workRecordsRangeDate.groupBy { it.workDate.toLocalDate().monthValue }
                return workRecordsByMonth.map { keys ->
                    val singleQuantity = keys.value.filter { it.singleQuantity > 0 }.sumOf { it.singleQuantity }  // 得到的就是月内的个人件数总和
                    val teamQuantity = keys.value.filter { it.teamSize > 1 }.sumOf { it.productQuantity }   //得到的就是团队数量总和
                    val totalSalary = keys.value.groupBy { it.teamSize }.mapValues {teamSizeKey ->
                        if (teamSizeKey.key == 0){
                            teamSizeKey.value.sumOf { it.singleQuantity * it.productPrice }
                        }else{
                            teamSizeKey.value.sumOf { (it.productPrice * it.productQuantity).plus(it.singleQuantity * it.productPrice) }.div(teamSizeKey.key)
                        }
                    }.values.sum()
                    WorkRecordsInRangeDate(
                        totalSalary = totalSalary,
                        singleQuantity = singleQuantity,
                        teamQuantity = teamQuantity,
                        workDate = keys.value.first().workDate.take(7)
                    )
                }
            }
            else -> {
                val workRecordsByMonth = workRecordsRangeDate.groupBy { it.workDate.toLocalDate().year }
                return workRecordsByMonth.map { keys ->

                    val teamQuantity = keys.value.filter { it.teamSize > 1 }.sumOf { it.productQuantity }   //得到的就是团队数量总和

                    val singleQuantity = keys.value.filter { it.singleQuantity > 0 }.sumOf { it.singleQuantity }  // 得到的就是月内的个人件数总和

                    val mixedTotalSalary = keys.value.filter { it.teamSize > 0 && it.singleQuantity  > 0 }.groupBy { it.teamSize }.map { mixedKeys ->
                        mixedKeys.value.sumOf { (it.productPrice * it.productQuantity) }.div(mixedKeys.key)
                            .plus(mixedKeys.value.sumOf { it.singleQuantity * it.productPrice })
                    }.sum()
                    val singleTotalSalary = keys.value.filter { it.teamSize == 0 && it.singleQuantity > 0 }.sumOf { it.singleQuantity * it.productPrice }

                    val multiTotalSalary = keys.value.filter { it.teamSize > 0 && it.singleQuantity == .0 }.groupBy { it.teamSize }.map { multiKeys ->
                        multiKeys.value.sumOf {
                            (it.productPrice * it.productQuantity)
                        }.div(multiKeys.key)
                    }.sum()
                    WorkRecordsInRangeDate(
                        totalSalary = mixedTotalSalary + singleTotalSalary + multiTotalSalary,
                        singleQuantity = singleQuantity,
                        teamQuantity = teamQuantity,
                        workDate = keys.value.first().workDate.take(4)
                    )
                }
            }
        }
    }


}