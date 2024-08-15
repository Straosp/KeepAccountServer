package cn.straosp.keepaccount.server.plugins

import cn.straosp.keepaccount.server.controller.userController
import cn.straosp.keepaccount.server.controller.workRecordsController
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(Resources)
    routing {
        userController()
        workRecordsController()
    }
}
