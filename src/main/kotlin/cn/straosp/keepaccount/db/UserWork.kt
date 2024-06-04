package cn.straosp.keepaccount.db

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int

data class UserWork(val userId:Int,val workId:Int)

interface UserWorkTable: Entity<UserWorkTable> {

    companion object : Entity.Factory<UserWorkTable>()
    val id:Int
    val userId:Int
    val workId:Int
}

object UserWorkTables : Table<UserWorkTable>("work_user") {
    val id = int("id").primaryKey().bindTo { it.id }
    val userId = int("user_id").bindTo { it.userId }
    val workId = int("work_id").bindTo { it.workId }
}