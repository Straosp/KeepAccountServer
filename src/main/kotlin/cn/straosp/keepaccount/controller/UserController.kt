package cn.straosp.keepaccount.controller

import cn.straosp.keepaccount.db.User
import cn.straosp.keepaccount.module.DruidConfig
import cn.straosp.keepaccount.service.UserService
import cn.straosp.keepaccount.util.RequestResult
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.userController() {
    val userService by inject<UserService>()

    route("/user"){
        post("/getAllUser"){
            val users = userService.getAllUser()
            call.respond(RequestResult.success(users))
        }
        post("/login"){
            val phone = kotlin.runCatching { call.receive<String>() }.getOrNull() ?: ""
            if (phone.isNullOrEmpty()){
                call.respond(RequestResult.error("请输入手机号"))
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
            val user = call.receive<User>()
            if (user.phone.isNullOrEmpty()){
                call.respond(RequestResult.error("请输入手机号"))
            }else if (user.username.isNullOrEmpty()){
                call.respond(RequestResult.error("请输入名称"))
            }else {
                val result = userService.register(user)
                call.respond(RequestResult.success("注册成功",result))
            }
        }

    }

}