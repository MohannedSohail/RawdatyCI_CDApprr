package org.mohanned.rawdatyci_cdapp.domain.repository

import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiResponse
import org.mohanned.rawdatyci_cdapp.data.local.AppPreferences
import org.mohanned.rawdatyci_cdapp.data.remote.api.AuthApiService
import org.mohanned.rawdatyci_cdapp.domain.model.AuthTokens
import org.mohanned.rawdatyci_cdapp.domain.model.LoggedUser
import org.mohanned.rawdatyci_cdapp.domain.model.UserRole


class AuthRepositoryImpl(
    private val api: AuthApiService,
    private val prefs: AppPreferences,
) : AuthRepository {

    override suspend fun login(
        identifier: String, // e.g., mohanned.teacher.amal.com
        password: String,
    ): ApiResponse<Pair<LoggedUser, AuthTokens>> {
        // ── Parse Identifier ────────────────────────────
        // Identifier format: [username].[role].[slug].com or similar
        val parts = identifier.split(".")
        val slug = if (parts.size >= 3) {
            parts[parts.size - 2] // Extracting 'amal' from mohanned.teacher.amal.com
        } else {
            "demo"
        }

        // Update API Config for this tenant
        org.mohanned.rawdatyci_cdapp.core.network.remote.ApiConfig.setTenant(slug)

        return when (val result = api.login(identifier, password)) {
            is ApiResponse.Success -> {
                val dto = result.data

                // حفظ التوكن
                prefs.saveTokens(
                    access  = dto.accessToken,
                    refresh = dto.refreshToken,
                )

                // حفظ بيانات المستخدم
                prefs.saveUserInfo(
                    id          = dto.user.id,
                    name        = dto.user.name,
                    email       = dto.user.email,
                    role        = dto.user.role,
                    tenantSlug  = slug,
                )

                val user = LoggedUser(
                    id          = dto.user.id,
                    name        = dto.user.name,
                    email       = dto.user.email,
                    role        = when (dto.user.role) {
                        "super_admin" -> UserRole.SUPER_ADMIN
                        "admin"       -> UserRole.ADMIN
                        "teacher"     -> UserRole.TEACHER
                        else          -> UserRole.PARENT
                    },
                    avatarUrl   = dto.user.avatarUrl,
                    tenantSlug  = slug,
                )

                val tokens = AuthTokens(
                    accessToken  = dto.accessToken,
                    refreshToken = dto.refreshToken,
                    expiresIn    = dto.expiresIn,
                )

                ApiResponse.Success(Pair(user, tokens))
            }
            is ApiResponse.Error ->
                ApiResponse.Error(result.code, result.message)
            is ApiResponse.NetworkError ->
                ApiResponse.NetworkError(result.message)
        }
    }

    override suspend fun logout(): ApiResponse<Unit> {
        prefs.clearAll()
        return try {
            api.logout()
            ApiResponse.Success(Unit)
        } catch (e: Exception) {
            ApiResponse.Success(Unit)
        }
    }

    override suspend fun forgotPassword(
        email: String,
    ): ApiResponse<Unit> {
        return when (val r = api.forgotPassword(email)) {
            is ApiResponse.Success      -> ApiResponse.Success(Unit)
            is ApiResponse.Error        -> ApiResponse.Error(r.code, r.message)
            is ApiResponse.NetworkError -> ApiResponse.NetworkError(r.message)
        }
    }

    override suspend fun verifyOtp(
        email: String,
        otp: String,
    ): ApiResponse<Unit> {
        return when (val r = api.verifyOtp(email, otp)) {
            is ApiResponse.Success      -> ApiResponse.Success(Unit)
            is ApiResponse.Error        -> ApiResponse.Error(r.code, r.message)
            is ApiResponse.NetworkError -> ApiResponse.NetworkError(r.message)
        }
    }

    override suspend fun resetPassword(
        email: String,
        otp: String,
        newPassword: String,
        confirmPassword: String,
    ): ApiResponse<Unit> {
        return when (val r = api.resetPassword(email, otp, newPassword, confirmPassword)) {
            is ApiResponse.Success      -> ApiResponse.Success(Unit)
            is ApiResponse.Error        -> ApiResponse.Error(r.code, r.message)
            is ApiResponse.NetworkError -> ApiResponse.NetworkError(r.message)
        }
    }
}