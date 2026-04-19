package org.mohanned.rawdatyci_cdapp.data.repository

import org.mohanned.rawdatyci_cdapp.core.network.TokenManager
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.data.remote.api.UsersApiService
import org.mohanned.rawdatyci_cdapp.data.remote.dto.toDomain
import org.mohanned.rawdatyci_cdapp.data.remote.dto.toPaginated
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
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toPaginated { it.toDomain() })
            } else {
                response as ApiResponse<PaginatedResult<User>>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }
    
    override suspend fun getUser(id: String): ApiResponse<User> {
        return try {
            val response = api.getUser(id)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toDomain())
            } else {
                response as ApiResponse<User>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }
    
    override suspend fun createUser(name: String, email: String, password: String, role: String, phone: String?, classId: String?): ApiResponse<User> {
        return try {
            val response = api.createUser(name, email, password, role, phone, classId)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toDomain())
            } else {
                response as ApiResponse<User>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }
    
    override suspend fun updateUser(id: String, name: String?, phone: String?, isActive: Boolean?, classId: String?): ApiResponse<User> {
        return try {
            val response = api.updateUser(id, name, phone, isActive)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toDomain())
            } else {
                response as ApiResponse<User>
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
            if (response is ApiResponse.Success) {
                val user = response.data.toDomain()
                tokenManager.saveUserInfo(
                    user.id, 
                    user.name, 
                    user.email, 
                    user.role.name, 
                    user.avatarUrl, 
                    tokenManager.getTenantSlug()
                )
                ApiResponse.Success(user)
            } else {
                response as ApiResponse<User>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }
    
    override suspend fun updateProfile(name: String, phone: String?, address: String?): ApiResponse<User> {
        return try {
            val response = api.updateProfile(name, phone, address)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toDomain())
            } else {
                response as ApiResponse<User>
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
