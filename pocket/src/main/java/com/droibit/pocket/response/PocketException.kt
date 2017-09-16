package com.droibit.pocket.response

class PocketException(val error: Error, val rateLimit: RateLimit? = null) : Exception() {

    override val message: String get() = error.message

    override fun toString(): String {
        return "PocketException(error=$error, rateLimit=$rateLimit)"
    }
}