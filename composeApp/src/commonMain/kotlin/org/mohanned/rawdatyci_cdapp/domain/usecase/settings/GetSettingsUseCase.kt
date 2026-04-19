package org.mohanned.rawdatyci_cdapp.domain.usecase.settings

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.KindergartenSettings
import org.mohanned.rawdatyci_cdapp.domain.repository.SettingsRepository

class GetSettingsUseCase(private val repository: SettingsRepository) {
    operator fun invoke(): Flow<UiState<KindergartenSettings>> = flow {
        emit(UiState.Loading)
        when (val result = repository.getSettings()) {
            is ApiResponse.Success -> emit(UiState.Success(result.data))
            is ApiResponse.Error -> emit(UiState.Error(result.message))
            is ApiResponse.NetworkError -> emit(UiState.Error("لا يوجد اتصال بالإنترنت"))
        }
    }
}
