package com.example.a36food.data.repository

import com.example.a36food.data.network.NetworkConnectionManager
import com.example.a36food.domain.model.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

abstract class BaseRepository {

    @Inject
    lateinit var networkConnectionManager: NetworkConnectionManager

    protected fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): Flow<Result<T>> = flow {
        emit(Result.Loading)

        if (!networkConnectionManager.checkNetworkAvailability()) {
            emit(Result.Error("No internet connection"))
            return@flow
        }

        try {
            val response = apiCall()
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An unknown error occurred"))
        }
    }.flowOn(Dispatchers.IO)
}