package cn.straosp.keepaccount.service.plugins

import cn.straosp.keepaccount.service.controller.userController
import cn.straosp.keepaccount.service.controller.workRecordsController
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
