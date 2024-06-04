package cn.straosp.keepaccount.db

import com.mysql.cj.conf.url.SingleConnectionUrl
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf

interface DatabaseManager {

    val database:Database

}

class DataBaseManagerImpl : DatabaseManager {

    override val database: Database
        get() = Database.connect(
            url = "jdbc:mysql://127.0.0.1:3306/keep_account",
            user = "root",
            driver = "com.mysql.cj.jdbc.Driver",
            password = ""
        )

    val dataSource = database.sequenceOf(UserTables).sourceTable

}