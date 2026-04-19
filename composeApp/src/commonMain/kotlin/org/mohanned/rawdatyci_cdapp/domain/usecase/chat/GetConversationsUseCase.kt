package org.mohanned.rawdatyci_cdapp.domain.usecase.chat

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.Conversation
import org.mohanned.rawdatyci_cdapp.domain.model.PaginatedResult
import org.mohanned.rawdatyci_cdapp.domain.repository.ChatRepository

class GetConversationsUseCase(private val repository: ChatRepository) {
    operator fun invoke(page: Int = 1): Flow<UiState<PaginatedResult<Conversation>>> = flow {
        emit(UiState.Loading)
        when (val result = repository.getConversations(page)) {
            is ApiResponse.Success -> emit(UiState.Success(result.data))
            is ApiResponse.Error -> emit(UiState.Error(result.message))
            is ApiResponse.NetworkError -> emit(UiState.Error("لا يوجد اتصال بالإنترنت"))
        }
    }
}
