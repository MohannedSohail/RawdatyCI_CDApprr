package org.mohanned.rawdatyci_cdapp.core.network

import org.mohanned.rawdatyci_cdapp.data.local.AppPreferences

class TokenManager(
    private val prefs: AppPreferences
) : TokenStorage {

    override suspend fun getAccessToken(): String? = prefs.getAccessToken()
    override suspend fun getRefreshToken(): String? = prefs.getRefreshToken()
    override suspend fun saveTokens(access: String, refresh: String) = prefs.saveTokens(access, refresh)
    override suspend fun clearTokens() = prefs.clearTokens()

    suspend fun getTenantSlug(): String = prefs.getTenantSlug() ?: ApiConfig.tenantSlug
    
    suspend fun getUserId(): String? = prefs.getUserId()
    suspend fun getUserName(): String? = prefs.getUserName()
    suspend fun getUserEmail(): String? = prefs.getUserEmail()
    suspend fun getUserRole(): String? = prefs.getUserRole()
    suspend fun getAvatarUrl(): String? = prefs.getAvatarUrl()

    suspend fun saveUserInfo(
        userId: String,
        name: String,
        email: String,
        role: String,
        avatarUrl: String?,
        tenantSlug: String
    ) {
        prefs.saveUserInfo(userId, name, email, role, avatarUrl, tenantSlug)
        ApiConfig.setTenant(tenantSlug)
    }

    suspend fun isLoggedIn(): Boolean = !getAccessToken().isNullOrBlank()

    suspend fun logout() {
        prefs.clearAll()
        ApiConfig.setTenant("demo")
    }
}
