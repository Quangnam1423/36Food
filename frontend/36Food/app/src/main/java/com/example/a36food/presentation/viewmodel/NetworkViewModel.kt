package com.example.a36food.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.a36food.data.network.NetworkConnectionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NetworkViewModel @Inject constructor(
    private val networkConnectionManager: NetworkConnectionManager
) : ViewModel() {

    fun isNetworkAvailable(): Boolean {
        return networkConnectionManager.checkNetworkAvailability()
    }
}