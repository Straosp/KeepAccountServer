package cn.straosp.keepaccount.util

import kotlin.jvm.internal.Intrinsics.Kotlin

public data class UsernamePhoneTimeCheckPrincipal(val username: String,val phone:String,val validityTime:String) : io.ktor.server.auth.Principal{

}