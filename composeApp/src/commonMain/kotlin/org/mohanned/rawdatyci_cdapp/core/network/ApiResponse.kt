package org.mohanned.rawdatyci_cdapp.core.network

import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*
import org.mohanned.rawdatyci_cdapp.data.remote.dto.BaseResponse

sealed class ApiResponse<out T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error(val code: Int, val message: String, val errorCode: String? = null) : ApiResponse<Nothing>()
    data class NetworkError(val message: String) : ApiResponse<Nothing>()
}

val sharedJson = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
    isLenient = true
    encodeDefaults = false
}

suspend inline fun <reified T> safeApiCall(
    crossinline call: suspend () -> HttpResponse,
): ApiResponse<T> {
    return try {
        val response: HttpResponse = call()
        val rawBody: String = response.bodyAsText()
        val url = response.request.url.toString()

        // طباعة الـ Response للتتبع
        println("🚀 API_LOG [URL]: $url")
        println("📥 API_LOG [BODY]: $rawBody")

        if (response.status.isSuccess()) {
            try {
                val base = sharedJson.decodeFromString<BaseResponse<T>>(rawBody)
                if (base.data != null) {
                    ApiResponse.Success(base.data)
                } else {
                    @Suppress("UNCHECKED_CAST")
                    if (T::class == Unit::class) ApiResponse.Success(Unit as T)
                    else ApiResponse.Error(response.status.value, base.message ?: "Success but no data")
                }
            } catch (e: Exception) {
                try {
                    ApiResponse.Success(sharedJson.decodeFromString<T>(rawBody))
                } catch (e2: Exception) {
                    ApiResponse.Error(response.status.value, "Mapping Error: ${e2.message}")
                }
            }
        } else {
            var errorMessage = "Unknown Error"
            var errorCode: String? = null
            
            println("❌ API_LOG [ERROR CODE]: ${response.status.value}")

            try {
                val obj = sharedJson.decodeFromString<JsonObject>(rawBody)
                errorMessage = obj["message"]?.jsonPrimitive?.content 
                    ?: obj["error"]?.jsonPrimitive?.content
                    ?: obj["detail"]?.let { detail ->
                        if (detail is JsonArray) {
                            detail.joinToString(", ") { 
                                val loc = it.jsonObject["loc"]?.jsonArray?.joinToString(".") ?: ""
                                val msg = it.jsonObject["msg"]?.jsonPrimitive?.content ?: ""
                                "[$loc]: $msg"
                            }
                        } else detail.jsonPrimitive.content
                    }
                    ?: response.status.description
                
                errorCode = obj["error_code"]?.jsonPrimitive?.content
            } catch (_: Exception) {
                errorMessage = if (rawBody.isNotBlank() && rawBody.length < 500) rawBody else response.status.description
            }
            ApiResponse.Error(response.status.value, errorMessage, errorCode)
        }
    } catch (e: HttpRequestTimeoutException) {
        println("⚠️ API_LOG [TIMEOUT]")
        ApiResponse.NetworkError("Timeout")
    } catch (e: Exception) {
        println("⚠️ API_LOG [EXCEPTION]: ${e.message}")
        ApiResponse.NetworkError(e.message ?: "Network Error")
    }
}
