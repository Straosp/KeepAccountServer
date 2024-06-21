package cn.straosp.keepaccount.service.impl

import cn.straosp.keepaccount.db.User
import cn.straosp.keepaccount.db.UserTables
import cn.straosp.keepaccount.db.WorkRecordsTables
import cn.straosp.keepaccount.db.toUser
import cn.straosp.keepaccount.module.AppDatabase
import cn.straosp.keepaccount.service.WorkRecordsService
import cn.straosp.keepaccount.util.*
import cn.straosp.keepaccount.vo.AddWorkRecords
import cn.straosp.keepaccount.vo.UpdateWorkRecords
import cn.straosp.keepaccount.vo.WorkRecordsLineChart
import cn.straosp.keepaccount.vo.WorkRecordsResult
import org.ktorm.database.asIterable
import org.ktorm.dsl.*
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import java.time.LocalDate

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
                    productQuantity = row[WorkRecordsTables.productQuantity] ?: 0,
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

    override fun getWorkRecordRangeYear(phone: String, startDate: String, endDate: String): List<WorkRecordsLineChart> {
        val result = AppDatabase.database.useConnection { conn->
            val sql = "select sum((wr.product_quantity * wr.product_price) / wr.team_size) as salary, " +
                    "DATE_FORMAT(wr.work_date,'%Y') as month from work_records as wr inner join user on wr.user_id = user.id " +
                    "where user.phone = ? AND wr.work_date >= ? AND wr.work_date <= ? group by month order by month;"
            conn.prepareStatement(sql).use{ statement ->
                statement.setString(1,phone)
                statement.setString(2,"${startDate}-01-01")
                statement.setString(3,"${endDate}-12-31")
                statement.executeQuery().asIterable().map {row ->
                    WorkRecordsLineChart(
                        workDate = row.getString(2),
                        salary = row.getDouble(1)
                    )
                }.toList()
            }
        }
        return result
    }

    override fun getWorkRecordRangeMonth(phone: String, startDate: String, endDate: String): List<WorkRecordsLineChart> {
        val result = AppDatabase.database.useConnection { conn->
            val endSplit = endDate.split("-")
            val days = LocalDate.of(
                endSplit[0].toInt(),
                endSplit[1].toInt(),
                endSplit[1].toInt().dayOfMonth(endSplit[0].toInt())
            )
            val sql = "select sum((wr.product_quantity * wr.product_price) / wr.team_size) as salary, " +
                    "DATE_FORMAT(wr.work_date,'%Y-%m') as month from work_records as wr inner join user on wr.user_id = user.id " +
                    "where user.phone = ? AND wr.work_date >= ? AND wr.work_date <= ? group by month order by month;"
            conn.prepareStatement(sql).use{ statement ->
                statement.setString(1,phone)
                statement.setString(2,"${startDate}-01")
                statement.setString(3, days.toISODateString())
                statement.executeQuery().asIterable().map {row ->
                    WorkRecordsLineChart(
                        workDate = row.getString(2),
                        salary = row.getDouble(1)
                    )
                }.toList()
            }
        }
        return result
    }

    override fun getWorkRecordRangeDay(phone: String, startDate: String, endDate: String): List<WorkRecordsLineChart> {
        val workRecords = AppDatabase.database.from(WorkRecordsTables)
            .innerJoin(UserTables, on = UserTables.id eq WorkRecordsTables.userId )
            .select(
                WorkRecordsTables.workDate,
                WorkRecordsTables.productPrice.toDouble().times(WorkRecordsTables.productQuantity.toDouble()).div(WorkRecordsTables.teamSize.toDouble())
            )
            .where {
                val startLocalDate = startDate.toLocalDate()
                val endLocalDate = endDate.toLocalDate()
                (UserTables.phone eq phone) and WorkRecordsTables.workDate.between(LocalDateRange(startLocalDate,endLocalDate))
            }
            .orderBy(WorkRecordsTables.workDate.desc())
            .map {row ->
                WorkRecordsLineChart(
                    workDate = row.getString(1) ?: "",
                    salary = row.getDouble(2) ?: .0
                )
            }
        return workRecords
    }



}