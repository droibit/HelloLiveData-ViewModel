package com.github.droibit.android.livedata_viewmodel

import com.droibit.pocket.response.AccessToken
import com.droibit.pocket.response.RequestToken

sealed class RequestTokenEvent {
    class Success(val value: RequestToken) : RequestTokenEvent()
    class Error(val error: String) : RequestTokenEvent()
}

sealed class AccessTokenEvent {
    class Success(val value: AccessToken) : AccessTokenEvent()
    class Error(val error: String) : AccessTokenEvent()
}