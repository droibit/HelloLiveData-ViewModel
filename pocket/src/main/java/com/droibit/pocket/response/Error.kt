package com.droibit.pocket.response

import okhttp3.Headers

/**
 * * [Error Handling](https://getpocket.com/developer/docs/errors)
 *
 * @property code Error code.
 * @property message Error message.
 */
data class Error(val code: Int, val message: String) {

    companion object {
        private const val X_ERROR = "X-Error"
        private const val X_ERROR_CODE = "X-Error-Code"
    }

    constructor(headers: Headers) : this(
            code = headers[X_ERROR_CODE]!!.toInt(),
            message = headers[X_ERROR]!!
    )
}