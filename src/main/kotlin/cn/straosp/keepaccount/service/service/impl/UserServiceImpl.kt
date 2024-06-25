package cn.straosp.keepaccount.service.service.impl

import cn.straosp.keepaccount.service.db.User
import cn.straosp.keepaccount.service.service.UserService
import org.ktorm.dsl.*
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import cn.straosp.keepaccount.service.db.*
import cn.straosp.keepaccount.service.util.*

class UserServiceImpl : UserService {

    override fun login(phone: String): Result<User> {
        val user = AppDatabase.database.sequenceOf(UserTables).find { (it.phone eq  phone) and (it.status eq  0)}?.toUser()
        user?.let {
            return Result.success(user)
        }
        return Result.failure(OperationMessage(10,"未找到该用户"))

    }

    override fun register(username:String,phone:String) : User? {
        AppDatabase.database.useTransaction {
            val id = AppDatabase.database.insertAndGenerateKey(UserTables){
                set(it.username,username)
                set(it.phone,phone)
            }
            val lastUser = AppDatabase.database.sequenceOf(UserTables).find { it.id eq (id as Int) }
            assert(lastUser != null)
            return lastUser?.toUser()
        }
    }

    override fun getAllUser(): List<User> {
        return AppDatabase.database.from(UserTables).select()
            .map {row ->
                User(
                    id = row[UserTables.id] ?: 0,
                    username = row[UserTables.username] ?: "",
                    phone = row[UserTables.phone] ?: "",
                    status = row[UserTables.status] ?: 0
                )
            }.toList()
    }

    override fun userAuth(phone: String): User? =
        AppDatabase.database.sequenceOf(UserTables).find {it.phone eq  phone}?.toUser()

    override fun deleteUser(phone: String) {
        AppDatabase.database.update(UserTables){
            set(it.status, 1)
            where {
                it.phone eq phone
            }
        }
    }

    override fun findUser(phone: String) {
        AppDatabase.database.update(UserTables){
            set(it.status,0)
            where {
                it.phone eq phone
            }
        }
    }


}