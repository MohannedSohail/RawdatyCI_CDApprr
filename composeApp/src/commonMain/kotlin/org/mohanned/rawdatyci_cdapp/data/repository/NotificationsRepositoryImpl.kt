package org.mohanned.rawdatyci_cdapp.data.repository

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.data.remote.api.NotificationsApiService
import org.mohanned.rawdatyci_cdapp.data.remote.dto.toDomain
import org.mohanned.rawdatyci_cdapp.data.remote.dto.toPaginated
import org.mohanned.rawdatyci_cdapp.domain.model.AppNotification
import org.mohanned.rawdatyci_cdapp.domain.model.PaginatedResult
import org.mohanned.rawdatyci_cdapp.domain.repository.NotificationsRepository

class NotificationsRepositoryImpl(
    private val api: NotificationsApiService
) : NotificationsRepository {
    override suspend fun getNotifications(page: Int, isRead: Boolean?): ApiResponse<PaginatedResult<AppNotification>> {
        return try {
            val response = api.getNotifications(isRead, page)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toPaginated { it.toDomain() })
            } else {
                response as ApiResponse<PaginatedResult<AppNotification>>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun markRead(id: String): ApiResponse<Unit> {
        return try {
            api.markAsRead(id)
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun markAllRead(): ApiResponse<Unit> {
        return try {
            api.markAllAsRead()
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun sendNotification(title: String, body: String, target: String, classId: String?): ApiResponse<Unit> {
        return try {
            api.sendNotification(title, body, target, classId)
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun registerFcmToken(fcmToken: String, deviceType: String): ApiResponse<Unit> {
        return try {
            api.updateFcmToken(fcmToken, deviceType)
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }
}
