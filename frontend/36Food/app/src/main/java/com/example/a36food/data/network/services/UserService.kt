package com.example.a36food.data.network.services

import com.example.a36food.domain.model.requests.RegisterRequest
import com.example.a36food.domain.model.User
import com.example.a36food.domain.model.requests.ChangePasswordRequest
import com.example.a36food.domain.model.requests.ForgotPasswordRequest
import com.example.a36food.domain.model.requests.LoginWithCredentialsRequest
import com.example.a36food.domain.model.requests.RefeshTokenRequest
import com.example.a36food.domain.model.requests.ResetPasswordRequest
import com.example.a36food.domain.model.requests.VerifyCodeRequest
import com.example.a36food.domain.model.responses.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface UserService {
    @POST("auth/login")
    suspend fun loginWithCredentials(@Body request: LoginWithCredentialsRequest): Response<TokenResponse>

    @POST("auth/login/token")
    suspend fun loginWithToken(@Header("Authorization") token: String): Response<TokenResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<TokenResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefeshTokenRequest): Response<TokenResponse>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<Unit>

    @POST("auth/verify-code")
    suspend fun verifyCode(@Body request: VerifyCodeRequest): Response<Unit>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Unit>

    @POST("auth/change-password")
    suspend fun changePassword(@Header("Authorization") token: String,@Body request: ChangePasswordRequest): Response<Unit>

    @GET("user/profile")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<User>

    @POST("auth/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<Unit>
}