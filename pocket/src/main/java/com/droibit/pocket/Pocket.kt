package com.droibit.pocket

import android.net.Uri
import android.support.annotation.AnyThread
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
import retrofit2.Callback as RetrofitCallback
import retrofit2.Response as RetrofitResponse

typealias Callback<T> = (Response<T>) -> Unit

sealed class Response<T>
class Success<T>(val value: T) : Response<T>()
class Failure<T>(val throwable: Throwable) : Response<T>()

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
    fun blockingGetRequestToken(redirectUri: Uri): RequestToken {
        val okhttpResponse = service.getRequestToken(consumerKey, redirectUri.toString()).execute()
        if (!okhttpResponse.isSuccessful) {
            throw PocketException(error = Error(okhttpResponse.headers()))
        }
        return okhttpResponse.body()!!
    }

    @AnyThread
    fun getRequestToken(redirectUri: Uri, callback: Callback<RequestToken>) {
        service.getRequestToken(consumerKey, redirectUri.toString())
                .enqueue(object : RetrofitCallback<RequestToken> {
                    override fun onResponse(call: Call<RequestToken>, response: RetrofitResponse<RequestToken>) {
                        if (response.isSuccessful) {
                            callback(Success(response.body()!!))
                        } else {
                            callback(Failure(PocketException(error = Error(response.headers()))))
                        }
                    }

                    override fun onFailure(call: Call<RequestToken>, t: Throwable) {
                        callback(Failure(t))
                    }
                })
    }

    @Throws(IOException::class, PocketException::class)
    @WorkerThread
    fun blockingGetAccessToken(requestToken: String): AccessToken {
        val okhttpResponse = service.getAccessToken(consumerKey, requestToken).execute()
        if (!okhttpResponse.isSuccessful) {
            throw PocketException(error = Error(okhttpResponse.headers()))
        }
        return okhttpResponse.body()!!
    }

    @AnyThread
    fun getAccessToken(requestToken: String, callback: Callback<AccessToken>) {
        service.getAccessToken(consumerKey, requestToken)
                .enqueue(object : RetrofitCallback<AccessToken> {
                    override fun onResponse(call: Call<AccessToken>, response: RetrofitResponse<AccessToken>) {
                        if (response.isSuccessful) {
                            callback(Success(response.body()!!))
                        } else {
                            callback(Failure(PocketException(error = Error(response.headers()))))
                        }
                    }

                    override fun onFailure(call: Call<AccessToken>, t: Throwable) {
                        callback(Failure(t))
                    }
                })
    }

    @Throws(IOException::class, PocketException::class)
    @WorkerThread
    fun blockingAdd(url: String, title: String? = null, tags: String? = null, tweetId: String? = null, accessToken: String) {
        val okhttpResponse = service.add(url, title, tags, tweetId, consumerKey, accessToken).execute()
        if (!okhttpResponse.isSuccessful) {
            throw PocketException(
                    error = Error(okhttpResponse.headers()),
                    rateLimit = PocketRateLimit(okhttpResponse.headers())
            )
        }
    }


    @AnyThread
    fun add(url: String, title: String? = null, tags: String? = null, tweetId: String? = null, accessToken: String, callback: Callback<Unit>) {
        service.add(url, title, tags, tweetId, consumerKey, accessToken)
                .enqueue(object : RetrofitCallback<AddResponse> {
                    override fun onResponse(call: Call<AddResponse>, response: RetrofitResponse<AddResponse>) {
                        if (response.isSuccessful) {
                            callback(Success(Unit))
                        } else {
                            val e = PocketException(
                                    error = Error(response.headers()),
                                    rateLimit = PocketRateLimit(response.headers())
                            )
                            callback(Failure(e))
                        }
                    }

                    override fun onFailure(call: Call<AddResponse>, t: Throwable) {
                        callback(Failure(t))
                    }
                })
    }
}