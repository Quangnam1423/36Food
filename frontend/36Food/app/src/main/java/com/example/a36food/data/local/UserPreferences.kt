package com.example.a36food.data.local

import android.content.SharedPreferences
import com.example.a36food.domain.model.responses.TokenResponse
import javax.inject.Inject

class UserPreferences @Inject constructor(
    private val preferences: SharedPreferences
) {
    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }

    fun saveTokens(tokenResponse: TokenResponse) {
        with(preferences.edit()) {
            putString(KEY_ACCESS_TOKEN, tokenResponse.accessToken)
            putString(KEY_REFRESH_TOKEN, tokenResponse.refreshToken)
            apply()
        }
    }

    fun getTokens(): TokenResponse? {
        val accessToken = preferences.getString(KEY_ACCESS_TOKEN, null)
        val refreshToken = preferences.getString(KEY_REFRESH_TOKEN, null)

        return if (accessToken != null && refreshToken != null) {
            TokenResponse(accessToken, refreshToken)
        } else {
            null
        }
    }

    fun getAccessToken(): String? = preferences.getString(KEY_ACCESS_TOKEN, null)

    fun getRefreshToken(): String? = preferences.getString(KEY_REFRESH_TOKEN, null)

    fun hasValidSession(): Boolean {
        val accessToken = preferences.getString(KEY_ACCESS_TOKEN, null)
        val refreshToken = preferences.getString(KEY_REFRESH_TOKEN, null)
        return !accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty()
    }

    fun clearTokens() {
        preferences.edit().clear().apply()
    }

    fun updateTokens(accessToken: String, refreshToken: String) {
        with(preferences.edit()) {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            apply()
        }
    }
}