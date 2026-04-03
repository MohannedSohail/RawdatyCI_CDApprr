package org.mohanned.rawdatyci_cdapp.core.network.remote


object ApiConfig {
    var BASE_URL: String = "http://localhost:8000/api/v1" // Default local dev
    var tenantSlug: String = "demo"

    fun setTenant(slug: String) {
        tenantSlug = slug
        // For production, the URL might be https://$slug.rawdati.app/api/v1
        // For local development, we use the header X-Tenant-Slug
        BASE_URL = "http://localhost:8000/api/v1"
    }
}

interface TokenStorage {
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun saveTokens(access: String, refresh: String)
    suspend fun clearTokens()
}