package cn.straosp.keepaccount.server

import cn.straosp.keepaccount.server.module.configureKoin
import cn.straosp.keepaccount.server.plugins.*
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
