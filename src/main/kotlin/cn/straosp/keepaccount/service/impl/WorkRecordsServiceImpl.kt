package cn.straosp.keepaccount.service.impl

import cn.straosp.keepaccount.db.*
import cn.straosp.keepaccount.module.AppDatabase
import cn.straosp.keepaccount.service.WorkRecordsService
import cn.straosp.keepaccount.util.toLocalDate
import cn.straosp.keepaccount.util.toTimestampString
import net.mamoe.yamlkt.YamlSequence
import org.ktorm.dsl.*
import org.ktorm.entity.sequenceOf
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
                        set(UserWorkTables.userId,userId)
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
                            set(UserWorkTables.userId,userId)
                            set(UserWorkTables.workId,workId[index])
                        }
                    }
                }
            }
            assert(userId.isNotEmpty())
        }
    }

    override fun getWorkRecordByDate(phone:String,date: String): WorkRecords {

        AppDatabase.database.useTransaction {
            val workRecords = AppDatabase.database.from(WorkRecordsTables)
                .innerJoin(UserWorkTables, on = WorkRecordsTables.id eq UserWorkTables.workId)
                .innerJoin(UserTables, on = UserTables.id eq )
                .select(
                    WorkRecordsTables.id,
                    WorkRecordsTables.teamSize,
                    WorkRecordsTables.productPrice,
                    WorkRecordsTables.productQuantity,
                    WorkRecordsTables.workDate,
                )
                .where {
                    WorkRecordsTables.workDate eq  LocalDate.parse(date)
                }

        }



    }

    override fun getAllWorkRecords(): List<WorkRecords> {
        TODO("Not yet implemented")
    }


}