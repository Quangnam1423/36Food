package com.example.a36food.data.api

import com.example.a36food.data.dto.ChangePasswordRequest
import com.example.a36food.data.dto.LoginRequest
import com.example.a36food.data.dto.LoginResponse
import com.example.a36food.domain.model.User
import com.example.a36food.data.dto.RegisterRequest
import com.example.a36food.data.dto.RegisterResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApi {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("auth/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<User>

    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<RegisterResponse>

    @POST("auth/token-login")
    suspend fun loginWithToken(@Header("Authorization") token: String): Response<LoginResponse>

    @GET("user/get-address")
    suspend fun getUserAddress(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ) : Response<String>

    @POST("user/change-password")
    suspend fun changePassword (
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ) : ResponseBody
}