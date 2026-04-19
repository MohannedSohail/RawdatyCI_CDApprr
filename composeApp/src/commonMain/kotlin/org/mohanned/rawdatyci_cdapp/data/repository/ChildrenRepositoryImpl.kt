package org.mohanned.rawdatyci_cdapp.data.repository


import org.mohanned.rawdatyci_cdapp.data.remote.api.ChildrenApiService
import org.mohanned.rawdatyci_cdapp.data.remote.dto.toDomain
import org.mohanned.rawdatyci_cdapp.data.remote.dto.toPaginated
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.domain.model.PaginatedResult
import org.mohanned.rawdatyci_cdapp.domain.repository.ChildrenRepository
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse


class ChildrenRepositoryImpl(
    private val api: ChildrenApiService
) : ChildrenRepository {
    override suspend fun getChildrenByClass(classId: String, page: Int): ApiResponse<PaginatedResult<Child>> {
        return try {
            val response = api.getChildren(classId, page)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toPaginated { it.toDomain() })
            } else {
                response as ApiResponse<PaginatedResult<Child>>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun getMyChildren(page: Int): ApiResponse<PaginatedResult<Child>> {
        return try {
            val response = api.getMyChildren(page)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toPaginated { it.toDomain() })
            } else {
                response as ApiResponse<PaginatedResult<Child>>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun createChild(name: String, parentId: String, classId: String?, birthDate: String?, gender: String): ApiResponse<Child> {
        return try {
            val response = api.createChild(name, classId ?: "", birthDate, gender, parentId)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toDomain())
            } else {
                response as ApiResponse<Child>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun updateChild(id: String, classId: String?, notes: String?, rating: Int?): ApiResponse<Child> {
        return try {
            // Note: API updateChild signature is (id, name, classId, birthDate, gender)
            // But Domain repository updateChild signature is (id, classId, notes, rating)
            // We'll map what we can or adapt the call.
            val response = api.updateChild(id, null, classId, null, null)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toDomain())
            } else {
                response as ApiResponse<Child>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }
}
