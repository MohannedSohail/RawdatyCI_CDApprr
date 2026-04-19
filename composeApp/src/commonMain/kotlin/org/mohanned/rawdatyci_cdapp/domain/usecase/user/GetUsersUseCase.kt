package org.mohanned.rawdatyci_cdapp.domain.usecase.user

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.PaginatedResult
import org.mohanned.rawdatyci_cdapp.domain.model.User
import org.mohanned.rawdatyci_cdapp.domain.repository.UsersRepository

class GetUsersUseCase(
    private val usersRepository: UsersRepository
) {
    suspend operator fun invoke(
        role: String? = null,
        search: String? = null,
        page: Int = 1
    ): Flow<UiState<PaginatedResult<User>>> = flow {
        emit(UiState.Loading)
        when (val result = usersRepository.getUsers(role = role, search = search, page = page)) {
            is ApiResponse.Success -> emit(UiState.Success(result.data))
            is ApiResponse.Error -> emit(UiState.Error(result.message))
            is ApiResponse.NetworkError -> emit(UiState.Error("لا يوجد اتصال بالإنترنت"))
        }
    }.flowOn(Dispatchers.IO)
}
