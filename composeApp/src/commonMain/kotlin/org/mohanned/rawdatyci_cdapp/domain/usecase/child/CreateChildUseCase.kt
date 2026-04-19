package org.mohanned.rawdatyci_cdapp.domain.usecase.child

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.domain.repository.ChildrenRepository

class CreateChildUseCase(private val repository: ChildrenRepository) {
    suspend operator fun invoke(
        name: String,
        parentId: String,
        classId: String? = null,
        birthDate: String? = null,
        gender: String
    ): Result<Child> {
        return when (val result = repository.createChild(name, parentId, classId, birthDate, gender)) {
            is ApiResponse.Success -> Result.success(result.data)
            is ApiResponse.Error -> Result.failure(Exception(result.message))
            is ApiResponse.NetworkError -> Result.failure(Exception(result.message))
        }
    }
}
