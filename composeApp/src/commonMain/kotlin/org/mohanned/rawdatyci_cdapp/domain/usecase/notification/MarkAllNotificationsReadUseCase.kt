package org.mohanned.rawdatyci_cdapp.domain.usecase.notification

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.repository.NotificationsRepository

class MarkAllNotificationsReadUseCase(private val repository: NotificationsRepository) {
    suspend operator fun invoke(): Result<Unit> {
        return when (val result = repository.markAllRead()) {
            is ApiResponse.Success -> Result.success(Unit)
            is ApiResponse.Error -> Result.failure(Exception(result.message))
            is ApiResponse.NetworkError -> Result.failure(Exception(result.message))
        }
    }
}
