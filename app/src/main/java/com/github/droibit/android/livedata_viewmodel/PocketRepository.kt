package com.github.droibit.android.livedata_viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.support.annotation.AnyThread
import com.droibit.pocket.Failure
import com.droibit.pocket.Pocket
import com.droibit.pocket.Success
import com.droibit.pocket.response.AccessToken
import com.droibit.pocket.response.RequestToken
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY

class PocketRepository(context: Context) {

    companion object {

        private const val KEY_ACCESS_TOKEN = "KEY_ACCESS_TOKEN"
    }

    private val pocket: Pocket

    private val prefs: SharedPreferences

    init {
        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(BODY))
                .build()
        pocket = Pocket(okHttpClient, Gson(), context.getString(R.string.pocket_consumer_key))
        prefs = context.getSharedPreferences("pocket", Context.MODE_PRIVATE)
    }

    @AnyThread
    fun getRequestToken(redirectUri: Uri): LiveData<Result<RequestToken>> {
        val data = MutableLiveData<Result<RequestToken>>()
        pocket.getRequestToken(redirectUri) {
            when (it) {
                is Success -> data.value = Result.Success(it.value)
                is Failure -> data.value = Result.Failure(it.throwable)
            }
        }
        return data
    }

    @AnyThread
    fun getAccessToken(requestToken: String): LiveData<Result<AccessToken>> {
        val data = MutableLiveData<Result<AccessToken>>()
        pocket.getAccessToken(requestToken) {
            when (it) {
                is Success -> {
                    storeAccessToken(it.value.token)
                    data.value = Result.Success(it.value)
                }
                is Failure -> data.value = Result.Failure(it.throwable)
            }
        }
        return data
    }

    @AnyThread
    fun add(url: String, tweetId: String? = null): LiveData<Result<Unit>> {
        val data = MutableLiveData<Result<Unit>>()
        pocket.add(url, tweetId = tweetId, accessToken = getAccessToken()) {
            when (it) {
                is Success -> data.value = Result.Success(it.value)
                is Failure -> data.value = Result.Failure(it.throwable)
            }
        }
        return data
    }

    private fun getAccessToken(): String {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    private fun storeAccessToken(accessToken: String) {
        prefs.edit().putString(KEY_ACCESS_TOKEN, accessToken).apply()
    }
}