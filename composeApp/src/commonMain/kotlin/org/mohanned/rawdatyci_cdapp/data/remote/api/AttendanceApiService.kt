package org.mohanned.rawdatyci_cdapp.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.*
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.network.safeApiCall
import org.mohanned.rawdatyci_cdapp.data.remote.dto.*

interface AttendanceApiService {
    suspend fun recordAttendance(request: CreateAttendanceRequest): ApiResponse<Unit>
    suspend fun updateAttendanceRecord(sessionId: String, childId: String, status: String): ApiResponse<Unit>
    suspend fun getClassAttendance(classId: String, date: String?): ApiResponse<AttendanceSummaryDto>
    suspend fun getChildAttendance(childId: String): ApiResponse<List<AttendanceRecordDto>>
    suspend fun getMonthlyReport(classId: String?, month: Int?, year: Int?): ApiResponse<ApiListDto<AttendanceSummaryDto>>
}

class AttendanceApiServiceImpl(private val client: HttpClient) : AttendanceApiService {
    override suspend fun recordAttendance(request: CreateAttendanceRequest): ApiResponse<Unit> = safeApiCall {
        client.post("attendance") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    override suspend fun updateAttendanceRecord(sessionId: String, childId: String, status: String): ApiResponse<Unit> = safeApiCall {
        client.patch("attendance/$sessionId/records/$childId") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("status" to status))
        }
    }

    override suspend fun getClassAttendance(classId: String, date: String?): ApiResponse<AttendanceSummaryDto> = safeApiCall {
        client.get("attendance/class/$classId") {
            parameter("date", date)
        }
    }

    override suspend fun getChildAttendance(childId: String): ApiResponse<List<AttendanceRecordDto>> = safeApiCall {
        client.get("attendance/child/$childId")
    }

    override suspend fun getMonthlyReport(classId: String?, month: Int?, year: Int?): ApiResponse<ApiListDto<AttendanceSummaryDto>> = safeApiCall {
        client.get("attendance/report/monthly") {
            parameter("class_id", classId)
            parameter("month", month)
            parameter("year", year)
        }
    }
}
