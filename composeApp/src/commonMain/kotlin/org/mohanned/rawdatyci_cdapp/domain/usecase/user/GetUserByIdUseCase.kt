package org.mohanned.rawdatyci_cdapp.domain.usecase.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.User
import org.mohanned.rawdatyci_cdapp.domain.repository.UsersRepository

class GetUserByIdUseCase(private val repository: UsersRepository) {
    operator fun invoke(id: String): Flow<UiState<User>> = flow {
        emit(UiState.Loading)
        when (val result = repository.getUser(id)) {
            is ApiResponse.Success -> emit(UiState.Success(result.data))
            is ApiResponse.Error -> emit(UiState.Error(result.message))
            is ApiResponse.NetworkError -> emit(UiState.Error("لا يوجد اتصال بالإنترنت"))
        }
    }
}
