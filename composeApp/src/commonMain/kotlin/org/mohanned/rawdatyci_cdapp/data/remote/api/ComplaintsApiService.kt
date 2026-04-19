package org.mohanned.rawdatyci_cdapp.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.network.safeApiCall
import org.mohanned.rawdatyci_cdapp.data.remote.dto.ApiListDto
import org.mohanned.rawdatyci_cdapp.data.remote.dto.ComplaintDto

interface ComplaintsApiService {
    suspend fun getComplaints(type: String?, page: Int): ApiResponse<ApiListDto<ComplaintDto>>
    suspend fun getComplaint(id: String): ApiResponse<ComplaintDto>
    suspend fun submitComplaint(
        title: String,
        content: String,
        type: String
    ): ApiResponse<ComplaintDto>

    suspend fun replyToComplaint(id: String, reply: String): ApiResponse<ComplaintDto>
}

class ComplaintsApiServiceImpl(private val client: HttpClient) : ComplaintsApiService {
    override suspend fun getComplaints(
        type: String?,
        page: Int
    ): ApiResponse<ApiListDto<ComplaintDto>> = safeApiCall {
        client.get("complaints") {
            parameter("type", type)
            parameter("page", page)
        }
    }

    override suspend fun getComplaint(id: String): ApiResponse<ComplaintDto> = safeApiCall {
        client.get("complaints/$id")
    }

    override suspend fun submitComplaint(
        title: String,
        content: String,
        type: String
    ): ApiResponse<ComplaintDto> = safeApiCall {
        client.post("complaints") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("title" to title, "content" to content, "type" to type))
        }
    }

    override suspend fun replyToComplaint(id: String, reply: String): ApiResponse<ComplaintDto> =
        safeApiCall {
            client.post("complaints/$id/reply") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("reply" to reply))
            }
        }
}
