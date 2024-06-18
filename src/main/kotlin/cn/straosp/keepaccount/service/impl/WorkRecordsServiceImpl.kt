package cn.straosp.keepaccount.service.impl

import cn.straosp.keepaccount.db.*
import cn.straosp.keepaccount.module.AppDatabase
import cn.straosp.keepaccount.service.WorkRecordsService
import cn.straosp.keepaccount.util.LocalDateRange
import cn.straosp.keepaccount.util.dayOfMonth
import cn.straosp.keepaccount.util.toISODateString
import cn.straosp.keepaccount.util.toLocalDate
import org.ktorm.dsl.*
import java.time.LocalDate

class WorkRecordsServiceImpl : WorkRecordsService {

    override fun addWorkRecord(workRecords: WorkRecords) {
        AppDatabase.database.useTransaction {
            val workId = AppDatabase.database.insertAndGenerateKey(WorkRecordsTables){
                set(WorkRecordsTables.teamSize,workRecords.teamSize)
                set(WorkRecordsTables.productPrice,workRecords.productPrice)
                set(WorkRecordsTables.productQuantity,workRecords.productQuantity)
                set(WorkRecordsTables.workDate,workRecords.workDate.toLocalDate())
            } as Int
            assert(workId != 0)
            AppDatabase.database.batchInsert(UserWorkTables){
                workRecords.users.map {userId ->
                    item {
                        set(UserWorkTables.workId,workId)
                        set(UserWorkTables.userId,userId.id)
                    }
                }
            }
        }
    }

    override fun addWorkRecords(workRecords: List<WorkRecords>) {
        AppDatabase.database.useTransaction {
            val workId = AppDatabase.database.batchInsert(WorkRecordsTables){
                workRecords.map {wr ->
                    item {
                        set(WorkRecordsTables.teamSize,wr.teamSize)
                        set(WorkRecordsTables.productPrice,wr.productPrice)
                        set(WorkRecordsTables.productQuantity,wr.productQuantity)
                        set(WorkRecordsTables.workDate,wr.workDate.toLocalDate())
                    }
                }
            }
            assert(workId.isNotEmpty())
            val userId = AppDatabase.database.batchInsert(UserWorkTables){
                workRecords.forEachIndexed { index, wr ->
                    wr.users.forEach { userId ->
                        item {
                            set(UserWorkTables.userId,userId.id)
                            set(UserWorkTables.workId,workId[index])
                        }
                    }
                }
            }
            assert(userId.isNotEmpty())
        }
    }

    override fun getWorkRecordByDate(phone:String,date: String): WorkRecords? {
        AppDatabase.database.useTransaction {
            val workRecords = AppDatabase.database.from(WorkRecordsTables)
                .innerJoin(UserWorkTables, on = WorkRecordsTables.id eq UserWorkTables.workId)
                .innerJoin(UserTables, on = UserTables.id eq UserWorkTables.userId )
                .select(
                    WorkRecordsTables.id,
                    WorkRecordsTables.teamSize,
                    WorkRecordsTables.productPrice,
                    WorkRecordsTables.productQuantity,
                    WorkRecordsTables.workDate,
                )
                .where {
                    (UserTables.phone eq phone) and (WorkRecordsTables.workDate eq  LocalDate.parse(date))
                }
                .map {row ->
                    WorkRecords(
                        id = row[WorkRecordsTables.id] ?: 0,
                        teamSize = row[WorkRecordsTables.teamSize] ?: 0,
                        productPrice = row[WorkRecordsTables.productPrice] ?: .0,
                        productQuantity = row[WorkRecordsTables.productQuantity] ?: 0,
                        workDate = (row[WorkRecordsTables.workDate] ?: LocalDate.now()).toISODateString(),
                        users =  emptyList()
                    )
                }
            if (workRecords.isNullOrEmpty()) return null
            val userMap = mutableMapOf<Int,List<User>?>()
            workRecords.forEach {row ->
                val users = AppDatabase.database.from(UserTables)
                    .innerJoin(UserWorkTables, on = UserTables.id eq UserWorkTables.userId)
                    .select(
                        UserTables.id,
                        UserTables.username,
                        UserTables.phone
                    )
                    .where {
                        UserWorkTables.workId eq (row.id ?: 0).toInt()
                    }
                    .map { userRow ->
                        User(
                            id = userRow[UserTables.id] ?: 0,
                            username = userRow[UserTables.username] ?: "",
                            phone = userRow[UserTables.phone] ?: ""
                        )
                    }
                userMap[(row.id ?: 0).toInt()] = users
            }
            return workRecords.map {
                it.copy(users = userMap[it.id] ?: emptyList())
            }.toList().first()

        }
    }

