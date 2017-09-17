package com.droibit.pocket

import com.droibit.pocket.response.Error
import com.droibit.pocket.response.PocketRateLimit

class PocketException(val error: Error, val rateLimit: PocketRateLimit? = null) : Exception() {

    override val message: String get() = error.message

    override fun toString(): String {
        return "PocketException(error=$error, rateLimit=$rateLimit)"
    }
}