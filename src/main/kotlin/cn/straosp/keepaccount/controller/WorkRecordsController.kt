package cn.straosp.keepaccount.controller

import cn.straosp.keepaccount.db.WorkRecords
import cn.straosp.keepaccount.service.WorkRecordsService
import cn.straosp.keepaccount.util.RequestResult
import cn.straosp.keepaccount.util.UsernamePhoneTimeCheckPrincipal
import cn.straosp.keepaccount.vo.SelectWorkRecordsByDate
import cn.straosp.keepaccount.vo.SelectWorkRecordsByRangeDate
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

fun Routing.workRecordsController(){
    val workRecordService by inject<WorkRecordsService>()
    route("/workRecords"){
        authenticate("headerAuth") {
            post("/addWorkRecord") {
                val records = runCatching { call.receive<WorkRecords>() }.getOrNull()
                if (null == records){
                    call.respond(RequestResult.parameterError(WorkRecords.parameterList))
                    return@post
                }
                if (records.teamSize < 1 || records.productPrice < 0.1 || records.productQuantity < 1 || records.workDate.isNullOrEmpty()){
                    call.respond(RequestResult.parameterError(WorkRecords.parameterList))
                    return@post
                }
                workRecordService.addWorkRecord(records)
                call.respond(RequestResult.success())
            }
        }
        authenticate("headerAuth") {
            post("/getAllWorkRecords") {
                val usernamePhoneTimeCheckPrincipal = call.principal<UsernamePhoneTimeCheckPrincipal>()
                val records = workRecordService.getAllWorkRecords(usernamePhoneTimeCheckPrincipal?.phone ?: "")
                call.respond(RequestResult.success(records))
            }
        }
        authenticate("headerAuth"){
            post("/getWorkRecordsByDate"){
                val userAuth = call.principal<UsernamePhoneTimeCheckPrincipal>()
                val selectWorkRecordsByTime = runCatching { call.receive<SelectWorkRecordsByDate>() }.getOrNull()
                selectWorkRecordsByTime?.let {
                    val records = workRecordService.getWorkRecordByDate(userAuth?.phone ?: "",selectWorkRecordsByTime.date)
                    call.respond(RequestResult.success(records))
                    return@post
                }
                call.respond(RequestResult.parameterError(SelectWorkRecordsByDate.parameterDescription()))
            }
        }
        authenticate("headerAuth"){
            post("/getWorkRecordsByRangeDate"){
                val userAuth = call.principal<UsernamePhoneTimeCheckPrincipal>()
                val select = runCatching { call.receive<SelectWorkRecordsByRangeDate>() }.getOrNull()
                select?.let {
                    val records = workRecordService.getWorkRecordsByRangeDate(userAuth?.phone ?: "",select.startDate,select.endDate)
                    call.respond(RequestResult.success(records))
                    return@post
                }
                call.respond(RequestResult.parameterError(SelectWorkRecordsByDate.parameterDescription()))
            }
        }

    }

}