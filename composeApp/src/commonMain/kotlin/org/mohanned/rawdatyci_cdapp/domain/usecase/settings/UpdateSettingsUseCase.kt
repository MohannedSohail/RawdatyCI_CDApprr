package org.mohanned.rawdatyci_cdapp.domain.usecase.settings

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.model.KindergartenSettings
import org.mohanned.rawdatyci_cdapp.domain.repository.SettingsRepository

class UpdateSettingsUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(settings: KindergartenSettings): Result<KindergartenSettings> {
        return when (val result = repository.updateSettings(settings)) {
            is ApiResponse.Success -> Result.success(result.data)
            is ApiResponse.Error -> Result.failure(Exception(result.message))
            is ApiResponse.NetworkError -> Result.failure(Exception(result.message))
        }
    }
}
