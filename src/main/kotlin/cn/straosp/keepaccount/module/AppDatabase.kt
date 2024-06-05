package cn.straosp.keepaccount.module

import com.alibaba.druid.pool.DruidDataSourceFactory
import org.koin.java.KoinJavaComponent.inject
import org.ktorm.database.Database
import org.ktorm.logging.LogLevel

object AppDatabase {

    private val druidConfigRepository :DruidConfigRepository = DruidConfigRepositoryImpl()
    private val druidConfig = druidConfigRepository.getDruidConfig()
    lateinit var database: Database
    init {
        val map = mapOf(
            "driverClassName" to druidConfig.driver,
            "url" to druidConfig.url,
            "username" to druidConfig.user,
            "password" to druidConfig.password,
        )
        database = Database.connect(DruidDataSourceFactory.createDataSource(map))
    }
/*
"initialSize" to druidConfig.defaultConnectSize,
            "maxActive" to druidConfig.maxConnectSize,
            "maxWait" to druidConfig.maxWaitTime,
            "logLevel" to LogLevel.DEBUG
 */


}