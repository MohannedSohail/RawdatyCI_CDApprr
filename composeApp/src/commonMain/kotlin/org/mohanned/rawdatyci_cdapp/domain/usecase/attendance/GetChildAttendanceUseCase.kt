package org.mohanned.rawdatyci_cdapp.domain.usecase.attendance

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceRecord
import org.mohanned.rawdatyci_cdapp.domain.model.PaginatedResult
import org.mohanned.rawdatyci_cdapp.domain.repository.AttendanceRepository

class GetChildAttendanceUseCase(private val repository: AttendanceRepository) {
    operator fun invoke(childId: String, fromDate: String? = null, page: Int = 1): Flow<UiState<PaginatedResult<AttendanceRecord>>> = flow {
        emit(UiState.Loading)
        when (val result = repository.getChildAttendance(childId, fromDate, page)) {
            is ApiResponse.Success -> emit(UiState.Success(result.data))
            is ApiResponse.Error -> emit(UiState.Error(result.message))
            is ApiResponse.NetworkError -> emit(UiState.Error("لا يوجد اتصال بالإنترنت"))
        }
    }
}
