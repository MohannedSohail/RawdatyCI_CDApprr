package org.mohanned.rawdatyci_cdapp.domain.usecase.attendance

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceRecord
import org.mohanned.rawdatyci_cdapp.domain.repository.AttendanceRepository

class UpdateAttendanceRecordUseCase(private val repository: AttendanceRepository) {
    suspend operator fun invoke(sessionId: String, childId: String, status: String, notes: String? = null): Result<AttendanceRecord> {
        return when (val result = repository.updateRecord(sessionId, childId, status, notes)) {
            is ApiResponse.Success -> Result.success(result.data)
            is ApiResponse.Error -> Result.failure(Exception(result.message))
            is ApiResponse.NetworkError -> Result.failure(Exception(result.message))
        }
    }
}
