package cn.straosp.keepaccount.server.controller

import cn.straosp.keepaccount.server.service.WorkRecordsService
import cn.straosp.keepaccount.server.util.*
import cn.straosp.keepaccount.server.vo.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.time.LocalDate
import kotlin.runCatching

fun Routing.workRecordsController(){
    val workRecordService by inject<WorkRecordsService>()
    route("/workRecords"){
        authenticate("headerAuth") {
            post("/addWorkRecord") {
                // {teamSize: 0, workDate: 2024-10-28, productPrice: 10, singleQuantity: 10, productQuantity: 0}
                val records = runCatching { call.receive<AddWorkRecords>() }.getOrNull()
                if (null == records){
                    call.respond(RequestResult.parameterError())
                    return@post
                }
                if (records.singleQuantity > 0 && records.productQuantity > 0  && records.teamSize < 2){
                    call.respond(RequestResult.parameterError())
                    return@post
                }
                if (records.singleQuantity < 1 && records.productQuantity < 1){
                    call.respond(RequestResult.parameterError())
                    return@post
                }
                if (records.productPrice < 0.1){
                    call.respond(RequestResult.parameterError())
                    return@post
                }
                if (records.workDate.isEmpty()){
                    call.respond(RequestResult.parameterError())
                    return@post
                }
                val loginUser = call.principal<UsernamePhoneTimeCheckPrincipal>()
                val result = workRecordService.addWorkRecord(loginUser?.phone ?: "",records)
                result.getOrElse {
                    val operationMessage = result.exceptionOrNull() as? OperationMessage
                    call.respond(RequestResult.error(operationMessage?.errorMsg ?: "数据异常，请稍后重试"))
                }
                call.respond(RequestResult.success())
            }
            post("/addWorkRecords"){
                val records = runCatching { call.receive<List<AddWorkRecords>>() }.getOrNull()
                if (records.isNullOrEmpty()){
                    call.respond(RequestResult.parameterError())
                    return@post
                }
                val loginUser = call.principal<UsernamePhoneTimeCheckPrincipal>()
                val result = workRecordService.addWorkRecords(loginUser?.phone ?: "",records)
                result.getOrElse {
                    val operationMessage = result.exceptionOrNull() as? OperationMessage
                    call.respond(RequestResult.error(operationMessage?.errorMsg ?: "数据异常，请稍后重试"))
                }
                call.respond(RequestResult.success())
            }

            post("/getCurrentMonthWorkRecords"){
                val userAuth = call.principal<UsernamePhoneTimeCheckPrincipal>()!!
                val records = workRecordService.getCurrentMonthWorkRecords(userAuth.phone ?: "")
                call.respond(RequestResult.success(records))
            }
            post("/getMonthWorkRecords"){
                val userAuth = call.principal<UsernamePhoneTimeCheckPrincipal>()
                val select = runCatching { call.receive<SelectWorkRecordsByYearMonth>() }.getOrNull()
                if (null == userAuth || null == select){
                    call.respond(RequestResult.parameterError())
                    return@post
                }

                val records = workRecordService.getMonthWorkRecords(userAuth.phone, LocalDate.of(select.year,select.month,1))
                call.respond(RequestResult.success(records))
            }

            post("/deleteWorkRecordsById"){
                val recordId = runCatching { call.receive<DeleteWorkRecordById>()  }.getOrNull()
                recordId?.let {
                    workRecordService.deleteWorkRecordById(recordId.id)
                    call.respond(RequestResult.success("删除成功"))
                }
                call.respond(RequestResult.parameterError())
            }
            post("/updateWorkRecords"){
                val records = runCatching { call.receive<UpdateWorkRecords>()  }.getOrNull()
                val account =  call.principal<UsernamePhoneTimeCheckPrincipal>()
                if (null == records) {
                    println("Record == NULL")
                    call.respond(RequestResult.error("参数异常"))
                }else if (records.productPrice < 0.1 || ((records.singleQuantity ?: .0) < 0 && (records.teamSize ?: 0) < 0)){
                    println("Record == 1")
                    call.respond(RequestResult.error("参数异常"))
                }else{
                    val result = workRecordService.updateWorkRecords(records)
                    result.getOrElse {
                        val operationMessage = result.exceptionOrNull() as? OperationMessage
                        call.respond(RequestResult.error(operationMessage?.errorMsg ?: "数据异常，请稍后重试"))
                    }
                    call.respond(RequestResult.success("更新成功"))
                }
            }

            post("/getWorkRecordsInRangeDate"){
                try {
                    val userAuth = runCatching { call.principal<UsernamePhoneTimeCheckPrincipal>() }.getOrNull()
                    val select = runCatching { call.receive<SelectWorkRecordsByRangeDate>() }.getOrNull()
                    if (null == userAuth || null == select) {
                        call.respond(RequestResult.parameterError())
                        return@post
                    }
                    val startDateSplit = select.startDate.split("-")
                    val endDateSplit = select.endDate.split("-")
                    if (startDateSplit.size != endDateSplit.size){
                        call.respond(RequestResult.parameterError())
                        return@post
                    }
                    val startDate = when (startDateSplit.size) {
                        3 -> {
                            LocalDate.of(startDateSplit[0].toInt(),startDateSplit[1].toInt(),startDateSplit[2].toInt())
                        }
                        2 -> {
                            LocalDate.of(startDateSplit[0].toInt(),startDateSplit[1].toInt(),1)
                        }
                        else -> {
                            LocalDate.of(startDateSplit[0].toInt(),1,1)
                        }
                    }
                    val endDate = when (endDateSplit.size) {
                        3 -> {
                            LocalDate.of(endDateSplit[0].toInt(),endDateSplit[1].toInt(),endDateSplit[2].toInt())
                        }
                        2 -> {
                            LocalDate.of(endDateSplit[0].toInt(),endDateSplit[1].toInt(), endDateSplit[1].toInt().dayOfMonth(endDateSplit[0].toInt()))
                        }
                        else -> {
                            LocalDate.of(endDateSplit[0].toInt(),12,31)
                        }
                    }
                    val result = workRecordService.getWorkRecordsInRangeDate(userAuth.phone,startDateSplit.size,startDate, endDate)
                    call.respond(RequestResult.success(result))
                }catch (e:Exception){
                    call.respond(RequestResult.parameterError())
                }
            }

        }
    }

}