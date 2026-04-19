package org.mohanned.rawdatyci_cdapp.domain.usecase.classroom

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.model.Classroom
import org.mohanned.rawdatyci_cdapp.domain.repository.ClassesRepository

class UpdateClassUseCase(private val repository: ClassesRepository) {
    suspend operator fun invoke(id: String, name: String?, teacherId: String?, isActive: Boolean?): Result<Classroom> {
        return when (val result = repository.updateClass(id, name, teacherId, isActive)) {
            is ApiResponse.Success -> Result.success(result.data)
            is ApiResponse.Error -> Result.failure(Exception(result.message))
            is ApiResponse.NetworkError -> Result.failure(Exception(result.message))
        }
    }
}
