package cn.straosp.keepaccount.server.util

public data class UsernamePhoneTimeCheckPrincipal(val username: String,val phone:String,val validityTime:String) : io.ktor.server.auth.Principal{

}