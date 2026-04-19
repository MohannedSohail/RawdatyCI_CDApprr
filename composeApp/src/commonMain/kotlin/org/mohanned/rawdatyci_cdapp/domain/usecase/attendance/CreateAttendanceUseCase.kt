package org.mohanned.rawdatyci_cdapp.domain.usecase.attendance

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.data.remote.dto.AttendanceRecordRequest
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceSummary
import org.mohanned.rawdatyci_cdapp.domain.repository.AttendanceRepository

class CreateAttendanceUseCase(private val repository: AttendanceRepository) {
    suspend operator fun invoke(classId: String, date: String, records: List<AttendanceRecordRequest>): Result<AttendanceSummary> {
        return when (val result = repository.createAttendance(classId, date, records)) {
            is ApiResponse.Success -> Result.success(result.data)
            is ApiResponse.Error -> Result.failure(Exception(result.message))
            is ApiResponse.NetworkError -> Result.failure(Exception(result.message))
        }
    }
}
