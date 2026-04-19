package org.mohanned.rawdatyci_cdapp.domain.usecase.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.LoggedUser
import org.mohanned.rawdatyci_cdapp.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    operator fun invoke(email: String, password: String): Flow<UiState<LoggedUser>> = flow {
        emit(UiState.Loading)
        when (val result = repository.login(email, password)) {
            is ApiResponse.Success -> emit(UiState.Success(result.data.first))
            is ApiResponse.Error -> emit(UiState.Error(result.message))
            is ApiResponse.NetworkError -> emit(UiState.Error("لا يوجد اتصال بالإنترنت"))
        }
    }
}
