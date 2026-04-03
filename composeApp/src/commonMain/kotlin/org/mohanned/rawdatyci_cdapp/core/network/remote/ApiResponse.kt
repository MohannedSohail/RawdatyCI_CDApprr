package org.mohanned.rawdatyci_cdapp.core.network.remote


sealed class ApiResponse<out T> {
    data class Success<T>(val data: T)    : ApiResponse<T>()
    data class Error(
        val code: Int,
        val message: String,
        val errorCode: String? = null,
    ) : ApiResponse<Nothing>()
    data class NetworkError(val message: String) : ApiResponse<Nothing>()
}