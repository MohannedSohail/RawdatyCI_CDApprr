package org.mohanned.rawdatyci_cdapp.domain.usecase.classroom

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.repository.ClassesRepository

class DeleteClassUseCase(private val repository: ClassesRepository) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return when (val result = repository.deleteClass(id)) {
            is ApiResponse.Success -> Result.success(Unit)
            is ApiResponse.Error -> Result.failure(Exception(result.message))
            is ApiResponse.NetworkError -> Result.failure(Exception(result.message))
        }
    }
}
