package cn.straosp.keepaccount.controller

import cn.straosp.keepaccount.db.WorkRecords
import cn.straosp.keepaccount.service.WorkRecordsService
import cn.straosp.keepaccount.util.OperationMessage
import cn.straosp.keepaccount.util.RequestResult
import cn.straosp.keepaccount.util.UsernamePhoneTimeCheckPrincipal
import cn.straosp.keepaccount.util.toLocalDate
import cn.straosp.keepaccount.vo.*
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
                val records = runCatching { call.receive<AddWorkRecords>() }.getOrNull()
                if (null == records){
                    call.respond(RequestResult.parameterError())
                    return@post
                }
                if (records.teamSize < 1 || records.productPrice < 0.1 || records.productQuantity < 1 || records.workDate.isEmpty()){
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
                if (null == records) {
                    call.respond(RequestResult.error("参数异常"))
                }else if (records.productPrice < 0.1 || records.productQuantity < 1 || records.teamSize < 1){
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

            post("/getWorkRecordsRangeDay"){
                val userAuth = call.principal<UsernamePhoneTimeCheckPrincipal>()
                val select = runCatching { call.receive<SelectWorkRecordsByRangeDate>() }.getOrNull()
                select?.let {
                    val records = workRecordService.getWorkRecordRangeDay(userAuth?.phone ?: "",select.startDate,select.endDate)
                    call.respond(RequestResult.success(records))
                }
                call.respond(RequestResult.parameterError())
            }
            post("/getWorkRecordsRangeMonth"){
                try {
                    val userAuth = call.principal<UsernamePhoneTimeCheckPrincipal>()
                    val select = runCatching { call.receive<SelectWorkRecordsByRangeDate>() }.getOrNull()
                    select?.let {
                        val startSplit = select.startDate.split("-")
                        val endSplit = select.endDate.split("-")
                        if (startSplit[0].toInt() > endSplit[0].toInt()){
                            call.respond(RequestResult.parameterError())
                        }else if (startSplit[0].toInt() >= endSplit[0].toInt() && startSplit[1].toInt() > endSplit[1].toInt()){
                            call.respond(RequestResult.parameterError())
                        }else{
                            val records = workRecordService.getWorkRecordRangeMonth(userAuth?.phone ?: "",select.startDate,select.endDate)
                            call.respond(RequestResult.success(records))
                        }
                    }
                    call.respond(RequestResult.parameterError())
                }catch (e:Exception){
                    call.respond(RequestResult.parameterError())
                }

            }
            post("/getWorkRecordsRangeYear"){
                try {
                    val userAuth = call.principal<UsernamePhoneTimeCheckPrincipal>()
                    val select = runCatching { call.receive<SelectWorkRecordsByRangeDate>() }.getOrNull()
                    select?.let {
                        if (select.startDate.toInt() - select.endDate.toInt() >= 0 || select.endDate.toInt() < select.startDate.toInt()){
                            call.respond(RequestResult.parameterError())
                        }else {
                            val records = workRecordService.getWorkRecordRangeYear(userAuth?.phone ?: "",select.startDate,select.endDate)
                            call.respond(RequestResult.success(records))
                        }
                        return@post
                    }
                    call.respond(RequestResult.parameterError())
                }catch (e:Exception){
                    call.respond(RequestResult.parameterError())
                }

            }
        }
    }

}