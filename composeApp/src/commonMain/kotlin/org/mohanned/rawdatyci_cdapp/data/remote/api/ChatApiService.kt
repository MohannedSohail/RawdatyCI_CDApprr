package org.mohanned.rawdatyci_cdapp.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.*
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.network.safeApiCall
import org.mohanned.rawdatyci_cdapp.data.remote.dto.*

interface ChatApiService {
    suspend fun getConversations(): ApiResponse<List<ConversationDto>>
    suspend fun startConversation(participantId: String): ApiResponse<ConversationDto>
    suspend fun getMessages(conversationId: String, page: Int): ApiResponse<ApiListDto<MessageDto>>
    suspend fun sendMessage(conversationId: String, content: String, imageUrl: String?): ApiResponse<MessageDto>
}

class ChatApiServiceImpl(private val client: HttpClient) : ChatApiService {
    override suspend fun getConversations(): ApiResponse<List<ConversationDto>> = safeApiCall {
        client.get("chat/conversations")
    }

    override suspend fun startConversation(participantId: String): ApiResponse<ConversationDto> = safeApiCall {
        client.post("chat/conversations") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("participant_id" to participantId))
        }
    }

    override suspend fun getMessages(conversationId: String, page: Int): ApiResponse<ApiListDto<MessageDto>> = safeApiCall {
        client.get("chat/conversations/$conversationId/messages") {
            parameter("page", page)
        }
    }

    override suspend fun sendMessage(conversationId: String, content: String, imageUrl: String?): ApiResponse<MessageDto> = safeApiCall {
        client.post("chat/conversations/$conversationId/messages") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("content" to content, "image_url" to imageUrl))
        }
    }
}
