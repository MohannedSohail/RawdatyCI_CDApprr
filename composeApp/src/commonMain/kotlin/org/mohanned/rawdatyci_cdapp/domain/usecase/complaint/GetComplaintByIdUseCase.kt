package org.mohanned.rawdatyci_cdapp.domain.usecase.complaint

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.Complaint
import org.mohanned.rawdatyci_cdapp.domain.repository.ComplaintsRepository

class GetComplaintByIdUseCase(private val repository: ComplaintsRepository) {
    operator fun invoke(id: String): Flow<UiState<Complaint>> = flow {
        emit(UiState.Loading)
        when (val result = repository.getComplaint(id)) {
            is ApiResponse.Success -> emit(UiState.Success(result.data))
            is ApiResponse.Error -> emit(UiState.Error(result.message))
            is ApiResponse.NetworkError -> emit(UiState.Error("لا يوجد اتصال بالإنترنت"))
        }
    }
}
