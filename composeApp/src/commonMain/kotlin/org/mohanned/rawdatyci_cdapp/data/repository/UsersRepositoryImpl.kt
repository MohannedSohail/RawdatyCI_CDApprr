package org.mohanned.rawdatyci_cdapp.data.repository

import org.mohanned.rawdatyci_cdapp.core.network.TokenManager
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.data.remote.api.UsersApiService
import org.mohanned.rawdatyci_cdapp.data.remote.dto.*
import org.mohanned.rawdatyci_cdapp.domain.model.PaginatedResult
import org.mohanned.rawdatyci_cdapp.domain.model.User
import org.mohanned.rawdatyci_cdapp.domain.repository.UsersRepository

class UsersRepositoryImpl(
    private val api: UsersApiService,
    private val tokenManager: TokenManager
) : UsersRepository {
    
    override suspend fun getUsers(role: String?, classId: String?, search: String?, page: Int): ApiResponse<PaginatedResult<User>> {
        return try {
            val response = api.getUsers(role, null, search, page)
            when (response) {
                is ApiResponse.Success<*> -> {
                    val data = (response as ApiResponse.Success).data
                    ApiResponse.Success(data.toPaginated { it.toDomain() })
                }
                is ApiResponse.Error -> ApiResponse.Error(response.code, response.message, response.errorCode)
                is ApiResponse.NetworkError -> ApiResponse.NetworkError(response.message)
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }
    
    override suspend fun getUser(id: String): ApiResponse<User> {
        return try {
            val response = api.getUser(id)
            when (response) {
                is ApiResponse.Success<*> -> {
                    val data = (response as ApiResponse.Success).data
                    ApiResponse.Success(data.toDomain())
                }
                is ApiResponse.Error -> ApiResponse.Error(response.code, response.message, response.errorCode)
                is ApiResponse.NetworkError -> ApiResponse.NetworkError(response.message)
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }
    
    override suspend fun createUser(
        name: String,
        email: String,
        password: String,
        role: String,
        phone: String?,
        classId: String?,
        children: List<CreateChildRequest>?
    ): ApiResponse<User> {
        return try {
            val request = CreateUserRequest(
                name = name,
                email = email,
                password = password,
                role = role,
                phone = phone,
                classId = classId,
                children = children
            )
            
            val response = if (role.lowercase() == "parent") {
                api.createParent(request)
            } else {
                api.createTeacher(request)
            }
            
            when (response) {
                is ApiResponse.Success<*> -> {
                    val data = (response as ApiResponse.Success).data
                    ApiResponse.Success(data.toDomain())
                }
                is ApiResponse.Error -> ApiResponse.Error(response.code, response.message, response.errorCode)
                is ApiResponse.NetworkError -> ApiResponse.NetworkError(response.message)
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }
    
    override suspend fun updateUser(id: String, name: String?, phone: String?, isActive: Boolean?, classId: String?): ApiResponse<User> {
        return try {
            val response = api.updateUser(id, name, phone, isActive)
            when (response) {
                is ApiResponse.Success<*> -> {
                    val data = (response as ApiResponse.Success).data
                    ApiResponse.Success(data.toDomain())
                }
                is ApiResponse.Error -> ApiResponse.Error(response.code, response.message, response.errorCode)
                is ApiResponse.NetworkError -> ApiResponse.NetworkError(response.message)
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }
    
    override suspend fun deleteUser(id: String): ApiResponse<Unit> {
        return try {
            api.deleteUser(id)
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }
    
    override suspend fun getProfile(): ApiResponse<User> {
        return try {
            val response = api.getProfile()
            when (response) {
                is ApiResponse.Success<*> -> {
                    val userDto = (response as ApiResponse.Success).data
                    val user = userDto.toDomain()
                    tokenManager.saveUserInfo(
                        user.id, 
                        user.name, 
                        user.email, 
                        user.role.name, 
                        user.avatarUrl, 
                        tokenManager.getTenantSlug()
                    )
                    ApiResponse.Success(user)
                }
                is ApiResponse.Error -> ApiResponse.Error(response.code, response.message, response.errorCode)
                is ApiResponse.NetworkError -> ApiResponse.NetworkError(response.message)
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }
    
    override suspend fun updateProfile(name: String, phone: String?, address: String?): ApiResponse<User> {
        return try {
            val response = api.updateProfile(name, phone, address)
            when (response) {
                is ApiResponse.Success<*> -> {
                    val data = (response as ApiResponse.Success).data
                    ApiResponse.Success(data.toDomain())
                }
                is ApiResponse.Error -> ApiResponse.Error(response.code, response.message, response.errorCode)
                is ApiResponse.NetworkError -> ApiResponse.NetworkError(response.message)
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }
    
    override suspend fun changePassword(current: String, newPass: String, confirm: String): ApiResponse<Unit> {
        return try {
            api.changePassword(current, newPass, confirm)
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }
    
    override suspend fun saveFcmToken(token: String, deviceType: String): ApiResponse<Unit> {
        return try {
            api.saveFcmToken(token, deviceType)
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }
}
