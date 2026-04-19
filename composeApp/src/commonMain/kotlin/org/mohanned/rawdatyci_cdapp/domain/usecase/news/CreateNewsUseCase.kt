package org.mohanned.rawdatyci_cdapp.domain.usecase.news

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.model.News
import org.mohanned.rawdatyci_cdapp.domain.repository.NewsRepository

class CreateNewsUseCase(private val repository: NewsRepository) {
    suspend operator fun invoke(
        title: String,
        content: String,
        type: String = "news",
        isVisible: Boolean = true,
        imageUrl: String? = null,
        notifyUsers: Boolean = true
    ): Result<News> {
        return when (val result = repository.createNews(title, content, type, isVisible, imageUrl, notifyUsers)) {
            is ApiResponse.Success -> Result.success(result.data)
            is ApiResponse.Error -> Result.failure(Exception(result.message))
            is ApiResponse.NetworkError -> Result.failure(Exception(result.message))
        }
    }
}
