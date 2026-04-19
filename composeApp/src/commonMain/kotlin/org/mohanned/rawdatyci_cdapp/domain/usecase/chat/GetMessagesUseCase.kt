package org.mohanned.rawdatyci_cdapp.domain.usecase.chat

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.Message
import org.mohanned.rawdatyci_cdapp.domain.model.PaginatedResult
import org.mohanned.rawdatyci_cdapp.domain.repository.ChatRepository

class GetMessagesUseCase(private val repository: ChatRepository) {
    operator fun invoke(conversationId: String, page: Int = 1): Flow<UiState<PaginatedResult<Message>>> = flow {
        emit(UiState.Loading)
        when (val result = repository.getMessages(conversationId, page)) {
            is ApiResponse.Success -> emit(UiState.Success(result.data))
            is ApiResponse.Error -> emit(UiState.Error(result.message))
            is ApiResponse.NetworkError -> emit(UiState.Error("لا يوجد اتصال بالإنترنت"))
        }
    }
}
