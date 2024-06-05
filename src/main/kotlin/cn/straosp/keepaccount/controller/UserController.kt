package cn.straosp.keepaccount.controller

import cn.straosp.keepaccount.db.User
import cn.straosp.keepaccount.db.UserTable
import cn.straosp.keepaccount.db.UserTables
import cn.straosp.keepaccount.service.UserService
import cn.straosp.keepaccount.util.RequestResult
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Routing.userController() {
    val userService by inject<UserService>()
    route("/user"){
        post("/getAllUser"){
            val users = userService.getAllUser()
            call.respond(RequestResult.selectSuccess(users))
        }
        post("/login"){
            val phone = kotlin.runCatching { call.receive<String>() }.getOrNull() ?: ""
            if (phone.isEmpty()){
                call.respond(RequestResult.parameterError("phone:String"))
            }else{
                val user = userService.login(phone)
                if ( null == user){
                    call.respond(RequestResult.error("用户不存在"))
                }else{
                    call.respond(RequestResult.success(user))
                }
            }
        }
        post("/register"){
            try {
                val user = kotlin.runCatching { call.receive<User>() }.getOrNull() ?: User("","")
                if (user.phone.isEmpty() || user.username.isEmpty()){
                    call.respond(RequestResult.parameterError(User.parameterList))
                } else {
                    val result = userService.register(user)
                    call.respond(RequestResult.success("注册成功",result))
                }
            }catch (e:Exception){
                call.respond(RequestResult.error(e.message ?: ""))
            }
        }

    }

}