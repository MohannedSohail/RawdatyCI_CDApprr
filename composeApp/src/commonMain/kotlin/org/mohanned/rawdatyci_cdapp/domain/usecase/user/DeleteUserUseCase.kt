package org.mohanned.rawdatyci_cdapp.domain.usecase.user

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.repository.UsersRepository

class DeleteUserUseCase(private val repository: UsersRepository) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return when (val result = repository.deleteUser(id)) {
            is ApiResponse.Success -> Result.success(Unit)
            is ApiResponse.Error -> Result.failure(Exception(result.message))
            is ApiResponse.NetworkError -> Result.failure(Exception(result.message))
        }
    }
}
