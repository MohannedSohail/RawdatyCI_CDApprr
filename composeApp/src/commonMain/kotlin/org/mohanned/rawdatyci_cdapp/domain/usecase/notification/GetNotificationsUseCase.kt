package org.mohanned.rawdatyci_cdapp.domain.usecase.notification

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.AppNotification
import org.mohanned.rawdatyci_cdapp.domain.model.PaginatedResult
import org.mohanned.rawdatyci_cdapp.domain.repository.NotificationsRepository

class GetNotificationsUseCase(private val repository: NotificationsRepository) {
    operator fun invoke(page: Int = 1, isRead: Boolean? = null): Flow<UiState<PaginatedResult<AppNotification>>> = flow {
        emit(UiState.Loading)
        when (val result = repository.getNotifications(page, isRead)) {
            is ApiResponse.Success -> emit(UiState.Success(result.data))
            is ApiResponse.Error -> emit(UiState.Error(result.message))
            is ApiResponse.NetworkError -> emit(UiState.Error("لا يوجد اتصال بالإنترنت"))
        }
    }
}
