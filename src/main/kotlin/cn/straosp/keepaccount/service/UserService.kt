package cn.straosp.keepaccount.service

import cn.straosp.keepaccount.db.User


interface UserService {

    fun login(phone:String):User?
    fun register(username:String,phone: String):User?
    fun getAllUser():List<User>
    fun userAuth(phone: String):User?

}