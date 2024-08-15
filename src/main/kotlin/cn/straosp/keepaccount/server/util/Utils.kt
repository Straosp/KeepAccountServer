package cn.straosp.keepaccount.server.util

import java.util.*


fun String.encodeBtoa():String{
    if (this.isEmpty()) return ""
    return Base64.getEncoder().encodeToString(this.toByteArray(Charsets.UTF_8))
}

fun String.decodeBtoa():String {
    if (this.isEmpty()) return ""
    return String(Base64.getDecoder().decode(this), Charsets.UTF_8)
}