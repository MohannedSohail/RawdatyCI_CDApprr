package org.mohanned.rawdatyci_cdapp.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.network.safeApiCall
import org.mohanned.rawdatyci_cdapp.data.remote.dto.ApiMessageDto
import org.mohanned.rawdatyci_cdapp.data.remote.dto.AuthResponseDto
import org.mohanned.rawdatyci_cdapp.data.remote.dto.BrandingDto
import org.mohanned.rawdatyci_cdapp.data.remote.dto.VerifyOtpResponseDto

interface AuthApiService {
    suspend fun getBranding(): ApiResponse<BrandingDto>
    suspend fun login(email: String, password: String): ApiResponse<AuthResponseDto>
    suspend fun refreshToken(refreshToken: String): ApiResponse<AuthResponseDto>
    suspend fun forgotPassword(email: String): ApiResponse<ApiMessageDto>
    suspend fun verifyOtp(email: String, otp: String): ApiResponse<VerifyOtpResponseDto>
    suspend fun resetPassword(resetToken: String, newPassword: String, confirmPassword: String): ApiResponse<ApiMessageDto>
    suspend fun logout(refreshToken: String): ApiResponse<ApiMessageDto>
}

class AuthApiServiceImpl(private val client: HttpClient) : AuthApiService {

    override suspend fun getBranding(): ApiResponse<BrandingDto> = safeApiCall {
        client.get("branding")
    }

    override suspend fun login(email: String, password: String): ApiResponse<AuthResponseDto> = safeApiCall {
        client.post("auth/login") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("email" to email, "password" to password))
        }
    }

    override suspend fun refreshToken(refreshToken: String): ApiResponse<AuthResponseDto> = safeApiCall {
        client.post("auth/refresh") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("refresh_token" to refreshToken))
        }
    }

    override suspend fun forgotPassword(email: String): ApiResponse<ApiMessageDto> = safeApiCall {
        client.post("auth/forgot-password") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("email" to email))
        }
    }

    override suspend fun verifyOtp(email: String, otp: String): ApiResponse<VerifyOtpResponseDto> = safeApiCall {
        client.post("auth/verify-otp") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("email" to email, "otp" to otp))
        }
    }

    override suspend fun resetPassword(
        resetToken: String,
        newPassword: String,
        confirmPassword: String
    ): ApiResponse<ApiMessageDto> = safeApiCall {
        client.post("auth/reset-password") {
            contentType(ContentType.Application.Json)
            setBody(mapOf(
                "reset_token" to resetToken,
                "password" to newPassword,
                "password_confirmation" to confirmPassword
            ))
        }
    }

    override suspend fun logout(refreshToken: String): ApiResponse<ApiMessageDto> = safeApiCall {
        client.post("auth/logout") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("refresh_token" to refreshToken))
        }
    }
}
