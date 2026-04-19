package org.mohanned.rawdatyci_cdapp.data.repository

import org.mohanned.rawdatyci_cdapp.data.remote.api.AttendanceApiService
import org.mohanned.rawdatyci_cdapp.data.remote.dto.*
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceRecord
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceSummary
import org.mohanned.rawdatyci_cdapp.domain.model.PaginatedResult
import org.mohanned.rawdatyci_cdapp.domain.repository.AttendanceRepository
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse

class AttendanceRepositoryImpl(
    private val api: AttendanceApiService
) : AttendanceRepository {
    override suspend fun createAttendance(
        classId: String,
        date: String,
        records: List<AttendanceRecordRequest>
    ): ApiResponse<AttendanceSummary> {
        return try {
            val response = api.recordAttendance(CreateAttendanceRequest(classId, date, records))
            // The API returns Unit, but the Domain Repository expects AttendanceSummary.
            // In a real scenario, the API might return the summary or we'd fetch it.
            // Assuming we need to return something to satisfy the interface.
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
            // Mocking the return record as the API returns Unit
            ApiResponse.Success(AttendanceRecord("", childId, "", null, org.mohanned.rawdatyci_cdapp.domain.model.AttendanceStatus.PRESENT, notes))
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
        return try {
            val response = api.getClassAttendance(classId, fromDate)
            if (response is ApiResponse.Success) {
                // Converting single summary to paginated result for interface compatibility
                ApiResponse.Success(PaginatedResult(listOf(response.data.toDomain()), 1, 1, 1, false))
            } else {
                response as ApiResponse<PaginatedResult<AttendanceSummary>>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun getChildAttendance(
        childId: String,
        fromDate: String?,
        page: Int
    ): ApiResponse<PaginatedResult<AttendanceRecord>> {
        return try {
            val response = api.getChildAttendance(childId)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(PaginatedResult(response.data.map { it.toDomain() }, response.data.size, 1, 1, false))
            } else {
                response as ApiResponse<PaginatedResult<AttendanceRecord>>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun getMonthlyReport(month: String, classId: String?): ApiResponse<AttendanceSummary> {
        return try {
            // Mapping month string to int if possible, otherwise null
            val response = api.getMonthlyReport(classId, month.toIntOrNull(), null)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.data.first().toDomain())
            } else {
                response as ApiResponse<AttendanceSummary>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }
}
