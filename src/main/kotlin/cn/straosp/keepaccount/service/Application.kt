package cn.straosp.keepaccount.service

import cn.straosp.keepaccount.service.module.configureKoin
import cn.straosp.keepaccount.service.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)

}

fun Application.module() {
    configureSecurity()
    configureAdministration()
    configureSerialization()
    configureHTTP()
    configureKoin()
    configureRouting()
}
