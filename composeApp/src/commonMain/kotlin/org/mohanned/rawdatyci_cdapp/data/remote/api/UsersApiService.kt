package org.mohanned.rawdatyci_cdapp.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.*
import org.mohanned.rawdatyci_cdapp.data.remote.dto.*
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.network.safeApiCall

interface UsersApiService {
    suspend fun getUsers(role: String?, status: String?, search: String?, page: Int): ApiResponse<ApiListDto<UserDto>>
    suspend fun getUser(id: String): ApiResponse<UserDto>
    suspend fun createUser(name: String, email: String, password: String, role: String, phone: String?, classId: String?): ApiResponse<UserDto>
    suspend fun updateUser(id: String, name: String?, phone: String?, isActive: Boolean?): ApiResponse<UserDto>
    suspend fun deleteUser(id: String): ApiResponse<Unit>
    suspend fun getProfile(): ApiResponse<UserDto>
    suspend fun updateProfile(name: String, phone: String?, address: String?): ApiResponse<UserDto>
    suspend fun changePassword(current: String, newPass: String, confirm: String): ApiResponse<Unit>
    suspend fun saveFcmToken(token: String, deviceType: String): ApiResponse<Unit>
}

class UsersApiServiceImpl(private val client: HttpClient) : UsersApiService {
    override suspend fun getUsers(role: String?, status: String?, search: String?, page: Int): ApiResponse<ApiListDto<UserDto>> = safeApiCall {
        client.get("users") {
            parameter("page", page)
            role?.let { parameter("role", it) }
            status?.let { parameter("status", it) }
            search?.let { parameter("search", it) }
        }
    }

    override suspend fun getUser(id: String): ApiResponse<UserDto> = safeApiCall {
        client.get("users/$id")
    }

    override suspend fun createUser(name: String, email: String, password: String, role: String, phone: String?, classId: String?): ApiResponse<UserDto> = safeApiCall {
        client.post("users") {
            contentType(ContentType.Application.Json)
            setBody(CreateUserRequest(name, email, password, role, phone, classId))
        }
    }

    override suspend fun updateUser(id: String, name: String?, phone: String?, isActive: Boolean?): ApiResponse<UserDto> = safeApiCall {
        client.put("users/$id") {
            contentType(ContentType.Application.Json)
            setBody(UpdateUserRequest(name, phone, isActive))
        }
    }

    override suspend fun deleteUser(id: String): ApiResponse<Unit> = safeApiCall {
        client.delete("users/$id")
    }

    override suspend fun getProfile(): ApiResponse<UserDto> = safeApiCall {
        client.get("users/profile/me")
    }

    override suspend fun updateProfile(name: String, phone: String?, address: String?): ApiResponse<UserDto> = safeApiCall {
        client.put("users/profile/me") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("name" to name, "phone" to phone, "address" to address))
        }
    }

    override suspend fun changePassword(current: String, newPass: String, confirm: String): ApiResponse<Unit> = safeApiCall {
        client.put("users/profile/password") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("current_password" to current, "password" to newPass, "password_confirmation" to confirm))
        }
    }

    override suspend fun saveFcmToken(token: String, deviceType: String): ApiResponse<Unit> = safeApiCall {
        client.post("users/profile/fcm-token") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("fcm_token" to token, "device_type" to deviceType))
        }
    }
}
