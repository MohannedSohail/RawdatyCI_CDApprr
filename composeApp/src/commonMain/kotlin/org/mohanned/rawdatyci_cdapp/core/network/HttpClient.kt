package org.mohanned.rawdatyci_cdapp.core.network

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

fun buildHttpClient(tokenManager: TokenManager): HttpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
        })
    }
    install(Logging) {
        level = LogLevel.BODY
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
    }
    
    install(Auth) {
        bearer {
            loadTokens {
                tokenManager.getAccessToken()?.let { BearerTokens(it, "") }
            }
            refreshTokens { null }
            sendWithoutRequest { request ->
                // إرسال التوكين في كل الطلبات ما عدا المسارات التالية
                val path = request.url.encodedPath
                !(path.contains("/auth/login") || 
                  path.contains("/branding") || 
                  path.contains("/auth/forgot-password"))
            }
        }
    }
    
    defaultRequest {
        url(ApiConfig.BASE_URL)
        header("Accept-Language", "ar")
    }
}.apply {
    plugin(HttpSend).intercept { request ->
        val tenant = tokenManager.getTenantSlug()
        request.header("X-Tenant-Slug", tenant)
        execute(request)
    }
}
