package org.mohanned.rawdatyci_cdapp.data.remote.api
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiConfig
import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.network.remote.safeApiCall
import org.mohanned.rawdatyci_cdapp.data.remote.dto.ApiListDto
import org.mohanned.rawdatyci_cdapp.data.remote.dto.AttendanceRecordDto
import org.mohanned.rawdatyci_cdapp.data.remote.dto.AttendanceSummaryDto

@Serializable
data class AttendanceRecordRequest(
    val child_id: Int,
    val status: String,
    val notes: String? = null,
)

@Serializable
data class CreateAttendanceRequest(
    val class_id: Int,
    val date: String,
    val records: List<AttendanceRecordRequest>,
)

class AttendanceApiService(private val client: HttpClient) {

    // POST /api/v1/attendance
    suspend fun createAttendance(
        classId: Int,
        date: String,
        records: List<AttendanceRecordRequest>,
    ): ApiResponse<AttendanceSummaryDto> =
        safeApiCall {
            client.post("${ApiConfig.BASE_URL}/attendance") {
                contentType(ContentType.Application.Json)
                setBody(CreateAttendanceRequest(classId, date, records))
            }
        }

    // PATCH /api/v1/attendance/:session_id/records/:child_id
    suspend fun updateRecord(
        sessionId: Int,
        childId: Int,
        status: String,
        notes: String? = null,
    ): ApiResponse<AttendanceRecordDto> =
        safeApiCall {
            client.patch(
                "${ApiConfig.BASE_URL}/attendance/$sessionId/records/$childId"
            ) {
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "status" to status,
                    "notes"  to notes,
                ))
            }
        }

    // GET /api/v1/attendance/class/:class_id
    suspend fun getClassAttendance(
        classId: Int,
        date: String? = null,
        page: Int = 1,
    ): ApiResponse<ApiListDto<AttendanceSummaryDto>> =
        safeApiCall {
            client.get("${ApiConfig.BASE_URL}/attendance/class/$classId") {
                date?.let { parameter("date", it) }
                parameter("page", page)
            }
        }

    // GET /api/v1/attendance/child/:child_id
    suspend fun getChildAttendance(
        childId: Int,
        page: Int = 1,
    ): ApiResponse<ApiListDto<AttendanceRecordDto>> =
        safeApiCall {
            client.get("${ApiConfig.BASE_URL}/attendance/child/$childId") {
                parameter("page", page)
            }
        }

    // GET /api/v1/attendance/report/monthly
    suspend fun getMonthlyReport(
        classId: Int,
        month: String,
    ): ApiResponse<AttendanceSummaryDto> =
        safeApiCall {
            client.get("${ApiConfig.BASE_URL}/attendance/report/monthly") {
                parameter("class_id", classId)
                parameter("month", month)
            }
        }
}