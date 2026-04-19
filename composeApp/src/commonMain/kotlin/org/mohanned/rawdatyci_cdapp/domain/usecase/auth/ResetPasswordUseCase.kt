package org.mohanned.rawdatyci_cdapp.domain.usecase.auth

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.repository.AuthRepository

class ResetPasswordUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(resetToken: String, newPassword: String, confirm: String): Result<Unit> {
        return when (val result = repository.resetPassword(resetToken, newPassword, confirm)) {
            is ApiResponse.Success -> Result.success(Unit)
            is ApiResponse.Error -> Result.failure(Exception(result.message))
            is ApiResponse.NetworkError -> Result.failure(Exception(result.message))
        }
    }
}
