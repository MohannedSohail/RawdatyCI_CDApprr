package org.mohanned.rawdatyci_cdapp.data.remote.api

import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiConfig
import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.network.remote.safeApiCall
import org.mohanned.rawdatyci_cdapp.data.remote.dto.ApiMessageDto
import org.mohanned.rawdatyci_cdapp.data.remote.dto.AuthResponseDto
import org.mohanned.rawdatyci_cdapp.data.remote.dto.BrandingDto
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthApiService(private val client: HttpClient) {

    // GET /api/v1/branding
    suspend fun getBranding(): ApiResponse<BrandingDto> =
        safeApiCall {
            client.get("${ApiConfig.BASE_URL}/branding")
        }

    // POST /api/v1/auth/login
    suspend fun login(
        identifier: String,
        password: String,
    ): ApiResponse<AuthResponseDto> =
        safeApiCall {
            client.post("${ApiConfig.BASE_URL}/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "username" to identifier, // Django/Ninja often uses username field
                    "password" to password,
                ))
            }
        }

    // POST /api/v1/auth/refresh
    suspend fun refreshToken(
        refreshToken: String,
    ): ApiResponse<AuthResponseDto> =
        safeApiCall {
            client.post("${ApiConfig.BASE_URL}/auth/refresh") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("refresh_token" to refreshToken))
            }
        }

    // POST /api/v1/auth/forgot-password
    suspend fun forgotPassword(
        identifier: String,
    ): ApiResponse<ApiMessageDto> =
        safeApiCall {
            client.post("${ApiConfig.BASE_URL}/auth/forgot-password") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("identifier" to identifier))
            }
        }

    // POST /api/v1/auth/verify-otp
    suspend fun verifyOtp(
        identifier: String,
        otp: String,
    ): ApiResponse<ApiMessageDto> =
        safeApiCall {
            client.post("${ApiConfig.BASE_URL}/auth/verify-otp") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("identifier" to identifier, "otp" to otp))
            }
        }

    // POST /api/v1/auth/reset-password
    suspend fun resetPassword(
        identifier: String,
        otp: String,
        newPassword: String,
        confirmPassword: String,
    ): ApiResponse<ApiMessageDto> =
        safeApiCall {
            client.post("${ApiConfig.BASE_URL}/auth/reset-password") {
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "identifier"            to identifier,
                    "otp"                   to otp,
                    "password"              to newPassword,
                    "password_confirmation" to confirmPassword,
                ))
            }
        }

    // POST /api/v1/auth/logout
    suspend fun logout(): ApiResponse<ApiMessageDto> =
        safeApiCall {
            client.post("${ApiConfig.BASE_URL}/auth/logout")
        }
}