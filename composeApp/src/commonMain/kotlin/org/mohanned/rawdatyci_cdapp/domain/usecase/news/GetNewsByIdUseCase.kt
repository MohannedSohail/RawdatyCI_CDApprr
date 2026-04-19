package org.mohanned.rawdatyci_cdapp.domain.usecase.news

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.News
import org.mohanned.rawdatyci_cdapp.domain.repository.NewsRepository

class GetNewsByIdUseCase(private val repository: NewsRepository) {
    operator fun invoke(id: String): Flow<UiState<News>> = flow {
        emit(UiState.Loading)
        when (val result = repository.getNewsItem(id)) {
            is ApiResponse.Success -> emit(UiState.Success(result.data))
            is ApiResponse.Error -> emit(UiState.Error(result.message))
            is ApiResponse.NetworkError -> emit(UiState.Error("لا يوجد اتصال بالإنترنت"))
        }
    }
}
