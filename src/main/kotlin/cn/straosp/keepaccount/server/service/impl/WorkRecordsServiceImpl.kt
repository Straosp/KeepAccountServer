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
                    productQuantity = row[WorkRecordsTables.productQuantity] ?: 0,
                    singleQuantity = row[WorkRecordsTables.singleQuantity] ?: 0,
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
            val sql = "select sum((wr.product_quantity * wr.product_price) / wr.team_size) as salary,sum(wr.product_quantity / wr.team_size) as quantity, " +
                    "SUM(CASE WHEN wr.team_size = 1 THEN product_quantity ELSE 0 END) as single_total,DATE_FORMAT(wr.work_date,'%Y') as month " +
                    "from work_records as wr inner join user on wr.user_id = user.id " +
                    "where user.phone = ? AND wr.work_date >= ? AND wr.work_date <= ? group by month order by month;"
            conn.prepareStatement(sql).use{ statement ->
                statement.setString(1,phone)
                statement.setString(2,"${startDate}-01-01")
                statement.setString(3,"${endDate}-12-31")
                statement.executeQuery().asIterable().map {row ->
                    WorkRecordsLineChart(
                        workDate = row.getString(4),
                        monthQuantity =  row.getDouble(2),
                        singleWorkProductQuantity = row.getDouble(3),
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
            val sql = "select \n" +
                    "sum(wr.product_quantity + wr.single_quantity) as quantity," +
                    "sum(wr.single_quantity) as singleQuantity," +
                    "SUM(CASE WHEN wr.single_quantity > 0 AND wr.product_quantity < 1 THEN wr.single_quantity * wr.product_price ELSE 0 END) as singleSalary," +
                    "SUM(CASE WHEN wr.single_quantity < 1 AND wr.product_quantity > 0 THEN ((wr.product_quantity * wr.product_price) / wr.team_size) ELSE 0 END) as teamSalary," +
                    "SUM( CASE WHEN wr.single_quantity > 0 AND wr.product_quantity > 0 THEN wr.single_quantity * wr.product_price + ((wr.product_quantity * wr.product_price) / wr.team_size ) ELSE 0 END ) as teamSingleSalary," +
                    "DATE_FORMAT(wr.work_date,'%Y-%m') as month " +
                    "from work_records as wr inner join user on wr.user_id = user.id " +
                    "where user.phone = ? AND wr.work_date >= ? AND wr.work_date <= ? group by month order by month;"
            conn.prepareStatement(sql).use{ statement ->
                statement.setString(1,phone)
                statement.setString(2,"${startDate}-01")
                statement.setString(3, days.toISODateString())
                statement.executeQuery().asIterable().map {row ->
                    WorkRecordsLineChart(
                        workDate = row.getString(6),
                        monthQuantity =  row.getDouble(1),
                        singleWorkProductQuantity = row.getDouble(2),
                        salary = row.getDouble(3) + row.getDouble(4) + row.getDouble(5)
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
                WorkRecordsTables.productQuantity.div(WorkRecordsTables.teamSize),
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
                    monthQuantity = row.getDouble(2) ?: .0,
                    singleWorkProductQuantity = .0,
                    salary = row.getDouble(3) ?: .0
                )
            }
        return workRecords
    }


    override fun getWorkRecordsByYearMonth(phone: String,year:Int,month:Int): List<AddWorkRecords> {
        val workRecords = AppDatabase.database.from(WorkRecordsTables)
            .innerJoin(UserTables, on = UserTables.id eq WorkRecordsTables.userId )
            .select(
                WorkRecordsTables.id,
                WorkRecordsTables.teamSize,
                WorkRecordsTables.productPrice,
                WorkRecordsTables.productQuantity,
                WorkRecordsTables.singleQuantity,
                WorkRecordsTables.workDate,
            )
            .where {
                val startDate = LocalDate.of(year,month,1)
                val endDate = LocalDate.of(year,month,month.dayOfMonth(year))
                (UserTables.phone eq phone) and WorkRecordsTables.workDate.between(LocalDateRange(startDate,endDate))
            }
            .orderBy(WorkRecordsTables.workDate.desc())
            .map { row ->
                AddWorkRecords(
                    teamSize = row[WorkRecordsTables.teamSize] ?: 0,
                    productPrice = row[WorkRecordsTables.productPrice] ?: .0,
                    productQuantity = row[WorkRecordsTables.productQuantity] ?: 0,
                    singleQuantity = row[WorkRecordsTables.singleQuantity] ?: 0,
                    workDate = (row[WorkRecordsTables.workDate] ?: LocalDate.now()).toISODateString(),
                )
            }
        return workRecords
    }

    override fun getTotalSalaryByYear(phone: String, year: Int):YearSalaryResult {
        val result = AppDatabase.database.from(WorkRecordsTables)
            .innerJoin(UserTables, on = WorkRecordsTables.userId eq UserTables.id)
            .select(
                WorkRecordsTables.userId,
                WorkRecordsTables.id,
                WorkRecordsTables.teamSize,
                WorkRecordsTables.productPrice,
                WorkRecordsTables.productQuantity,
                WorkRecordsTables.singleQuantity,
            )
            .where {
                val startLocalDate = LocalDate.of(year,1,1)
                val endDate = LocalDate.of(year,12,31)
                (UserTables.phone eq phone) and (WorkRecordsTables.workDate.between(LocalDateRange(startLocalDate,endDate)))
            }
            .map { row ->
                WorkRecords(
                    id = row[WorkRecordsTables.id] ?: 0,
                    teamSize = row[WorkRecordsTables.teamSize] ?: 0,
                    productPrice = row[WorkRecordsTables.productPrice] ?:  0.0,
                    productQuantity = row[WorkRecordsTables.productQuantity] ?: 0,
                    userId = row[WorkRecordsTables.userId] ?: 0,
                    singleQuantity = row[WorkRecordsTables.singleQuantity] ?: 0,
                    workDate = ""
                )
            }
        var totalSalary = 0.0
        val salaryMap:MutableMap<Int,Double> = HashMap()
        for (wr in result) {
            if (wr.singleQuantity > 0){
               totalSalary += wr.singleQuantity * wr.productPrice
            }
            //(wr.productPrice * wr.productQuantity).div(wr.teamSize)
            if (wr.teamSize < 1) continue
            if (salaryMap.containsKey(wr.teamSize)){
                salaryMap[wr.teamSize] = (salaryMap.get(wr.teamSize) ?: 0.0) + wr.productPrice * wr.productQuantity
            }else{
                salaryMap[wr.teamSize] = wr.productPrice * wr.productQuantity
            }
        }
        for (element in salaryMap.keys) {
            totalSalary += (salaryMap[element] ?: 0.0).div(element)
        }
        return YearSalaryResult(totalSalary)
    }



}