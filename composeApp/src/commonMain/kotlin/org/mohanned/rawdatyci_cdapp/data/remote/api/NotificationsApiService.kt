package org.mohanned.rawdatyci_cdapp.data.remote.api
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiConfig
import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.network.remote.safeApiCall
import org.mohanned.rawdatyci_cdapp.data.remote.dto.ApiListDto
import org.mohanned.rawdatyci_cdapp.data.remote.dto.ApiMessageDto
import org.mohanned.rawdatyci_cdapp.data.remote.dto.NotificationDto

class NotificationsApiService(private val client: HttpClient) {

    // GET /api/v1/notifications
    suspend fun getNotifications(
        page: Int = 1,
    ): ApiResponse<ApiListDto<NotificationDto>> =
        safeApiCall {
            client.get("${ApiConfig.BASE_URL}/notifications") {
                parameter("page", page)
            }
        }

    // PATCH /api/v1/notifications/:id/read
    suspend fun markRead(
        id: Int,
    ): ApiResponse<ApiMessageDto> =
        safeApiCall {
            client.patch("${ApiConfig.BASE_URL}/notifications/$id/read")
        }

    // POST /api/v1/notifications/read-all
    suspend fun markAllRead(): ApiResponse<ApiMessageDto> =
        safeApiCall {
            client.post("${ApiConfig.BASE_URL}/notifications/read-all")
        }

    // POST /api/v1/notifications/send
    suspend fun sendNotification(
        title: String,
        body: String,
        target: String = "all",
        classId: Int? = null,
    ): ApiResponse<ApiMessageDto> =
        safeApiCall {
            client.post("${ApiConfig.BASE_URL}/notifications/send") {
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "title"    to title,
                    "body"     to body,
                    "target"   to target,
                    "class_id" to classId,
                ))
            }
        }

    // POST /api/v1/notifications/fcm-token
    suspend fun updateFcmToken(
        token: String,
        platform: String,
    ): ApiResponse<ApiMessageDto> =
        safeApiCall {
            client.post("${ApiConfig.BASE_URL}/notifications/fcm-token") {
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "fcm_token" to token,
                    "platform"  to platform,
                ))
            }
        }
}