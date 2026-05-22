package org.mohanned.rawdatyci_cdapp.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.*
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.data.remote.dto.*
import org.mohanned.rawdatyci_cdapp.core.network.safeApiCall

interface GamesApiService {
    suspend fun getQuestions(gameType: String, childId: String): ApiResponse<List<GameQuestionDto>>
    suspend fun saveResult(request: GameResultRequest): ApiResponse<Unit>
    suspend fun getChildHistory(childId: String, gameType: String?): ApiResponse<List<GameResultRequest>>
    suspend fun getQuestionBank(): ApiResponse<List<GameQuestionDto>>
    suspend fun updateQuestion(id: String, correctAnswer: String?, isActive: Boolean?): ApiResponse<GameQuestionDto>
}

class GamesApiServiceImpl(private val client: HttpClient) : GamesApiService {
    override suspend fun getQuestions(gameType: String, childId: String): ApiResponse<List<GameQuestionDto>> = safeApiCall {
        client.get("games/questions") { 
            parameter("game_type", gameType)
            parameter("child_id", childId)
        }
    }

    override suspend fun saveResult(request: GameResultRequest): ApiResponse<Unit> = safeApiCall {
        client.post("games/results") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    override suspend fun getChildHistory(childId: String, gameType: String?): ApiResponse<List<GameResultRequest>> = safeApiCall {
        client.get("games/results/$childId") { 
            parameter("game_type", gameType) 
        }
    }

    override suspend fun getQuestionBank(): ApiResponse<List<GameQuestionDto>> = safeApiCall {
        client.get("games/questions/bank")
    }

    override suspend fun updateQuestion(id: String, correctAnswer: String?, isActive: Boolean?): ApiResponse<GameQuestionDto> = safeApiCall {
        client.patch("games/questions/$id") {
            contentType(ContentType.Application.Json)
            setBody(buildMap {
                correctAnswer?.let { put("correct_answer", it) }
                isActive?.let { put("is_active", it) }
            })
        }
    }
}
