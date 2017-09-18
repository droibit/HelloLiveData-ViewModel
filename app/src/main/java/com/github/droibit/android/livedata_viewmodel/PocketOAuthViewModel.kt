package com.github.droibit.android.livedata_viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.net.Uri
import com.droibit.pocket.response.AccessToken
import com.droibit.pocket.response.RequestToken

class PocketOAuthViewModel(application: Application) : AndroidViewModel(application) {

    sealed class RequestTokenEvent {
        class Success(val value: RequestToken) : RequestTokenEvent()
        class Error(val error: String) : RequestTokenEvent()
    }

    sealed class AccessTokenEvent {
        class Success(val value: AccessToken) : AccessTokenEvent()
        class Error(val error: String) : AccessTokenEvent()
    }

    private val pocketRepository: PocketRepository

    private val requestTokenTrigger = MutableLiveData<Uri>()

    private val accessTokenTrigger = MutableLiveData<String>()

    val requestToken: LiveData<RequestTokenEvent>

    val accesssToken: LiveData<AccessTokenEvent>

    init {
        pocketRepository = (application as HelloApplication).pocketRepository
        requestToken = requestTokenTrigger.switchMap {
            pocketRepository.getRequestToken(redirectUri = it)
        }.map {
            when (it) {
                is Result.Success<RequestToken> -> RequestTokenEvent.Success(it.value)
                is Result.Failure -> RequestTokenEvent.Error(it.throwable.toString())
            }
        }

        accesssToken = accessTokenTrigger.switchMap {
            pocketRepository.getAccessToken(requestToken = it)
        }.map {
            when (it) {
                is Result.Success<AccessToken> -> AccessTokenEvent.Success(it.value)
                is Result.Failure -> AccessTokenEvent.Error(it.throwable.toString())
            }
        }
    }

    fun getRequestToken(redirectUri: Uri) {
        requestTokenTrigger.value = redirectUri
    }

    fun getAccessToken(requestToken: String) {
        accessTokenTrigger.value = requestToken
    }
}