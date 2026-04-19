package org.mohanned.rawdatyci_cdapp.domain.usecase.user

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.model.User
import org.mohanned.rawdatyci_cdapp.domain.repository.UsersRepository

class CreateUserUseCase(private val repository: UsersRepository) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        role: String,
        phone: String? = null,
        classId: String? = null
    ): Result<User> {
        return when (val result = repository.createUser(name, email, password, role, phone, classId)) {
            is ApiResponse.Success -> Result.success(result.data)
            is ApiResponse.Error -> Result.failure(Exception(result.message))
            is ApiResponse.NetworkError -> Result.failure(Exception(result.message))
        }
    }
}
