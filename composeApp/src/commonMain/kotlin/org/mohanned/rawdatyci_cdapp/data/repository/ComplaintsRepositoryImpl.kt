package org.mohanned.rawdatyci_cdapp.data.repository

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.data.remote.api.ComplaintsApiService
import org.mohanned.rawdatyci_cdapp.data.remote.dto.toDomain
import org.mohanned.rawdatyci_cdapp.data.remote.dto.toPaginated
import org.mohanned.rawdatyci_cdapp.domain.model.Complaint
import org.mohanned.rawdatyci_cdapp.domain.model.PaginatedResult
import org.mohanned.rawdatyci_cdapp.domain.repository.ComplaintsRepository

class ComplaintsRepositoryImpl(
    private val api: ComplaintsApiService
) : ComplaintsRepository {
    override suspend fun getComplaints(status: String?, type: String?, page: Int): ApiResponse<PaginatedResult<Complaint>> {
        return try {
            val response = api.getComplaints(type, page)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toPaginated { it.toDomain() })
            } else {
                response as ApiResponse<PaginatedResult<Complaint>>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun getComplaint(id: String): ApiResponse<Complaint> {
        return try {
            val response = api.getComplaint(id)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toDomain())
            } else {
                response as ApiResponse<Complaint>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun createComplaint(content: String, type: String): ApiResponse<Complaint> {
        return try {
            val response = api.submitComplaint("", content, type)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toDomain())
            } else {
                response as ApiResponse<Complaint>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun replyToComplaint(id: String, reply: String): ApiResponse<Complaint> {
        return try {
            val response = api.replyToComplaint(id, reply)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toDomain())
            } else {
                response as ApiResponse<Complaint>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }
}
