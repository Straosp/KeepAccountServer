package cn.straosp.keepaccount.server.db

import cn.straosp.keepaccount.server.module.DruidConfigRepository
import cn.straosp.keepaccount.server.module.DruidConfigRepositoryImpl
import com.alibaba.druid.pool.DruidDataSourceFactory
import org.ktorm.database.Database

object AppDatabase {

    private val druidConfigRepository : DruidConfigRepository = DruidConfigRepositoryImpl()
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