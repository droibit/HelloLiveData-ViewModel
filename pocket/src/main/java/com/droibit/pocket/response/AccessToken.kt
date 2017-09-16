package com.droibit.pocket.response

import android.support.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AccessToken(
        @SerializedName("access_token") val token: String,
        @SerializedName("username") val userName: String)
