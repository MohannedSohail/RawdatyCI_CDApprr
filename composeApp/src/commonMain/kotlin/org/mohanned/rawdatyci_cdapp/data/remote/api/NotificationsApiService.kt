package org.mohanned.rawdatyci_cdapp.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.*
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.network.safeApiCall
import org.mohanned.rawdatyci_cdapp.data.remote.dto.*

interface NotificationsApiService {
    suspend fun getNotifications(isRead: Boolean?, page: Int): ApiResponse<ApiListDto<NotificationDto>>
    suspend fun markAsRead(id: String): ApiResponse<Unit>
    suspend fun markAllAsRead(): ApiResponse<Unit>
    suspend fun sendNotification(title: String, body: String, target: String, classId: String?): ApiResponse<Unit>
    suspend fun updateFcmToken(token: String, deviceType: String): ApiResponse<Unit>
}

class NotificationsApiServiceImpl(private val client: HttpClient) : NotificationsApiService {
    override suspend fun getNotifications(isRead: Boolean?, page: Int): ApiResponse<ApiListDto<NotificationDto>> = safeApiCall {
        client.get("notifications") {
            parameter("is_read", isRead)
            parameter("page", page)
        }
    }

    override suspend fun markAsRead(id: String): ApiResponse<Unit> = safeApiCall {
        client.patch("notifications/$id/read")
    }

    override suspend fun markAllAsRead(): ApiResponse<Unit> = safeApiCall {
        client.post("notifications/read-all")
    }

    override suspend fun sendNotification(title: String, body: String, target: String, classId: String?): ApiResponse<Unit> = safeApiCall {
        client.post("notifications/send") {
            contentType(ContentType.Application.Json)
            setBody(mapOf(
                "title" to title,
                "body" to body,
                "target" to target,
                "class_id" to classId
            ))
        }
    }

    override suspend fun updateFcmToken(token: String, deviceType: String): ApiResponse<Unit> = safeApiCall {
        client.post("notifications/fcm-token") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("token" to token, "device_type" to deviceType))
        }
    }
}
