package com.example.a36food.data.api

import com.example.a36food.data.dto.LoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {
    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest) : Response<String>
}