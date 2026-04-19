package org.mohanned.rawdatyci_cdapp.data.repository

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.data.remote.api.NewsApiService
import org.mohanned.rawdatyci_cdapp.data.remote.dto.toDomain
import org.mohanned.rawdatyci_cdapp.data.remote.dto.toPaginated
import org.mohanned.rawdatyci_cdapp.domain.model.News
import org.mohanned.rawdatyci_cdapp.domain.model.PaginatedResult
import org.mohanned.rawdatyci_cdapp.domain.repository.NewsRepository

class NewsRepositoryImpl(
    private val api: NewsApiService
) : NewsRepository {
    override suspend fun getNews(search: String?, type: String?, page: Int): ApiResponse<PaginatedResult<News>> {
        return try {
            val response = api.getNews(type, page)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toPaginated { it.toDomain() })
            } else {
                response as ApiResponse<PaginatedResult<News>>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun getSlider(): ApiResponse<List<News>> {
        return try {
            val response = api.getNews("slider", 1)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.data.map { it.toDomain() })
            } else {
                response as ApiResponse<List<News>>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun getNewsItem(id: String): ApiResponse<News> {
        return try {
            val response = api.getNewsById(id)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toDomain())
            } else {
                response as ApiResponse<News>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun createNews(title: String, content: String, type: String, isVisible: Boolean, imageUrl: String?, notifyUsers: Boolean): ApiResponse<News> {
        return try {
            val response = api.createNews(title, content, type, isVisible)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toDomain())
            } else {
                response as ApiResponse<News>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun updateNews(id: String, title: String?, content: String?, isVisible: Boolean?): ApiResponse<News> {
        return try {
            val response = api.updateNews(id, title, content, null, isVisible)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toDomain())
            } else {
                response as ApiResponse<News>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun deleteNews(id: String): ApiResponse<Unit> {
        return try {
            api.deleteNews(id)
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }
}
