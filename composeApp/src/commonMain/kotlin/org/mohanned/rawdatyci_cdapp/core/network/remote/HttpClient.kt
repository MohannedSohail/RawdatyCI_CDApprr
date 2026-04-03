package org.mohanned.rawdatyci_cdapp.core.network.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.mohanned.rawdatyci_cdapp.data.remote.dto.BaseResponse

fun buildHttpClient(tokenStorage: TokenStorage): HttpClient = HttpClient {

    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            coerceInputValues  = true
            isLenient          = true
            encodeDefaults     = false
        })
    }

    install(Logging) {
        logger = Logger.DEFAULT
        level  = LogLevel.BODY
        sanitizeHeader { header ->
            header == HttpHeaders.Authorization
        }
    }

    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
        connectTimeoutMillis = 15_000
        socketTimeoutMillis  = 30_000
    }

    install(Auth) {
        bearer {
            loadTokens {
                val access  = tokenStorage.getAccessToken()
                val refresh = tokenStorage.getRefreshToken()
                if (access != null && refresh != null) {
                    BearerTokens(access, refresh)
                } else null
            }

            refreshTokens {
                val refresh = tokenStorage.getRefreshToken()
                    ?: return@refreshTokens null
                try {
                    // Note: This needs to be calibrated based on the final Auth API structure
                    val response = client.post("${ApiConfig.BASE_URL}/auth/refresh") {
                        contentType(ContentType.Application.Json)
                        setBody(mapOf("refresh_token" to refresh))
                        markAsRefreshTokenRequest()
                    }
                    if (response.status.isSuccess()) {
                        null // TODO: Implement token parsing
                    } else {
                        tokenStorage.clearTokens()
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }

            sendWithoutRequest { request ->
                !request.url.pathSegments.any {
                    it == "login" || it == "refresh" || it == "branding"
                }
            }
        }
    }

    defaultRequest {
        header(HttpHeaders.AcceptLanguage, "ar")
        header(HttpHeaders.Accept, "application/json")
        header("X-Tenant-Slug", ApiConfig.tenantSlug)
    }
}

suspend inline fun <reified T> safeApiCall(
    call: () -> HttpResponse,
): ApiResponse<T> {
    return try {
        val response = call()
        val baseResponse = response.body<BaseResponse<T>>()
        
        when {
            response.status.isSuccess() && baseResponse.success -> {
                baseResponse.data?.let {
                    ApiResponse.Success(it)
                } ?: ApiResponse.Error(
                    code = response.status.value,
                    message = baseResponse.message ?: "تم تسجيل العملية بنجاح"
                )
            }
            !baseResponse.success -> {
                ApiResponse.Error(
                    code = response.status.value,
                    message = baseResponse.message ?: response.status.description,
                    errorCode = baseResponse.error
                )
            }
            else -> {
                ApiResponse.Error(
                    code    = response.status.value,
                    message = response.status.description,
                )
            }
        }
    } catch (e: HttpRequestTimeoutException) {
        ApiResponse.NetworkError("انتهت مهلة الطلب")
    } catch (e: Exception) {
        ApiResponse.NetworkError(e.message ?: "خطأ في الشبكة")
    }
}