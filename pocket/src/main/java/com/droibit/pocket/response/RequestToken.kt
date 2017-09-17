package com.droibit.pocket.response

import android.net.Uri
import android.support.annotation.Keep

@Keep
data class RequestToken(val code: String) {

    companion object {
        private const val URL_OAUTH = "https://getpocket.com/auth/authorize"
        private const val KEY_REQUEST_TOKEN = "request_token"
        private const val KEY_REDIRECT_URI = "redirect_uri"
    }

    fun asAuthenticationUri(redirectUri: String): Uri {
        return Uri.parse(URL_OAUTH)
                .buildUpon()
                .appendQueryParameter(KEY_REQUEST_TOKEN, code)
                .appendQueryParameter(KEY_REDIRECT_URI, redirectUri)
                .build()
    }
}
