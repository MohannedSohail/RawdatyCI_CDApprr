package org.mohanned.rawdatyci_cdapp.domain.usecase.profile

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.repository.UsersRepository

class ChangePasswordUseCase(private val repository: UsersRepository) {
    suspend operator fun invoke(current: String, newPass: String, confirm: String): Result<Unit> {
        return when (val result = repository.changePassword(current, newPass, confirm)) {
            is ApiResponse.Success -> Result.success(Unit)
            is ApiResponse.Error -> Result.failure(Exception(result.message))
            is ApiResponse.NetworkError -> Result.failure(Exception(result.message))
        }
    }
}
