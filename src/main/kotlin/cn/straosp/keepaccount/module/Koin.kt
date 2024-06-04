package cn.straosp.keepaccount.module

import cn.straosp.keepaccount.service.UserService
import cn.straosp.keepaccount.service.impl.UserServiceImpl
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.ktorm.database.Database

val serviceModule = module {
    single<UserService> { UserServiceImpl()}
}
val dataBaseModule = module {
    single<DruidConfigRepository>{ DruidConfigRepositoryImpl() }
    single<Database> {
        Database.connect(
            url = "jdbc:mysql://127.0.0.1:3306/keep_account",
            user = "root",
            driver = "com.mysql.cj.jdbc.Driver",
            password = ""
        )
    }
}

fun Application.configureKoin(){
    install(Koin) {
        modules(serviceModule, dataBaseModule)
    }
}