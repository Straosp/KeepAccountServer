package cn.straosp.keepaccount.util

import kotlinx.serialization.Serializable

@Serializable
data class RequestResult<T>(
    val code:Int,
    val message:String?,
    val data:T?
) {

    companion object {

        fun success() = RequestResult(200,"success",null)
        fun <T> success(data:T) = RequestResult(200,"success",data)
        fun success(message:String) = RequestResult(200,message,null)
        fun <T> success(message:String,data:T) = RequestResult(200,message,data)
        fun success(map: Map<String,Any>) = RequestResult(200,"success",map)

        fun error() = RequestResult(100,"request error",null)
        fun error(message: String) = RequestResult(101,message,null)
        fun <T> error(message: String,data:T) = RequestResult(102,message,data)
        fun error(code: Int,message: String) = RequestResult(code,message,null)

    }

}