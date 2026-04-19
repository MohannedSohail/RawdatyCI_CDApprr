package org.mohanned.rawdatyci_cdapp.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.*
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.network.safeApiCall
import org.mohanned.rawdatyci_cdapp.data.remote.dto.*

interface NewsApiService {
    suspend fun getNews(type: String?, page: Int): ApiResponse<ApiListDto<NewsDto>>
    suspend fun getNewsById(id: String): ApiResponse<NewsDto>
    suspend fun createNews(title: String, body: String, type: String, isVisible: Boolean): ApiResponse<NewsDto>
    suspend fun updateNews(id: String, title: String?, body: String?, type: String?, isVisible: Boolean?): ApiResponse<NewsDto>
    suspend fun deleteNews(id: String): ApiResponse<Unit>
}

class NewsApiServiceImpl(private val client: HttpClient) : NewsApiService {
    override suspend fun getNews(type: String?, page: Int): ApiResponse<ApiListDto<NewsDto>> = safeApiCall {
        client.get("news") {
            parameter("type", type)
            parameter("page", page)
        }
    }

    override suspend fun getNewsById(id: String): ApiResponse<NewsDto> = safeApiCall {
        client.get("news/$id")
    }

    override suspend fun createNews(title: String, body: String, type: String, isVisible: Boolean): ApiResponse<NewsDto> = safeApiCall {
        client.post("news") {
            contentType(ContentType.Application.Json)
            setBody(CreateNewsRequest(title, body, type, isVisible))
        }
    }

    override suspend fun updateNews(id: String, title: String?, body: String?, type: String?, isVisible: Boolean?): ApiResponse<NewsDto> = safeApiCall {
        client.put("news/$id") {
            contentType(ContentType.Application.Json)
            setBody(UpdateNewsRequest(title, body, type, isVisible))
        }
    }

    override suspend fun deleteNews(id: String): ApiResponse<Unit> = safeApiCall {
        client.delete("news/$id")
    }
}
