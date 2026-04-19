package org.mohanned.rawdatyci_cdapp.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.*
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse

import org.mohanned.rawdatyci_cdapp.core.network.safeApiCall
import org.mohanned.rawdatyci_cdapp.data.remote.dto.*

interface ClassesApiService {
    suspend fun getClasses(includeChildren: Boolean, page: Int): ApiResponse<ApiListDto<ClassDto>>
    suspend fun getClass(id: String): ApiResponse<ClassDto>
    suspend fun createClass(name: String, teacherId: String?, capacity: Int?): ApiResponse<ClassDto>
    suspend fun updateClass(id: String, name: String?, teacherId: String?, isActive: Boolean?): ApiResponse<ClassDto>
    suspend fun deleteClass(id: String): ApiResponse<Unit>
}

class ClassesApiServiceImpl(private val client: HttpClient) : ClassesApiService {
    override suspend fun getClasses(includeChildren: Boolean, page: Int): ApiResponse<ApiListDto<ClassDto>> = safeApiCall {
        client.get("classes") {
            parameter("include_children", includeChildren)
            parameter("page", page)
        }
    }

    override suspend fun getClass(id: String): ApiResponse<ClassDto> = safeApiCall {
        client.get("classes/$id")
    }

    override suspend fun createClass(name: String, teacherId: String?, capacity: Int?): ApiResponse<ClassDto> = safeApiCall {
        client.post("classes") {
            contentType(ContentType.Application.Json)
            setBody(CreateClassRequest(name, teacherId, capacity))
        }
    }

    override suspend fun updateClass(id: String, name: String?, teacherId: String?, isActive: Boolean?): ApiResponse<ClassDto> = safeApiCall {
        client.put("classes/$id") {
            contentType(ContentType.Application.Json)
            setBody(UpdateClassRequest(name, teacherId, isActive))
        }
    }

    override suspend fun deleteClass(id: String): ApiResponse<Unit> = safeApiCall {
        client.delete("classes/$id")
    }
}
