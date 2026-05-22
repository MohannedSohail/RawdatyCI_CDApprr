package org.mohanned.rawdatyci_cdapp.core.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.mohanned.rawdatyci_cdapp.data.local.AppPreferences

class TokenManager(
    private val prefs: AppPreferences
) : TokenStorage {

    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedInFlow: StateFlow<Boolean?> = _isLoggedIn.asStateFlow()

    override suspend fun getAccessToken(): String? = prefs.getAccessToken()
    override suspend fun getRefreshToken(): String? = prefs.getRefreshToken()
    
    override suspend fun saveTokens(access: String, refresh: String) {
        prefs.saveTokens(access, refresh)
        _isLoggedIn.value = true
    }

    override suspend fun clearTokens() {
        prefs.clearTokens()
        _isLoggedIn.value = false
    }

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
        _isLoggedIn.value = true
    }

    suspend fun checkInitialAuth() {
        _isLoggedIn.value = !prefs.getAccessToken().isNullOrBlank()
    }

    suspend fun isLoggedIn(): Boolean = !getAccessToken().isNullOrBlank()

    suspend fun logout() {
        prefs.clearAll()
        ApiConfig.setTenant("demo")
        _isLoggedIn.value = false
    }
}
