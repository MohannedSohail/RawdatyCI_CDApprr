package org.mohanned.rawdatyci_cdapp.domain.usecase.classroom

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.Classroom
import org.mohanned.rawdatyci_cdapp.domain.model.PaginatedResult
import org.mohanned.rawdatyci_cdapp.domain.repository.ClassesRepository

class GetClassesUseCase(private val repository: ClassesRepository) {
    operator fun invoke(search: String? = null, page: Int = 1): Flow<UiState<PaginatedResult<Classroom>>> = flow {
        emit(UiState.Loading)
        when (val result = repository.getClasses(search, page)) {
            is ApiResponse.Success -> emit(UiState.Success(result.data))
            is ApiResponse.Error -> emit(UiState.Error(result.message))
            is ApiResponse.NetworkError -> emit(UiState.Error("لا يوجد اتصال بالإنترنت"))
        }
    }
}
