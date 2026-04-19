package org.mohanned.rawdatyci_cdapp.domain.usecase.notification

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.repository.NotificationsRepository

class SendNotificationUseCase(private val repository: NotificationsRepository) {
    suspend operator fun invoke(
        title: String,
        body: String,
        target: String,
        classId: String? = null
    ): Result<Unit> {
        return when (val result = repository.sendNotification(title, body, target, classId)) {
            is ApiResponse.Success -> Result.success(Unit)
            is ApiResponse.Error -> Result.failure(Exception(result.message))
            is ApiResponse.NetworkError -> Result.failure(Exception(result.message))
        }
    }
}
