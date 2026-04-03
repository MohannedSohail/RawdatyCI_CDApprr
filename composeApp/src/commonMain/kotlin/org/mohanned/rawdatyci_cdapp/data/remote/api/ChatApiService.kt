package org.mohanned.rawdatyci_cdapp.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiConfig
import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.network.remote.safeApiCall
import org.mohanned.rawdatyci_cdapp.data.remote.dto.ApiListDto
import org.mohanned.rawdatyci_cdapp.data.remote.dto.ConversationDto
import org.mohanned.rawdatyci_cdapp.data.remote.dto.MessageDto

class ChatApiService(private val client: HttpClient) {

    // GET /api/v1/chat/conversations
    suspend fun getConversations(
        page: Int = 1,
    ): ApiResponse<ApiListDto<ConversationDto>> =
        safeApiCall {
            client.get("${ApiConfig.BASE_URL}/chat/conversations") {
                parameter("page", page)
            }
        }

    // GET /api/v1/chat/conversations/:id/messages
    suspend fun getMessages(
        conversationId: Int,
        page: Int = 1,
    ): ApiResponse<ApiListDto<MessageDto>> =
        safeApiCall {
            client.get(
                "${ApiConfig.BASE_URL}/chat/conversations/$conversationId/messages"
            ) {
                parameter("page", page)
            }
        }

    // POST /api/v1/chat/conversations/:id/messages
    suspend fun sendMessage(
        conversationId: Int,
        content: String,
    ): ApiResponse<MessageDto> =
        safeApiCall {
            client.post(
                "${ApiConfig.BASE_URL}/chat/conversations/$conversationId/messages"
            ) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("content" to content))
            }
        }

    // POST /api/v1/chat/conversations
    suspend fun startConversation(
        participantId: Int,
        childId: Int? = null,
    ): ApiResponse<ConversationDto> =
        safeApiCall {
            client.post("${ApiConfig.BASE_URL}/chat/conversations") {
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "participant_id" to participantId,
                    "child_id"       to childId,
                ))
            }
        }
}
