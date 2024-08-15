package cn.straosp.keepaccount.server.module

import cn.straosp.keepaccount.server.service.UserService
import cn.straosp.keepaccount.server.service.WorkRecordsService
import cn.straosp.keepaccount.server.service.impl.UserServiceImpl
import cn.straosp.keepaccount.server.service.impl.WorkRecordsServiceImpl
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

val serviceModule = module {
    single<UserService> { UserServiceImpl() }
    single<WorkRecordsService>{ WorkRecordsServiceImpl()  }
}
val dataBaseModule = module {
    single<DruidConfigRepository>{ DruidConfigRepositoryImpl() }
}

fun Application.configureKoin(){
    install(Koin) {
        modules(
            serviceModule,
            dataBaseModule
        )
    }
}