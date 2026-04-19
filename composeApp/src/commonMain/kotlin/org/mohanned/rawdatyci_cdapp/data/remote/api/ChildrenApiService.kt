package org.mohanned.rawdatyci_cdapp.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.*
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.network.safeApiCall
import org.mohanned.rawdatyci_cdapp.data.remote.dto.*

interface ChildrenApiService {
    suspend fun getMyChildren(page: Int): ApiResponse<ApiListDto<ChildDto>>
    suspend fun getChildren(classId: String?, page: Int): ApiResponse<ApiListDto<ChildDto>>
    suspend fun getChild(id: String): ApiResponse<ChildDto>
    suspend fun createChild(name: String, classId: String, birthDate: String?, gender: String, parentId: String?): ApiResponse<ChildDto>
    suspend fun updateChild(id: String, name: String?, classId: String?, birthDate: String?, gender: String?): ApiResponse<ChildDto>
    suspend fun deleteChild(id: String): ApiResponse<Unit>
}

class ChildrenApiServiceImpl(private val client: HttpClient) : ChildrenApiService {
    override suspend fun getMyChildren(page: Int): ApiResponse<ApiListDto<ChildDto>> = safeApiCall {
        client.get("children/my") { parameter("page", page) }
    }

    override suspend fun getChildren(classId: String?, page: Int): ApiResponse<ApiListDto<ChildDto>> = safeApiCall {
        client.get("children") {
            parameter("class_id", classId)
            parameter("page", page)
        }
    }

    override suspend fun getChild(id: String): ApiResponse<ChildDto> = safeApiCall {
        client.get("children/$id")
    }

    override suspend fun createChild(name: String, classId: String, birthDate: String?, gender: String, parentId: String?): ApiResponse<ChildDto> = safeApiCall {
        client.post("children") {
            contentType(ContentType.Application.Json)
            setBody(mapOf(
                "name" to name,
                "class_id" to classId,
                "birth_date" to birthDate,
                "gender" to gender,
                "parent_id" to parentId
            ))
        }
    }

    override suspend fun updateChild(id: String, name: String?, classId: String?, birthDate: String?, gender: String?): ApiResponse<ChildDto> = safeApiCall {
        client.put("children/$id") {
            contentType(ContentType.Application.Json)
            setBody(buildMap {
                name?.let { put("name", it) }
                classId?.let { put("class_id", it) }
                birthDate?.let { put("birth_date", it) }
                gender?.let { put("gender", it) }
            })
        }
    }

    override suspend fun deleteChild(id: String): ApiResponse<Unit> = safeApiCall {
        client.delete("children/$id")
    }
}
