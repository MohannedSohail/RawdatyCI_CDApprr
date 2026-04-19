package org.mohanned.rawdatyci_cdapp.data.repository


import org.mohanned.rawdatyci_cdapp.data.remote.api.ChatApiService
import org.mohanned.rawdatyci_cdapp.data.remote.dto.toDomain
import org.mohanned.rawdatyci_cdapp.data.remote.dto.toPaginated
import org.mohanned.rawdatyci_cdapp.domain.model.Conversation
import org.mohanned.rawdatyci_cdapp.domain.model.Message
import org.mohanned.rawdatyci_cdapp.domain.model.PaginatedResult
import org.mohanned.rawdatyci_cdapp.domain.repository.ChatRepository
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse


class ChatRepositoryImpl(
    private val api: ChatApiService
) : ChatRepository {
    override suspend fun getConversations(page: Int): ApiResponse<PaginatedResult<Conversation>> {
        return try {
            val response = api.getConversations()
            if (response is ApiResponse.Success) {
                ApiResponse.Success(PaginatedResult(response.data.map { it.toDomain() }, response.data.size, 1, 1, false))
            } else {
                response as ApiResponse<PaginatedResult<Conversation>>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun getMessages(conversationId: String, page: Int): ApiResponse<PaginatedResult<Message>> {
        return try {
            val response = api.getMessages(conversationId, page)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toPaginated { it.toDomain() })
            } else {
                response as ApiResponse<PaginatedResult<Message>>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun sendMessage(conversationId: String, content: String): ApiResponse<Message> {
        return try {
            val response = api.sendMessage(conversationId, content, null)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toDomain())
            } else {
                response as ApiResponse<Message>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun startConversation(participantId: String, childId: String?): ApiResponse<Conversation> {
        return try {
            val response = api.startConversation(participantId)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toDomain())
            } else {
                response as ApiResponse<Conversation>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }
}
