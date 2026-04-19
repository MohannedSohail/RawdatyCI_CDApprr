package org.mohanned.rawdatyci_cdapp.domain.usecase.chat

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.model.Conversation
import org.mohanned.rawdatyci_cdapp.domain.repository.ChatRepository

class StartConversationUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke(participantId: String, childId: String? = null): Result<Conversation> {
        return when (val result = repository.startConversation(participantId, childId)) {
            is ApiResponse.Success -> Result.success(result.data)
            is ApiResponse.Error -> Result.failure(Exception(result.message))
            is ApiResponse.NetworkError -> Result.failure(Exception(result.message))
        }
    }
}
