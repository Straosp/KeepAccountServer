package cn.straosp.keepaccount.service.module

import cn.straosp.keepaccount.service.service.UserService
import cn.straosp.keepaccount.service.service.WorkRecordsService
import cn.straosp.keepaccount.service.service.impl.UserServiceImpl
import cn.straosp.keepaccount.service.service.impl.WorkRecordsServiceImpl
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