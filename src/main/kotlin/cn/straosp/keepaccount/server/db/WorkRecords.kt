package cn.straosp.keepaccount.server.db

import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.date
import org.ktorm.schema.double
import org.ktorm.schema.int
import java.time.LocalDate

@Serializable
data class WorkRecords(
    val id:Int,
    val teamSize:Int,
    val productPrice:Double,
    val productQuantity:Double,
    val workDate:String,
    val userId:Int,
    val singleQuantity:Double
    )

interface WorkRecordsTable: Entity<WorkRecordsTable> {

    companion object : Entity.Factory<WorkRecordsTable>()
    val id:Int
    val teamSize:Int
    val productPrice:Double
    val productQuantity: Double
    val workDate:LocalDate
    val userId:Int
    val singleQuantity:Double
}

object WorkRecordsTables : Table<WorkRecordsTable>("work_records") {
    val id = int("id").primaryKey().bindTo { it.id }
    val teamSize = int("team_size").bindTo { it.teamSize }
    val productPrice = double("product_price").bindTo { it.productPrice }
    val productQuantity = double("product_quantity").bindTo { it.productQuantity }
    val workDate = date("work_date").bindTo { it.workDate }
    val userId = int("user_id").bindTo { it.userId }
    val singleQuantity = double("single_quantity").bindTo { it.singleQuantity }
}
