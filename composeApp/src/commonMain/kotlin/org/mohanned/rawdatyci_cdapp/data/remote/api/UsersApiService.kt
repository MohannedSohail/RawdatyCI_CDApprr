package org.mohanned.rawdatyci_cdapp.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiConfig
import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.network.remote.safeApiCall
import org.mohanned.rawdatyci_cdapp.data.remote.dto.ApiListDto
import org.mohanned.rawdatyci_cdapp.data.remote.dto.ApiMessageDto
import org.mohanned.rawdatyci_cdapp.data.remote.dto.UserDto


class UsersApiService(private val client: HttpClient) {

    // GET /api/v1/users
    suspend fun getUsers(
        role: String? = null,
        classId: Int? = null,
        search: String? = null,
        page: Int = 1,
    ): ApiResponse<ApiListDto<UserDto>> =
        safeApiCall {
            client.get("${ApiConfig.BASE_URL}/users") {
                role?.let    { parameter("role",     it) }
                classId?.let { parameter("class_id", it) }
                search?.let  { parameter("search",   it) }
                parameter("page", page)
            }
        }

    // GET /api/v1/users/:id
    suspend fun getUser(
        id: Int,
    ): ApiResponse<UserDto> =
        safeApiCall {
            client.get("${ApiConfig.BASE_URL}/users/$id")
        }

    // POST /api/v1/users
    suspend fun createUser(
        name: String,
        email: String,
        password: String,
        role: String,
        phone: String? = null,
        classId: Int? = null,
    ): ApiResponse<UserDto> =
        safeApiCall {
            client.post("${ApiConfig.BASE_URL}/users") {
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "name"      to name,
                    "email"     to email,
                    "password"  to password,
                    "role"      to role,
                    "phone"     to phone,
                    "class_id"  to classId,
                ))
            }
        }

    // PUT /api/v1/users/:id
    suspend fun updateUser(
        id: Int,
        name: String? = null,
        phone: String? = null,
        isActive: Boolean? = null,
        classId: Int? = null,
    ): ApiResponse<UserDto> =
        safeApiCall {
            client.put("${ApiConfig.BASE_URL}/users/$id") {
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "name"      to name,
                    "phone"     to phone,
                    "is_active" to isActive,
                    "class_id"  to classId,
                ))
            }
        }

    // DELETE /api/v1/users/:id
    suspend fun deleteUser(
        id: Int,
    ): ApiResponse<ApiMessageDto> =
        safeApiCall {
            client.delete("${ApiConfig.BASE_URL}/users/$id")
        }

    // GET /api/v1/profile
    suspend fun getProfile(): ApiResponse<UserDto> =
        safeApiCall {
            client.get("${ApiConfig.BASE_URL}/profile")
        }

    // PUT /api/v1/profile
    suspend fun updateProfile(
        name: String,
        phone: String?,
    ): ApiResponse<UserDto> =
        safeApiCall {
            client.put("${ApiConfig.BASE_URL}/profile") {
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "name"  to name,
                    "phone" to phone,
                ))
            }
        }

    // PUT /api/v1/profile/password
    suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String,
    ): ApiResponse<ApiMessageDto> =
        safeApiCall {
            client.put("${ApiConfig.BASE_URL}/profile/password") {
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "current_password"      to currentPassword,
                    "password"              to newPassword,
                    "password_confirmation" to confirmPassword,
                ))
            }
        }
}