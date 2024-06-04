package cn.straosp.keepaccount.module

import io.ktor.server.config.*

data class DruidConfig(
    val  url:String,
    val user:String,
    val driver:String,
    val password:String?
)

interface DruidConfigRepository {
    fun getDruidConfig(applicationConfig: ApplicationConfig = ApplicationConfig(null)):DruidConfig
}

class DruidConfigRepositoryImpl : DruidConfigRepository {
    override fun getDruidConfig(applicationConfig: ApplicationConfig): DruidConfig {
        val mysqlConfig = applicationConfig.config("database.mysql")
        return DruidConfig(
            url = mysqlConfig.property("url").getString(),
            user = mysqlConfig.property("user").getString(),
            driver = mysqlConfig.property("driver").getString(),
            password = mysqlConfig.property("password").getString()
        )
    }
}