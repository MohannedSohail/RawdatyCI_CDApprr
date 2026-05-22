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
    suspend fun getClassAttendance(classId: String, fromDate: String?, toDate: String?): ApiResponse<AttendanceSummaryDto>
    suspend fun getChildAttendance(childId: String, fromDate: String?): ApiResponse<List<AttendanceRecordDto>>
    suspend fun getMonthlyReport(classId: String?, month: String?): ApiResponse<ApiListDto<AttendanceSummaryDto>>
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

    override suspend fun getClassAttendance(classId: String, fromDate: String?, toDate: String?): ApiResponse<AttendanceSummaryDto> = safeApiCall {
        client.get("attendance/class/$classId") {
            parameter("from_date", fromDate)
            parameter("to_date", toDate)
        }
    }

    override suspend fun getChildAttendance(childId: String, fromDate: String?): ApiResponse<List<AttendanceRecordDto>> = safeApiCall {
        client.get("attendance/child/$childId") {
            parameter("from_date", fromDate)
        }
    }

    override suspend fun getMonthlyReport(classId: String?, month: String?): ApiResponse<ApiListDto<AttendanceSummaryDto>> = safeApiCall {
        client.get("attendance/report/monthly") {
            parameter("class_id", classId)
            parameter("month", month)
        }
    }
}
