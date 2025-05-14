package com.example.a36food.data.network.interceptors

import android.content.SharedPreferences
import okhttp3.Interceptor
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val token = sharedPreferences.getString("access_token", null)
        val request = chain.request().newBuilder()

        if (!token.isNullOrEmpty()) {
            request.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(request.build())
    }
}