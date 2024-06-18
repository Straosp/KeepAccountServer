package cn.straosp.keepaccount.controller

import cn.straosp.keepaccount.db.User
import cn.straosp.keepaccount.service.UserService
import cn.straosp.keepaccount.util.RequestResult
import cn.straosp.keepaccount.util.encodeBtoa
import cn.straosp.keepaccount.vo.LoginUser
import cn.straosp.keepaccount.vo.RegisterUser
import cn.straosp.keepaccount.vo.Token
import com.mysql.cj.log.Log
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Routing.userController() {
    val userService by inject<UserService>()
    route("/user"){
        authenticate("headerAuth"){
            post("/getAllUser"){
                val users = userService.getAllUser()
                call.respond(RequestResult.selectSuccess(users))
            }
        }
        post("/login"){
            val loginUser = kotlin.runCatching { call.receive<LoginUser>() }.getOrNull() ?: LoginUser(phone = "")
            if (loginUser.phone.isEmpty()){
                call.respond(RequestResult.parameterError("phone:String"))
            }else{
                val user = userService.login(loginUser.phone)
                if ( null == user){
                    call.respond(RequestResult.error("用户不存在"))
                }else{
                    call.respond(RequestResult.success(Token(token = "${user.username}:${user.phone}".encodeBtoa())))
                }
            }
        }
        post("/register"){
            try {
                val user = kotlin.runCatching { call.receive<RegisterUser>() }.getOrNull() ?: RegisterUser("","")
                if (user.phone.isEmpty() || user.username.isEmpty()){
                    call.respond(RequestResult.parameterError(User.parameterList))
                } else {
                    val result = userService.register(user.username,user.phone)
                    call.respond(RequestResult.success("注册成功",result))
                }
            }catch (e:Exception){
                call.respond(RequestResult.error(e.message ?: ""))
            }
        }

    }

}