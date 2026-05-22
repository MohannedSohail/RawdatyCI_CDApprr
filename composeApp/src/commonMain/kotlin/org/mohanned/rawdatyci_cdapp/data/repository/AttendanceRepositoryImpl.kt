package org.mohanned.rawdatyci_cdapp.data.repository

import org.mohanned.rawdatyci_cdapp.data.remote.api.AttendanceApiService
import org.mohanned.rawdatyci_cdapp.data.remote.dto.*
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceRecord
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceSummary
import org.mohanned.rawdatyci_cdapp.domain.model.PaginatedResult
import org.mohanned.rawdatyci_cdapp.domain.repository.AttendanceRepository
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceStatus

class AttendanceRepositoryImpl(
    private val api: AttendanceApiService
) : AttendanceRepository {
    override suspend fun createAttendance(
        classId: String,
        date: String,
        records: List<AttendanceRecordRequest>
    ): ApiResponse<AttendanceSummary> {
        return try {
            api.recordAttendance(CreateAttendanceRequest(classId, date, records))
            ApiResponse.Success(AttendanceSummary(date, classId, records.size, 0, 0, 0, 0f))
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun updateRecord(
        sessionId: String,
        childId: String,
        status: String,
        notes: String?
    ): ApiResponse<AttendanceRecord> {
        return try {
            api.updateAttendanceRecord(sessionId, childId, status)
            ApiResponse.Success(
                AttendanceRecord(
                    "",
                    childId,
                    "",
                    null,
                    AttendanceStatus.PRESENT,
                    notes
                )
            )
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun getClassAttendance(
        classId: String,
        fromDate: String?,
        toDate: String?,
        page: Int
    ): ApiResponse<PaginatedResult<AttendanceSummary>> {
        return when (val response = api.getClassAttendance(classId, fromDate, toDate)) {
            is ApiResponse.Success -> {
                ApiResponse.Success(
                    PaginatedResult(
                        listOf(response.data.toDomain()),
                        1, 1, 1, false
                    )
                )
            }
            is ApiResponse.Error -> ApiResponse.Error(response.code, response.message, response.errorCode)
            is ApiResponse.NetworkError -> ApiResponse.NetworkError(response.message)
        }
    }

    override suspend fun getChildAttendance(
        childId: String,
        fromDate: String?,
        page: Int
    ): ApiResponse<PaginatedResult<AttendanceRecord>> {
        return when (val response = api.getChildAttendance(childId, fromDate)) {
            is ApiResponse.Success -> {
                ApiResponse.Success(
                    PaginatedResult(
                        response.data.map { it.toDomain() },
                        response.data.size, 1, 1, false
                    )
                )
            }
            is ApiResponse.Error -> ApiResponse.Error(response.code, response.message, response.errorCode)
            is ApiResponse.NetworkError -> ApiResponse.NetworkError(response.message)
        }
    }

    override suspend fun getMonthlyReport(
        month: String,
        classId: String?
    ): ApiResponse<AttendanceSummary> {
        return when (val response = api.getMonthlyReport(classId, month)) {
            is ApiResponse.Success -> {
                ApiResponse.Success(response.data.data.first().toDomain())
            }
            is ApiResponse.Error -> ApiResponse.Error(response.code, response.message, response.errorCode)
            is ApiResponse.NetworkError -> ApiResponse.NetworkError(response.message)
        }
    }
}
