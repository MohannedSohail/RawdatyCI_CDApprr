package org.mohanned.rawdatyci_cdapp.data.repository

import org.mohanned.rawdatyci_cdapp.core.network.ApiConfig
import org.mohanned.rawdatyci_cdapp.core.network.TokenManager
import org.mohanned.rawdatyci_cdapp.data.remote.api.AuthApiService
import org.mohanned.rawdatyci_cdapp.data.remote.dto.ApiMessageDto
import org.mohanned.rawdatyci_cdapp.data.remote.dto.toDomain
import org.mohanned.rawdatyci_cdapp.data.remote.dto.toLoggedUser
import org.mohanned.rawdatyci_cdapp.domain.model.AuthTokens
import org.mohanned.rawdatyci_cdapp.domain.model.Branding
import org.mohanned.rawdatyci_cdapp.domain.model.LoggedUser
import org.mohanned.rawdatyci_cdapp.domain.model.UserRole
import org.mohanned.rawdatyci_cdapp.domain.repository.AuthRepository
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse


class AuthRepositoryImpl(
    private val api: AuthApiService,
    private val tokenManager: TokenManager,
) : AuthRepository {

    override suspend fun getBranding(): ApiResponse<Branding> {
        return when (val response = api.getBranding()) {
            is ApiResponse.Success -> ApiResponse.Success(response.data.toDomain())
            is ApiResponse.Error -> ApiResponse.Error(response.code, response.message, response.errorCode)
            is ApiResponse.NetworkError -> ApiResponse.NetworkError(response.message)
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ): ApiResponse<Pair<LoggedUser, AuthTokens>> {
        val slug = extractSlug(email)
        ApiConfig.setTenant(slug)

        return when (val response = api.login(email, password)) {
            is ApiResponse.Success -> {
                val dto = response.data
                tokenManager.saveTokens(dto.accessToken, dto.refreshToken)
                tokenManager.saveUserInfo(
                    dto.user.id,
                    dto.user.name,
                    dto.user.email,
                    dto.user.role,
                    dto.user.avatarUrl,
                    slug
                )

                ApiResponse.Success(
                    Pair(
                        dto.user.toLoggedUser(slug),
                        AuthTokens(dto.accessToken, dto.refreshToken, dto.expiresIn)
                    )
                )
            }
            is ApiResponse.Error -> ApiResponse.Error(response.code, response.message, response.errorCode)
            is ApiResponse.NetworkError -> ApiResponse.NetworkError(response.message)
        }
    }

    override suspend fun logout(): ApiResponse<Unit> {
        return try {
            val refreshToken = tokenManager.getRefreshToken()
            if (!refreshToken.isNullOrBlank()) {
                api.logout(refreshToken)
            }
            tokenManager.logout()
            ApiResponse.Success(Unit)
        } catch (e: Exception) {
            tokenManager.logout()
            ApiResponse.Success(Unit)
        }
    }

    override suspend fun forgotPassword(email: String): ApiResponse<ApiMessageDto> {
        return api.forgotPassword(email)
    }

    override suspend fun verifyOtp(email: String, otp: String): ApiResponse<String> {
        return when (val response = api.verifyOtp(email, otp)) {
            is ApiResponse.Success -> ApiResponse.Success(response.data.resetToken)
            is ApiResponse.Error -> ApiResponse.Error(response.code, response.message, response.errorCode)
            is ApiResponse.NetworkError -> ApiResponse.NetworkError(response.message)
        }
    }

    override suspend fun resetPassword(
        resetToken: String,
        newPassword: String,
        confirm: String
    ): ApiResponse<ApiMessageDto> {
        return api.resetPassword(resetToken, newPassword, confirm)
    }

    override suspend fun getLoggedUser(): LoggedUser? {
        val userId = tokenManager.getUserId() ?: return null
        val userName = tokenManager.getUserName() ?: ""
        val userEmail = tokenManager.getUserEmail() ?: ""
        val userRole = tokenManager.getUserRole() ?: "parent"
        val avatarUrl = tokenManager.getAvatarUrl()
        val tenantSlug = tokenManager.getTenantSlug()

        return LoggedUser(
            id = userId,
            name = userName,
            email = userEmail,
            role = userRole.toUserRoleEnum(),
            avatarUrl = avatarUrl,
            tenantSlug = tenantSlug
        )
    }

    private fun extractSlug(email: String): String {
        val domain = email.substringAfter("@", "")
        return when {
            domain.contains(".rawdati.app") || domain.contains(".rawdaty.app") -> domain.substringBefore(".")
            domain.contains("localhost") || domain.isEmpty() -> "demo"
            else -> domain.split(".").firstOrNull() ?: "demo"
        }
    }

    private fun String.toUserRoleEnum(): UserRole {
        return when (this.lowercase()) {
            "super_admin" -> UserRole.SUPER_ADMIN
            "admin" -> UserRole.ADMIN
            "teacher" -> UserRole.TEACHER
            else -> UserRole.PARENT
        }
    }
}
