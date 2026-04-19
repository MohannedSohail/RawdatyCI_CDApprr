package org.mohanned.rawdatyci_cdapp.domain.usecase.attendance

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceSummary
import org.mohanned.rawdatyci_cdapp.domain.model.PaginatedResult
import org.mohanned.rawdatyci_cdapp.domain.repository.AttendanceRepository

class GetClassAttendanceUseCase(private val repository: AttendanceRepository) {
    operator fun invoke(classId: String, fromDate: String? = null, toDate: String? = null, page: Int = 1): Flow<UiState<PaginatedResult<AttendanceSummary>>> = flow {
        emit(UiState.Loading)
        when (val result = repository.getClassAttendance(classId, fromDate, toDate, page)) {
            is ApiResponse.Success -> emit(UiState.Success(result.data))
            is ApiResponse.Error -> emit(UiState.Error(result.message))
            is ApiResponse.NetworkError -> emit(UiState.Error("لا يوجد اتصال بالإنترنت"))
        }
    }
}
