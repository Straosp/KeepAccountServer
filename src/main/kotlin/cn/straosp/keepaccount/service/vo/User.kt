package cn.straosp.keepaccount.service.vo

import kotlinx.serialization.Serializable

@Serializable
data class Token(val token:String)
@Serializable
data class LoginUser(val phone:String)
@Serializable
data class RegisterUser(val username:String,val phone:String)