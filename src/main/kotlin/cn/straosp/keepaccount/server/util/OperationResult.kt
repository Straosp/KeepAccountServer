package cn.straosp.keepaccount.server.util

import kotlinx.serialization.Serializable

@Serializable
data class OperationMessage(val code:Int,val errorMsg:String):Throwable()