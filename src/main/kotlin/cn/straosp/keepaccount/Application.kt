package cn.straosp.keepaccount

import cn.straosp.keepaccount.module.configureKoin
import cn.straosp.keepaccount.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.koin.ktor.plugin.Koin

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
