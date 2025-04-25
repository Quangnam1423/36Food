package com.example.a36food.data.repository

import com.example.a36food.data.local.AddressPreferences
import com.example.a36food.data.network.base.ApiResponse
import com.example.a36food.data.network.base.BaseRepository
import com.example.a36food.data.network.services.AddressService
import com.example.a36food.domain.model.Address
import javax.inject.Inject

class AddressRepository @Inject constructor(
    private val api: AddressService,
    private val addressPreferences: AddressPreferences
) : BaseRepository() {

    suspend fun getUserAddresses(): ApiResponse<List<Address>> {
        // Trả về địa chỉ local trước
        val localAddresses = addressPreferences.getAddresses()
        if (localAddresses.isNotEmpty()) {
            return ApiResponse.Success(localAddresses)
        }

        // Sau đó fetch từ backend và lưu local
        return safeApiCall {
            api.getUserAddresses()
        }.also { response ->
            if (response is ApiResponse.Success) {
                addressPreferences.saveAddresses(response.data)
            }
        }
    }

    suspend fun addAddress(address: Address): ApiResponse<Address> {
        return safeApiCall {
            api.addAddress(address)
        }.also { response ->
            if (response is ApiResponse.Success) {
                // Cập nhật cache local
                val currentAddresses = addressPreferences.getAddresses().toMutableList()
                currentAddresses.add(response.data)
                addressPreferences.saveAddresses(currentAddresses)
            }
        }
    }
}