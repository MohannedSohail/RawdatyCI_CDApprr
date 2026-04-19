package org.mohanned.rawdatyci_cdapp.domain.usecase.child

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.domain.repository.ChildrenRepository

class UpdateChildUseCase(private val repository: ChildrenRepository) {
    suspend operator fun invoke(id: String, classId: String? = null, notes: String? = null, rating: Int? = null): Result<Child> {
        return when (val result = repository.updateChild(id, classId, notes, rating)) {
            is ApiResponse.Success -> Result.success(result.data)
            is ApiResponse.Error -> Result.failure(Exception(result.message))
            is ApiResponse.NetworkError -> Result.failure(Exception(result.message))
        }
    }
}
