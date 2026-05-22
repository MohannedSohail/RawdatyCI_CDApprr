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
            encodeDefaults = false
        })
    }
    install(Logging) {
        level = LogLevel.BODY
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
        connectTimeoutMillis = 30_000
    }

    install(Auth) {
        bearer {
            loadTokens {
                tokenManager.getAccessToken()?.let { BearerTokens(it, "") }
            }
            refreshTokens { null }
            sendWithoutRequest { request ->
                val path = request.url.encodedPath
                // نرسل بدون Token فقط للمسارات العامة
                path.contains("/auth/") || path.contains("/branding")
            }
        }
    }

    // مراقبة الاستجابة للتعامل مع انتهاء صلاحية الـ Token (401)
    HttpResponseValidator {
        validateResponse { response ->
            if (response.status == HttpStatusCode.Unauthorized) {
                // استلام 401 يعني أن الـ Token انتهى أو غير صالح
                // نقوم بتفريغ كافة البيانات المسجلة فوراً
                tokenManager.logout()
            }
        }
    }

    defaultRequest {
        url(ApiConfig.BASE_URL)
        header(HttpHeaders.Accept, "application/json")
        header("Accept-Language", "ar")
    }
}.apply {
    plugin(HttpSend).intercept { request ->
        // إضافة Tenant Slug لكل الطلبات
        val tenant = tokenManager.getTenantSlug()
        if (tenant.isNotEmpty()) {
            request.header("X-Tenant-Slug", tenant)
        }

        // كإجراء أمان إضافي للطلبات المحمية
        if (!request.url.encodedPath.contains("/auth/") && !request.url.encodedPath.contains("/branding")) {
            tokenManager.getAccessToken()?.let { token ->
                if (!request.headers.contains(HttpHeaders.Authorization)) {
                    request.header(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }

        execute(request)
    }
}
