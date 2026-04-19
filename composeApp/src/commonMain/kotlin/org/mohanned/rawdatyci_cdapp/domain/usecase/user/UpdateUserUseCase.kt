package org.mohanned.rawdatyci_cdapp.domain.usecase.user

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.model.User
import org.mohanned.rawdatyci_cdapp.domain.repository.UsersRepository

class UpdateUserUseCase(private val repository: UsersRepository) {
    suspend operator fun invoke(
        id: String,
        name: String? = null,
        phone: String? = null,
        isActive: Boolean? = null
    ): Result<User> {
        return when (val result = repository.updateUser(id, name, phone, isActive)) {
            is ApiResponse.Success -> Result.success(result.data)
            is ApiResponse.Error -> Result.failure(Exception(result.message))
            is ApiResponse.NetworkError -> Result.failure(Exception(result.message))
        }
    }
}
