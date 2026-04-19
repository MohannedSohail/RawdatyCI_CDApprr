package org.mohanned.rawdatyci_cdapp.domain.usecase.attendance

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceSummary
import org.mohanned.rawdatyci_cdapp.domain.repository.AttendanceRepository

class GetMonthlyAttendanceReportUseCase(private val repository: AttendanceRepository) {
    operator fun invoke(month: String, classId: String? = null): Flow<UiState<AttendanceSummary>> = flow {
        emit(UiState.Loading)
        when (val result = repository.getMonthlyReport(month, classId)) {
            is ApiResponse.Success -> emit(UiState.Success(result.data))
            is ApiResponse.Error -> emit(UiState.Error(result.message))
            is ApiResponse.NetworkError -> emit(UiState.Error("لا يوجد اتصال بالإنترنت"))
        }
    }
}
