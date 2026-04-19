package org.mohanned.rawdatyci_cdapp.domain.usecase.auth

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.repository.AuthRepository

class ForgotPasswordUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String): Result<String> {
        return when (val result = repository.forgotPassword(email)) {
            is ApiResponse.Success -> Result.success(result.data.message)
            is ApiResponse.Error -> Result.failure(Exception(result.message))
            is ApiResponse.NetworkError -> Result.failure(Exception(result.message))
        }
    }
}
