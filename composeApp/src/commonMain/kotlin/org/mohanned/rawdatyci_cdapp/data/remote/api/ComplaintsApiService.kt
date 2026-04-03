package org.mohanned.rawdatyci_cdapp.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiConfig
import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.network.remote.safeApiCall
import org.mohanned.rawdatyci_cdapp.data.remote.dto.ApiListDto
import org.mohanned.rawdatyci_cdapp.data.remote.dto.ComplaintDto

class ComplaintsApiService(private val client: HttpClient) {

    // GET /api/v1/complaints
    suspend fun getComplaints(
        status: String? = null,
        page: Int = 1,
    ): ApiResponse<ApiListDto<ComplaintDto>> =
        safeApiCall {
            client.get("${ApiConfig.BASE_URL}/complaints") {
                status?.let { parameter("status", it) }
                parameter("page", page)
            }
        }

    // GET /api/v1/complaints/:id
    suspend fun getComplaint(
        id: Int,
    ): ApiResponse<ComplaintDto> =
        safeApiCall {
            client.get("${ApiConfig.BASE_URL}/complaints/$id")
        }

    // POST /api/v1/complaints
    suspend fun createComplaint(
        title: String,
        body: String,
    ): ApiResponse<ComplaintDto> =
        safeApiCall {
            client.post("${ApiConfig.BASE_URL}/complaints") {
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "title" to title,
                    "body"  to body,
                ))
            }
        }

    // POST /api/v1/complaints/:id/reply
    suspend fun replyToComplaint(
        id: Int,
        reply: String,
        status: String,
    ): ApiResponse<ComplaintDto> =
        safeApiCall {
            client.post("${ApiConfig.BASE_URL}/complaints/$id/reply") {
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "reply"  to reply,
                    "status" to status,
                ))
            }
        }
}