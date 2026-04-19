package org.mohanned.rawdatyci_cdapp.data.repository

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.data.remote.api.SettingsApiService
import org.mohanned.rawdatyci_cdapp.data.remote.dto.SettingsDto
import org.mohanned.rawdatyci_cdapp.data.remote.dto.toDomain
import org.mohanned.rawdatyci_cdapp.domain.model.KindergartenSettings
import org.mohanned.rawdatyci_cdapp.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val api: SettingsApiService
) : SettingsRepository {
    override suspend fun getSettings(): ApiResponse<KindergartenSettings> {
        return try {
            val response = api.getSettings()
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toDomain())
            } else {
                response as ApiResponse<KindergartenSettings>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun updateSettings(settings: KindergartenSettings): ApiResponse<KindergartenSettings> {
        return try {
            val dto = SettingsDto(
                kindergartenName = settings.kindergartenName,
                address = settings.address,
                phone = settings.phone,
                whatsapp = settings.whatsapp,
                twitter = settings.twitter,
                instagram = settings.instagram,
                mapLat = settings.mapLat,
                mapLng = settings.mapLng,
                academicYear = settings.academicYear
            )
            val response = api.updateSettings(dto)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toDomain())
            } else {
                response as ApiResponse<KindergartenSettings>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }
}
