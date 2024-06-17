package cn.straosp.keepaccount.db

import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar


@Serializable
data class User(val id:Int,val username: String,val phone:String){

    companion object{
        val parameterList = "{username:String,phone:String}"
    }

}

interface UserTable : Entity<UserTable> {
    companion object : Entity.Factory<UserTable>()
    val id:Int
    val username:String
    val phone:String
}

object UserTables : Table<UserTable>("user") {
    val id = int("id").primaryKey().bindTo { it.id }
    val username = varchar("username").bindTo { it.username }
    val phone = varchar("phone").bindTo { it.phone }
}

fun  UserTable.toUser(): User = User(id = this.id, username = this.username,phone = this.phone)