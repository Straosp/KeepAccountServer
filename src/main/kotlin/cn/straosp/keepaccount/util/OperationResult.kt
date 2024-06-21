package cn.straosp.keepaccount.util

import kotlinx.serialization.Serializable

@Serializable
data class OperationMessage(val code:Int,val errorMsg:String):Throwable()