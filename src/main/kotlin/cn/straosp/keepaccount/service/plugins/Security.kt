package cn.straosp.keepaccount.service.plugins

import cn.straosp.keepaccount.service.service.UserService
import cn.straosp.keepaccount.service.util.UsernamePhoneTimeCheckPrincipal
import io.ktor.server.application.*
import io.ktor.server.auth.*
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