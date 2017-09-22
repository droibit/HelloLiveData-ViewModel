package com.github.droibit.android.livedata_viewmodel

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.net.Uri
import timber.log.Timber


class CoroutinePocketOAuthViewModel(application: Application) : RunnableAndroidViewModel(application) {

    private val pocketRepository: PocketRepository = (application as HelloApplication).pocketRepository

    val requestToken: LiveData<RequestTokenEvent> get() = _requestToken
    private val _requestToken = MutableLiveData<RequestTokenEvent>()

    val accessToken: LiveData<AccessTokenEvent> get() = _accessToken
    private val _accessToken = MutableLiveData<AccessTokenEvent>()

    fun getRequestToken(redirectUri: Uri) {
        execute {
            val event = async { _getRequestToken(redirectUri) }.await()
            _requestToken.postValue(event)
        }
    }

    private fun _getRequestToken(redirectUri: Uri): RequestTokenEvent {
        Timber.d("[${Thread.currentThread().name}]: _getRequestToken(uri=$redirectUri)")
        return try {
            pocketRepository.blockingGetRequestToken(redirectUri).run {
                RequestTokenEvent.Success(this)
            }
        } catch (e: Exception) {
            Timber.e(e)
            RequestTokenEvent.Error(e.toString())
        }
    }

    fun getAccessToken(requestToken: String) {
        execute {
            val event = async { _getAccessToken(requestToken) }.await()
            _accessToken.postValue(event)
        }
    }

    private fun _getAccessToken(requestToken: String): AccessTokenEvent {
        return try {
            pocketRepository.blockingGetAccessToken(requestToken).run {
                AccessTokenEvent.Success(this)
            }
        } catch (e: Exception) {
            Timber.e(e)
            AccessTokenEvent.Error(e.toString())
        }
    }
}