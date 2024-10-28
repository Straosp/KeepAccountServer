package cn.straosp.keepaccount.server.module

data class DruidConfig(
    val  url:String,
    val user:String,
    val driver:String,
    val password:String?,
    val defaultConnectSize: Int,
    val maxConnectSize: Int,
    val maxWaitTime: Int

)

interface DruidConfigRepository {
    fun getDruidConfig():DruidConfig
}

class DruidConfigRepositoryImpl : DruidConfigRepository {
    override fun getDruidConfig(): DruidConfig {
        return DruidConfig(
            url = "jdbc:mysql://10.10.10.10:3306/keep_account",
            driver = "com.mysql.cj.jdbc.Driver",
            user = "keep_account",
            password = "785125ABC",
            defaultConnectSize = 10,
            maxConnectSize = 20,
            maxWaitTime = 1000,
        )
    }
}
//user = "keep_account",
//password = "785125ABCd/",