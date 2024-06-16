package cn.straosp.keepaccount.db

import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.date
import org.ktorm.schema.double
import org.ktorm.schema.int
import java.time.LocalDate

@Serializable
data class WorkRecords(val teamSize:Int,val productPrice:Double,val productQuantity:Int,val workDate:String,val users:List<Int>) {
    companion object{
        val parameterList = "teamSize:Int,productPrice:Double,productQuantity:Int,workDate:String,users:List<Int>"
    }
}

interface WorkRecordsTable: Entity<WorkRecordsTable> {

    companion object : Entity.Factory<WorkRecordsTable>()
    val id:Int
    val teamSize:Int
    val productPrice:Double
    val productQuantity: Int
    val workDate:LocalDate
}

object WorkRecordsTables : Table<WorkRecordsTable>("work_records") {
    val id = int("id").primaryKey().bindTo { it.id }
    val teamSize = int("team_size").bindTo { it.teamSize }
    val productPrice = double("product_price").bindTo { it.productPrice }
    val productQuantity = int("product_quantity").bindTo { it.productQuantity }
    val workDate = date("work_date").bindTo { it.workDate }
}
