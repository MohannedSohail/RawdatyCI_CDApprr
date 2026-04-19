package org.mohanned.rawdatyci_cdapp.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.*
import org.mohanned.rawdatyci_cdapp.data.remote.dto.*
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.network.safeApiCall

interface SettingsApiService {
    suspend fun getSettings(): ApiResponse<SettingsDto>
    suspend fun updateSettings(settings: SettingsDto): ApiResponse<SettingsDto>
}

class SettingsApiServiceImpl(private val client: HttpClient) : SettingsApiService {
    override suspend fun getSettings(): ApiResponse<SettingsDto> = safeApiCall {
        client.get("settings")
    }

    override suspend fun updateSettings(settings: SettingsDto): ApiResponse<SettingsDto> = safeApiCall {
        client.put("settings") {
            contentType(ContentType.Application.Json)
            setBody(settings)
        }
    }
}
