package org.mohanned.rawdatyci_cdapp.core.network

interface TokenStorage {
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun saveTokens(access: String, refresh: String)
    suspend fun clearTokens()
}
