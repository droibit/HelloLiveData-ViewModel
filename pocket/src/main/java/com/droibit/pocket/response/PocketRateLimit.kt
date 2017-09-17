package com.droibit.pocket.response

import okhttp3.Headers

/**
 * * [Rate Limits](https://getpocket.com/developer/docs/rate-limits)
 *
 * @property limitForUser Current rate limit enforced per user.
 * @property remainingCallsForUser Number of calls remaining before hitting user's rate limit.
 * @property secondsUntilResetsForUser Seconds until user's rate limit resets.
 * @property limitForConsumerKey Current rate limit enforced per consumer key.
 * @property remainingCallsForConsumerKey Number of calls remaining before hitting consumer key's rate limit.
 * @property secondsUntilResetsForConsumerKey  Seconds until consumer key rate limit resets.
 */
data class PocketRateLimit(
        val limitForUser: Int,
        val remainingCallsForUser: Int,
        val secondsUntilResetsForUser: Int,
        val limitForConsumerKey: Int,
        val remainingCallsForConsumerKey: Int,
        val secondsUntilResetsForConsumerKey: Int) {

    companion object {

        private const val X_LIMIT_USER_LIMIT = "X-Limit-User-Limit"
        private const val X_LIMIT_USER_REMAINING = "X-Limit-User-Remaining"
        private const val X_LIMIT_USER_RESET = "X-Limit-User-Reset"
        private const val X_LIMIT_KEY_LIMIT = "X-Limit-Key-Limit"
        private const val X_LIMIT_KEY_REMAINING = "X-Limit-Key-Remaining"
        private const val X_LIMIT_KEY_RESET = "X-Limit-Key-Reset"
    }

    constructor(headers: Headers) : this(
            limitForUser = headers[X_LIMIT_USER_LIMIT]!!.toInt(),
            remainingCallsForUser = headers[X_LIMIT_USER_REMAINING]!!.toInt(),
            secondsUntilResetsForUser = headers[X_LIMIT_USER_RESET]!!.toInt(),
            limitForConsumerKey = headers[X_LIMIT_KEY_LIMIT]!!.toInt(),
            remainingCallsForConsumerKey = headers[X_LIMIT_KEY_REMAINING]!!.toInt(),
            secondsUntilResetsForConsumerKey = headers[X_LIMIT_KEY_RESET]!!.toInt()
    )
}
