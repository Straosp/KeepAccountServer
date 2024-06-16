package cn.straosp.keepaccount.controller

import cn.straosp.keepaccount.db.WorkRecords
import cn.straosp.keepaccount.util.RequestResult
import cn.straosp.keepaccount.util.UsernamePhoneTimeCheckPrincipal
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Routing.workRecordsController(){

    route("/workRecords"){
        authenticate("headerAuth") {
            post("/addWorkRecord") {
                val usernamePhoneTimeCheckPrincipal = call.principal<UsernamePhoneTimeCheckPrincipal>()
                val records = runCatching { call.receive<WorkRecords>() }.getOrNull()
                if (null == records){
                    call.respond(RequestResult.parameterError(WorkRecords.parameterList))
                    return@post
                }
                if (records.teamSize < 1 || records.productPrice < 0.1 || records.productQuantity < 1 || records.workDate.isNullOrEmpty()){
                    call.respond(RequestResult.parameterError(WorkRecords.parameterList))
                    return@post
                }


            }
        }
    }

}