package cn.straosp.keepaccount.service

import cn.straosp.keepaccount.db.User


interface UserService {

    fun login(phone:String):Result<User>
    fun register(username:String,phone: String): User?
    fun getAllUser():List<User>
    fun userAuth(phone: String):User?
    fun deleteUser(phone: String)
    fun findUser(phone: String)

}