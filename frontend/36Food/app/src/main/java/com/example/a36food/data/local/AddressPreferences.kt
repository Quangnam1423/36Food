package com.example.a36food.data.local

import android.content.SharedPreferences
import com.example.a36food.domain.model.Address
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class AddressPreferences @Inject constructor(
    private val preferences: SharedPreferences
) {
    companion object {
        private const val KEY_ADDRESSES = "user_addresses"
    }

    fun saveAddresses(addresses: List<Address>) {
        val json = Gson().toJson(addresses)
        preferences.edit().putString(KEY_ADDRESSES, json).apply()
    }

    fun getAddresses(): List<Address> {
        val json = preferences.getString(KEY_ADDRESSES, null)
        return if (json != null) {
            Gson().fromJson(json, object : TypeToken<List<Address>>() {}.type)
        } else {
            emptyList()
        }
    }

    fun clearAddresses() {
        preferences.edit().remove(KEY_ADDRESSES).apply()
    }
}