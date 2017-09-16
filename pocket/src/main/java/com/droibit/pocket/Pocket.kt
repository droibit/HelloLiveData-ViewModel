package com.droibit.pocket

import android.net.Uri
import android.support.annotation.WorkerThread
import com.droibit.pocket.response.*
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST
import java.io.IOException


class Pocket internal constructor(private val service: Service, private val consumerKey: String) {

    internal interface Service {

        @Headers("X-Accept:application/json")
        @FormUrlEncoded
        @POST("/v3/oauth/request")
        fun getRequestToken(
                @Field("consumer_key") consumerKey: String,
                @Field("redirect_uri") redirectUri: String): Call<RequestToken>

        @Headers("X-Accept:application/json")
        @FormUrlEncoded
        @POST("/v3/oauth/authorize")
        fun getAccessToken(
                @Field("consumer_key") consumerKey: String,
                @Field("code") requestToken: String): Call<AccessToken>

        @Headers("X-Accept:application/json")
        @FormUrlEncoded
        @POST("/v3/add")
        fun add(@Field("url") url: String,
                @Field("title") title: String?,
                @Field("tags") tags: String?,
                @Field("tweet_id") tweetId: String?,
                @Field("consumer_key") consumerKey: String,
                @Field("access_token") accessToken: String): Call<AddResponse>

    }

    companion object {
        private const val BASE_URL = "https://getpocket.com"
    }

    constructor(okHttpClient: OkHttpClient, gson: Gson, consumerKey: String) : this(
            service = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build().create(Pocket.Service::class.java),
            consumerKey = consumerKey
    )

    @Throws(IOException::class, PocketException::class)
    @WorkerThread
    fun getRequestToken(redirectUri: Uri): RequestToken {
        val okhttpResponse = service.getRequestToken(consumerKey, redirectUri.toString()).execute()
        if (!okhttpResponse.isSuccessful) {
            throw PocketException(error = Error(okhttpResponse.headers()))
        }
        return okhttpResponse.body()!!
    }

    @Throws(IOException::class, PocketException::class)
    @WorkerThread
    fun getAccessToken(requestToken: String): AccessToken {
        val okhttpResponse = service.getAccessToken(consumerKey, requestToken).execute()
        if (!okhttpResponse.isSuccessful) {
            throw PocketException(error = Error(okhttpResponse.headers()))
        }
        return okhttpResponse.body()!!
    }

    @Throws(IOException::class, PocketException::class)
    @WorkerThread
    fun add(url: String, title: String? = null, tags: String? = null, tweetId: String? = null, accessToken: String) {
        val okhttpResponse = service.add(url, title, tags, tweetId, consumerKey, accessToken).execute()
        if (!okhttpResponse.isSuccessful) {
            throw PocketException(
                    error = Error(okhttpResponse.headers()),
                    rateLimit = RateLimit(okhttpResponse.headers())
            )
        }
    }
}