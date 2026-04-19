package org.mohanned.rawdatyci_cdapp.domain.usecase.classroom

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.Classroom
import org.mohanned.rawdatyci_cdapp.domain.repository.ClassesRepository

class GetClassByIdUseCase(private val repository: ClassesRepository) {
    operator fun invoke(id: String): Flow<UiState<Classroom>> = flow {
        emit(UiState.Loading)
        when (val result = repository.getClass(id)) {
            is ApiResponse.Success -> emit(UiState.Success(result.data))
            is ApiResponse.Error -> emit(UiState.Error(result.message))
            is ApiResponse.NetworkError -> emit(UiState.Error("لا يوجد اتصال بالإنترنت"))
        }
    }
}
