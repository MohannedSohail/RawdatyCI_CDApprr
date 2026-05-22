package org.mohanned.rawdatyci_cdapp.data.repository

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.data.remote.api.ClassesApiService
import org.mohanned.rawdatyci_cdapp.data.remote.dto.toDomain
import org.mohanned.rawdatyci_cdapp.domain.model.Classroom
import org.mohanned.rawdatyci_cdapp.domain.model.PaginatedResult
import org.mohanned.rawdatyci_cdapp.domain.repository.ClassesRepository

class ClassesRepositoryImpl(
    private val api: ClassesApiService
) : ClassesRepository {
    override suspend fun getClasses(search: String?, page: Int): ApiResponse<PaginatedResult<Classroom>> {
        return try {
            // طلب قائمة الفصول مع تضمين الطلاب بناءً على طلبك
            val response = api.getClasses(includeChildren = true)
            if (response is ApiResponse.Success) {
                var items = response.data.map { it.toDomain() }

                if (!search.isNullOrBlank()) {
                    items = items.filter { it.name.contains(search, ignoreCase = true) }
                }

                ApiResponse.Success(
                    PaginatedResult(
                        items = items,
                        total = items.size,
                        page = 1,
                        lastPage = 1,
                        hasMore = false
                    )
                )
            } else {
                @Suppress("UNCHECKED_CAST")
                response as ApiResponse<PaginatedResult<Classroom>>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun getClass(id: String): ApiResponse<Classroom> {
        return try {
            val response = api.getClass(id)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toDomain())
            } else {
                response as ApiResponse<Classroom>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun createClass(name: String, description: String?, teacherId: String?, capacity: Int?): ApiResponse<Classroom> {
        return try {
            // مطابقة Postman: POST /api/v1/classes
            val response = api.createClass(name, description, teacherId)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toDomain())
            } else {
                response as ApiResponse<Classroom>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun updateClass(id: String, name: String?, teacherId: String?, isActive: Boolean?): ApiResponse<Classroom> {
        return try {
            // مطابقة Postman: PUT /api/v1/classes/{id}
            val response = api.updateClass(id, name, null, teacherId, isActive)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toDomain())
            } else {
                response as ApiResponse<Classroom>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun deleteClass(id: String): ApiResponse<Unit> {
        return try {
            api.deleteClass(id)
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }
}
