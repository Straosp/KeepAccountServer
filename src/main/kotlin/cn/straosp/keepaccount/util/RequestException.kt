package cn.straosp.keepaccount.util

open class RequestException(val requestResult: RequestResult<Nothing>) : Exception(requestResult.toString()) {

}

class IllegalParameterException(vararg params:String) : RequestException(RequestResult(code = 101, message = params.joinToString(prefix = "参数：", postfix = " 不合法!"), data = null) )