package org.mohanned.rawdatyci_cdapp.data.remote.api

import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiConfig
import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.network.remote.safeApiCall
import org.mohanned.rawdatyci_cdapp.data.remote.dto.ApiListDto
import org.mohanned.rawdatyci_cdapp.data.remote.dto.ApiMessageDto
import org.mohanned.rawdatyci_cdapp.data.remote.dto.ChildDto
import org.mohanned.rawdatyci_cdapp.data.remote.dto.ClassDto
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ClassesApiService(private val client: HttpClient) {

    // GET /api/v1/classes
    suspend fun getClasses(
        search: String? = null,
        page: Int = 1,
    ): ApiResponse<ApiListDto<ClassDto>> =
        safeApiCall {
            client.get("${ApiConfig.BASE_URL}/classes") {
                search?.let { parameter("search", it) }
                parameter("page", page)
            }
        }

    // GET /api/v1/classes/:id
    suspend fun getClass(
        id: Int,
    ): ApiResponse<ClassDto> =
        safeApiCall {
            client.get("${ApiConfig.BASE_URL}/classes/$id")
        }

    // POST /api/v1/classes
    suspend fun createClass(
        name: String,
        teacherId: Int?,
        capacity: Int?,
    ): ApiResponse<ClassDto> =
        safeApiCall {
            client.post("${ApiConfig.BASE_URL}/classes") {
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "name"       to name,
                    "teacher_id" to teacherId,
                    "capacity"   to capacity,
                ))
            }
        }

    // PUT /api/v1/classes/:id
    suspend fun updateClass(
        id: Int,
        name: String?,
        teacherId: Int?,
    ): ApiResponse<ClassDto> =
        safeApiCall {
            client.put("${ApiConfig.BASE_URL}/classes/$id") {
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "name"       to name,
                    "teacher_id" to teacherId,
                ))
            }
        }

    // DELETE /api/v1/classes/:id
    suspend fun deleteClass(
        id: Int,
    ): ApiResponse<ApiMessageDto> =
        safeApiCall {
            client.delete("${ApiConfig.BASE_URL}/classes/$id")
        }

    // GET /api/v1/classes/:id/children
    suspend fun getChildrenByClass(
        classId: Int,
        page: Int = 1,
    ): ApiResponse<ApiListDto<ChildDto>> =
        safeApiCall {
            client.get("${ApiConfig.BASE_URL}/classes/$classId/children") {
                parameter("page", page)
            }
        }

    // GET /api/v1/my-children
    suspend fun getMyChildren(
        page: Int = 1,
    ): ApiResponse<ApiListDto<ChildDto>> =
        safeApiCall {
            client.get("${ApiConfig.BASE_URL}/my-children") {
                parameter("page", page)
            }
        }
}