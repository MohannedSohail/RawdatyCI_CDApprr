package org.mohanned.rawdatyci_cdapp.domain.usecase.news

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.model.News
import org.mohanned.rawdatyci_cdapp.domain.repository.NewsRepository

class UpdateNewsUseCase(private val repository: NewsRepository) {
    suspend operator fun invoke(
        id: String,
        title: String? = null,
        content: String? = null,
        isVisible: Boolean? = null
    ): Result<News> {
        return when (val result = repository.updateNews(id, title, content, isVisible)) {
            is ApiResponse.Success -> Result.success(result.data)
            is ApiResponse.Error -> Result.failure(Exception(result.message))
            is ApiResponse.NetworkError -> Result.failure(Exception(result.message))
        }
    }
}
