package org.mohanned.rawdatyci_cdapp.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.*
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.network.safeApiCall
import org.mohanned.rawdatyci_cdapp.data.remote.dto.*

interface ClassesApiService {
    // GET /api/v1/classes
    suspend fun getClasses(includeChildren: Boolean = false): ApiResponse<List<ClassDto>>
    
    // GET /api/v1/classes/{{CLASS_ID}}
    suspend fun getClass(id: String): ApiResponse<ClassDto>
    
    // POST /api/v1/classes
    suspend fun createClass(name: String, description: String?, teacherId: String?): ApiResponse<ClassDto>
    
    // PUT /api/v1/classes/{{CLASS_ID}}
    suspend fun updateClass(id: String, name: String?, description: String?, teacherId: String?, isActive: Boolean?): ApiResponse<ClassDto>
    
    // DELETE /api/v1/classes/{{CLASS_ID}}
    suspend fun deleteClass(id: String): ApiResponse<Unit>
}

class ClassesApiServiceImpl(private val client: HttpClient) : ClassesApiService {
    
    override suspend fun getClasses(includeChildren: Boolean): ApiResponse<List<ClassDto>> = safeApiCall {
        val response = client.get("classes") {
            if (includeChildren) parameter("include_children", true)
        }
        // سنفترض أن BaseResponse سيعالج التحويل لـ List<ClassDto>
        response
    }

    override suspend fun getClass(id: String): ApiResponse<ClassDto> = safeApiCall {
        client.get("classes/$id")
    }

    override suspend fun createClass(name: String, description: String?, teacherId: String?): ApiResponse<ClassDto> = safeApiCall {
        client.post("classes") {
            contentType(ContentType.Application.Json)
            setBody(mapOf(
                "name" to name,
                "description" to description,
                "teacher_id" to teacherId
            ))
        }
    }

    override suspend fun updateClass(id: String, name: String?, description: String?, teacherId: String?, isActive: Boolean?): ApiResponse<ClassDto> = safeApiCall {
        client.put("classes/$id") {
            contentType(ContentType.Application.Json)
            setBody(buildMap {
                name?.let { put("name", it) }
                description?.let { put("description", it) }
                teacherId?.let { put("teacher_id", it) }
                isActive?.let { put("is_active", it) }
            })
        }
    }

    override suspend fun deleteClass(id: String): ApiResponse<Unit> = safeApiCall {
        client.delete("classes/$id")
    }
}
