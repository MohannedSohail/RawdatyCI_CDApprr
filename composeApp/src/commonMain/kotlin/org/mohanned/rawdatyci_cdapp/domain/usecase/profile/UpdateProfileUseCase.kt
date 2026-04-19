package org.mohanned.rawdatyci_cdapp.domain.usecase.profile

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.model.User
import org.mohanned.rawdatyci_cdapp.domain.repository.UsersRepository

class UpdateProfileUseCase(private val repository: UsersRepository) {
    suspend operator fun invoke(name: String, phone: String? = null, address: String? = null): Result<User> {
        return when (val result = repository.updateProfile(name, phone, address)) {
            is ApiResponse.Success -> Result.success(result.data)
            is ApiResponse.Error -> Result.failure(Exception(result.message))
            is ApiResponse.NetworkError -> Result.failure(Exception(result.message))
        }
    }
}
