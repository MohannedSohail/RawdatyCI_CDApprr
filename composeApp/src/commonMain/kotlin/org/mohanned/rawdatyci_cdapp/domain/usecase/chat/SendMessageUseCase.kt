package org.mohanned.rawdatyci_cdapp.domain.usecase.chat

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.model.Message
import org.mohanned.rawdatyci_cdapp.domain.repository.ChatRepository

class SendMessageUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke(conversationId: String, content: String): Result<Message> {
        return when (val result = repository.sendMessage(conversationId, content)) {
            is ApiResponse.Success -> Result.success(result.data)
            is ApiResponse.Error -> Result.failure(Exception(result.message))
            is ApiResponse.NetworkError -> Result.failure(Exception(result.message))
        }
    }
}
