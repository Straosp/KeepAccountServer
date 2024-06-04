package cn.straosp.keepaccount.service.impl

import cn.straosp.keepaccount.db.User
import cn.straosp.keepaccount.service.UserService
import org.koin.ktor.ext.inject
import org.ktorm.database.Database

class UserServiceImpl : UserService {

    private val dataBase:Database  by inject<Database>()

    override fun login(phone: String): User? {

    }

    override fun register(user: User) : User {

    }

    override fun getAllUser(): List<User> {

    }


}