    override fun getWorkRecordsByRangeDate(phone: String,startDate: String,endDate:String): List<WorkRecords> {
        AppDatabase.database.useTransaction {
            val workRecords = AppDatabase.database.from(WorkRecordsTables)
                .innerJoin(UserWorkTables, on = WorkRecordsTables.id eq UserWorkTables.workId)
                .innerJoin(UserTables, on = UserTables.id eq UserWorkTables.userId )
                .select(
                    WorkRecordsTables.id,
                    WorkRecordsTables.teamSize,
                    WorkRecordsTables.productPrice,
                    WorkRecordsTables.productQuantity,
                    WorkRecordsTables.workDate,
                )
                .where {
                    val startLocalDate = startDate.toLocalDate()
                    val endLocalDate = endDate.toLocalDate()
                    (UserTables.phone eq phone) and WorkRecordsTables.workDate.between(LocalDateRange(startLocalDate,endLocalDate))
                }
                .map {row ->
                    WorkRecords(
                        id = row[WorkRecordsTables.id] ?: 0,
                        teamSize = row[WorkRecordsTables.teamSize] ?: 0,
                        productPrice = row[WorkRecordsTables.productPrice] ?: .0,
                        productQuantity = row[WorkRecordsTables.productQuantity] ?: 0,
                        workDate = (row[WorkRecordsTables.workDate] ?: LocalDate.now()).toISODateString(),
                        users =  emptyList()
                    )
                }
            if (workRecords.isNullOrEmpty()) return emptyList()
            val userMap = mutableMapOf<Int,List<User>?>()
            workRecords.forEach {row ->
                val users = AppDatabase.database.from(UserTables)
                    .innerJoin(UserWorkTables, on = UserTables.id eq UserWorkTables.userId)
                    .select(
                        UserTables.id,
                        UserTables.username,
                        UserTables.phone
                    )
                    .where {
                        UserWorkTables.workId eq (row.id ?: 0).toInt()
                    }
                    .map { userRow ->
                        User(
                            id = userRow[UserTables.id] ?: 0,
                            username = userRow[UserTables.username] ?: "",
                            phone = userRow[UserTables.phone] ?: ""
                        )
                    }
                userMap[(row.id ?: 0).toInt()] = users
            }
            return workRecords.map {
                it.copy(users = userMap[it.id] ?: emptyList())
            }.toList()

        }
    }

    override fun getAllWorkRecords(phone: String): List<WorkRecords> {
        AppDatabase.database.useTransaction {
            val workRecords = AppDatabase.database.from(WorkRecordsTables)
                .innerJoin(UserWorkTables, on = WorkRecordsTables.id eq UserWorkTables.workId)
                .innerJoin(UserTables, on = UserTables.id eq UserWorkTables.userId )
                .select(
                    WorkRecordsTables.id,
                    WorkRecordsTables.teamSize,
                    WorkRecordsTables.productPrice,
                    WorkRecordsTables.productQuantity,
                    WorkRecordsTables.workDate,
                )
                .where {
                    UserTables.phone eq phone
                }.map {row ->
                    WorkRecords(
                        id = row[WorkRecordsTables.id] ?: 0,
                        teamSize = row[WorkRecordsTables.teamSize] ?: 0,
                        productPrice = row[WorkRecordsTables.productPrice] ?: .0,
                        productQuantity = row[WorkRecordsTables.productQuantity] ?: 0,
                        workDate = (row[WorkRecordsTables.workDate] ?: LocalDate.now()).toISODateString(),
                        users =  emptyList()
                    )
                }
            val userMap = mutableMapOf<Int,List<User>?>()
            workRecords.forEach {row ->
                val users = AppDatabase.database.from(UserTables)
                    .innerJoin(UserWorkTables, on = UserTables.id eq UserWorkTables.userId)
                    .select(
                        UserTables.id,
                        UserTables.username,
                        UserTables.phone
                    )
                    .where {
                        UserWorkTables.workId eq (row.id ?: 0).toInt()
                    }
                    .map { userRow ->
                        User(
                            id = userRow[UserTables.id] ?: 0,
                            username = userRow[UserTables.username] ?: "",
                            phone = userRow[UserTables.phone] ?: ""
                        )
                    }
                userMap[(row.id ?: 0).toInt()] = users
            }
            return workRecords.map {
                it.copy(users = userMap[it.id] ?: emptyList())
            }.toList()


        }
    }


}