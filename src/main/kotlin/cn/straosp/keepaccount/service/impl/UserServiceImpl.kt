package cn.straosp.keepaccount.service.impl

import cn.straosp.keepaccount.db.User
import cn.straosp.keepaccount.db.UserTable
import cn.straosp.keepaccount.db.UserTables
import cn.straosp.keepaccount.db.toUser
import cn.straosp.keepaccount.module.AppDatabase
import cn.straosp.keepaccount.service.UserService
import cn.straosp.keepaccount.util.IllegalParameterException
import org.ktorm.dsl.*
import org.ktorm.entity.all
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf

class UserServiceImpl : UserService {

    override fun login(phone: String): User? {
        if (phone.isNullOrEmpty()) throw IllegalParameterException("phone")
        return AppDatabase.database.sequenceOf(UserTables).find { it.phone eq  phone}?.toUser()
    }

    override fun register(user: User) : User {
        if (user.username.isNullOrEmpty() || user.phone.isNullOrEmpty()) throw IllegalParameterException()
        AppDatabase.database.useTransaction {
            val id = AppDatabase.database.insertAndGenerateKey(UserTables){
                set(it.username,user.username)
                set(it.phone,user.phone)
            }
            val lastUser = AppDatabase.database.sequenceOf(UserTables).find { it.id eq (id as Int) }
            assert(lastUser != null)
            return lastUser?.toUser()!!
        }
    }

    override fun getAllUser(): List<User> {
        return AppDatabase.database.from(UserTables).select()
            .map {row ->
                User(username = row[UserTables.username] ?: "", phone = row[UserTables.phone] ?: "")
            }.toList()
    }


}