package cn.straosp.keepaccount

import cn.straosp.keepaccount.module.configureKoin
import cn.straosp.keepaccount.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)

}

fun Application.module() {
    configureSecurity()
    configureAdministration()
    configureSerialization()
    //configureDatabases()
    configureHTTP()
    configureKoin()
    configureRouting()
}
