package cn.straosp.keepaccount.service

import cn.straosp.keepaccount.db.User


interface UserService {

    fun login(phone:String):User?
    fun register(user: User):User
    fun getAllUser():List<User>

}