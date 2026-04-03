package org.mohanned.rawdatyci_cdapp.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiConfig
import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.network.remote.safeApiCall
import org.mohanned.rawdatyci_cdapp.data.remote.dto.ApiListDto
import org.mohanned.rawdatyci_cdapp.data.remote.dto.ApiMessageDto
import org.mohanned.rawdatyci_cdapp.data.remote.dto.NewsDto

class NewsApiService(private val client: HttpClient) {

    // GET /api/v1/news
    suspend fun getNews(
        search: String? = null,
        page: Int = 1,
    ): ApiResponse<ApiListDto<NewsDto>> =
        safeApiCall {
            client.get("${ApiConfig.BASE_URL}/news") {
                parameter("search", search)
                parameter("page", page)
            }
        }

    // GET /api/v1/news/:id
    suspend fun getNewsItem(
        id: Int,
    ): ApiResponse<NewsDto> =
        safeApiCall {
            client.get("${ApiConfig.BASE_URL}/news/$id")
        }

    // POST /api/v1/news
    suspend fun createNews(
        title: String,
        body: String,
        isPinned: Boolean = false,
    ): ApiResponse<NewsDto> =
        safeApiCall {
            client.post("${ApiConfig.BASE_URL}/news") {
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "title"     to title,
                    "body"      to body,
                    "is_pinned" to isPinned,
                ))
            }
        }

    // PUT /api/v1/news/:id
    suspend fun updateNews(
        id: Int,
        title: String?,
        body: String?,
    ): ApiResponse<NewsDto> =
        safeApiCall {
            client.put("${ApiConfig.BASE_URL}/news/$id") {
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "title" to title,
                    "body"  to body,
                ))
            }
        }

    // DELETE /api/v1/news/:id
    suspend fun deleteNews(
        id: Int,
    ): ApiResponse<ApiMessageDto> =
        safeApiCall {
            client.delete("${ApiConfig.BASE_URL}/news/$id")
        }
}
