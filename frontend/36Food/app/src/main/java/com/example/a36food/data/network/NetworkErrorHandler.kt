package com.example.a36food.data.network

import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

/*
NetworkErrorHandler phục vụ một số mục đích chính:
Tập trung xử lý lỗi: Tập trung logic xử lý lỗi mạng trên toàn bộ ứng dụng của bạn
Kiểm tra mạng khi lỗi: Chỉ kiểm tra kết nối mạng khi yêu cầu không thành công
Phát hiện lỗi thông minh: Phân biệt giữa các sự cố kết nối và các lỗi API khác
Cơ chế gọi lại: Chỉ kích hoạt NoConnectionScreen khi thực sự không có kết nối
 */

@Singleton
class NetworkErrorHandler @Inject constructor(
    private val networkConnectionManager: NetworkConnectionManager
) {
    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T,
        onNetworkError: () -> Unit
    ): Result<T> {
        return try {
            Result.success(apiCall())
        } catch (e: Exception) {
            // Only call onNetworkError for actual network issues
            when (e) {
                is NoConnectionException,
                is SocketTimeoutException,
                is UnknownHostException,
                is ConnectException -> onNetworkError()
                else -> {
                    android.util.Log.e("NetworkErrorHandler", "Non-network error: ${e.message}", e)
                }
            }
            Result.failure(e)
        }
    }
}

class NoConnectionException : Exception("No internet connection available")