package org.mohanned.rawdatyci_cdapp.domain.usecase.classroom

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.model.Classroom
import org.mohanned.rawdatyci_cdapp.domain.repository.ClassesRepository

class CreateClassUseCase(private val repository: ClassesRepository) {
    suspend operator fun invoke(name: String, description: String?, teacherId: String?, capacity: Int?): Result<Classroom> {
        return when (val result = repository.createClass(name, description, teacherId, capacity)) {
            is ApiResponse.Success -> Result.success(result.data)
            is ApiResponse.Error -> Result.failure(Exception(result.message))
            is ApiResponse.NetworkError -> Result.failure(Exception(result.message))
        }
    }
}
