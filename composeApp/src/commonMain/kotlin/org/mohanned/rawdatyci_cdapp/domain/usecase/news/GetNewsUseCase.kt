package org.mohanned.rawdatyci_cdapp.domain.usecase.news

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.News
import org.mohanned.rawdatyci_cdapp.domain.model.PaginatedResult
import org.mohanned.rawdatyci_cdapp.domain.repository.NewsRepository

class GetNewsUseCase(private val repository: NewsRepository) {
    operator fun invoke(
        search: String? = null,
        type: String? = null,
        page: Int = 1
    ): Flow<UiState<PaginatedResult<News>>> = flow {
        emit(UiState.Loading)
        when (val result = repository.getNews(search, type, page)) {
            is ApiResponse.Success -> emit(UiState.Success(result.data))
            is ApiResponse.Error -> emit(UiState.Error(result.message))
            is ApiResponse.NetworkError -> emit(UiState.Error("لا يوجد اتصال بالإنترنت"))
        }
    }
}
