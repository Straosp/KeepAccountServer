package cn.straosp.keepaccount.plugins

import cn.straosp.keepaccount.service.UserService
import cn.straosp.keepaccount.util.RequestResult
import cn.straosp.keepaccount.util.UsernamePhoneTimeCheckPrincipal
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {
    val userService by inject<UserService>()
    install(Authentication){
        basic(name = "headerAuth") {
            realm = "Ktor Server"
            validate { credentials ->
                println("Credentials: ${credentials.name}. ${credentials.password}")
                val user = userService.userAuth(credentials.password)
                if (user == null) null else UsernamePhoneTimeCheckPrincipal(user.username,user.phone,System.currentTimeMillis().toString())
            }

        }
    }
}
