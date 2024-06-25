package cn.straosp.keepaccount.service.controller

import cn.straosp.keepaccount.service.db.User
import cn.straosp.keepaccount.service.service.UserService
import cn.straosp.keepaccount.service.util.OperationMessage
import cn.straosp.keepaccount.service.util.RequestResult
import cn.straosp.keepaccount.service.util.UsernamePhoneTimeCheckPrincipal
import cn.straosp.keepaccount.service.vo.LoginUser
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import cn.straosp.keepaccount.service.vo.*
import cn.straosp.keepaccount.service.util.encodeBtoa

fun Routing.userController() {
    val userService by inject<UserService>()
    route("/user"){
        post("/login"){
            val loginUser = kotlin.runCatching { call.receive<LoginUser>() }.getOrNull() ?: LoginUser(phone = "")
            if (loginUser.phone.isEmpty()){
                call.respond(RequestResult.parameterError())
            }else{
                val operationResult = userService.login(loginUser.phone)
                val user = operationResult.getOrElse {
                    val operationMessage = operationResult.exceptionOrNull() as? OperationMessage
                    call.respond(RequestResult.error(operationMessage?.errorMsg ?: "数据异常，请稍后重试"))
                } as? User
                call.respond(RequestResult.success(Token(token = "${user?.username}:${user?.phone}".encodeBtoa())))
            }
        }

        post("/register"){
            try {
                val user = kotlin.runCatching { call.receive<RegisterUser>() }.getOrNull() ?: RegisterUser("","")
                if (user.phone.isEmpty() || user.username.isEmpty()){
                    call.respond(RequestResult.parameterError())
                } else {
                    val result = userService.register(user.username,user.phone)
                    call.respond(RequestResult.success("注册成功",Token(token = "${result?.username}:${result?.phone}".encodeBtoa())))
                }
            }catch (e:Exception){
                call.respond(RequestResult.error(e.message ?: ""))
            }
        }

        post("/findUser"){
            val findUser = kotlin.runCatching { call.receive<LoginUser>() }.getOrNull() ?: LoginUser("")
            if (findUser.phone.isEmpty()){
                call.respond(RequestResult.parameterError())
            }else{
                userService.findUser(findUser.phone)
                call.respond(RequestResult.success())
            }
        }

        authenticate("headerAuth"){
            post("/getAllUser"){
                val users = userService.getAllUser()
                call.respond(RequestResult.selectSuccess(users))
            }
            post("/deleteUser"){
                val loginUser = call.principal<UsernamePhoneTimeCheckPrincipal>()
                val deletePhone = kotlin.runCatching { call.receive<LoginUser>() }.getOrNull() ?: LoginUser("")
                if (deletePhone.phone.isEmpty()){
                    call.respond(RequestResult.parameterError())
                }else{
                    userService.deleteUser(phone = deletePhone.phone)
                    call.respond(RequestResult.success("删除成功"))
                }
            }
        }

    }

}