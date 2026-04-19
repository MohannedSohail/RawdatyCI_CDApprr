package org.mohanned.rawdatyci_cdapp.domain.usecase.child

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.domain.model.PaginatedResult
import org.mohanned.rawdatyci_cdapp.domain.repository.ChildrenRepository

class GetMyChildrenUseCase(private val repository: ChildrenRepository) {
    operator fun invoke(page: Int = 1): Flow<UiState<PaginatedResult<Child>>> = flow {
        emit(UiState.Loading)
        when (val result = repository.getMyChildren(page)) {
            is ApiResponse.Success -> emit(UiState.Success(result.data))
            is ApiResponse.Error -> emit(UiState.Error(result.message))
            is ApiResponse.NetworkError -> emit(UiState.Error("لا يوجد اتصال بالإنترنت"))
        }
    }
}
