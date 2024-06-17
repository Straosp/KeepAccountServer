package cn.straosp.keepaccount.module

import cn.straosp.keepaccount.service.UserService
import cn.straosp.keepaccount.service.WorkRecordsService
import cn.straosp.keepaccount.service.impl.UserServiceImpl
import cn.straosp.keepaccount.service.impl.WorkRecordsServiceImpl
import com.alibaba.druid.pool.DruidDataSource
import com.alibaba.druid.pool.DruidDataSourceFactory
import io.ktor.http.auth.*
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.ktorm.database.Database
import org.ktorm.logging.LogLevel

val serviceModule = module {
    single<UserService> { UserServiceImpl()}
    single<WorkRecordsService>{ WorkRecordsServiceImpl()  }
}
val dataBaseModule = module {
    single<DruidConfigRepository>{ DruidConfigRepositoryImpl() }
}

fun Application.configureKoin(){
    install(Koin) {
        modules(serviceModule, dataBaseModule)
    }
}