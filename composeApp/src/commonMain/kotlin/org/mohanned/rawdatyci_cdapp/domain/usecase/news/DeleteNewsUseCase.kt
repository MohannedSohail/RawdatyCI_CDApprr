package org.mohanned.rawdatyci_cdapp.domain.usecase.news

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.repository.NewsRepository

class DeleteNewsUseCase(private val repository: NewsRepository) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return when (val result = repository.deleteNews(id)) {
            is ApiResponse.Success -> Result.success(Unit)
            is ApiResponse.Error -> Result.failure(Exception(result.message))
            is ApiResponse.NetworkError -> Result.failure(Exception(result.message))
        }
    }
}
