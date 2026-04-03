package org.mohanned.rawdatyci_cdapp.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiConfig
import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.network.remote.safeApiCall
import org.mohanned.rawdatyci_cdapp.data.remote.dto.ApiListDto
import org.mohanned.rawdatyci_cdapp.data.remote.dto.GameQuestionDto

@Serializable
data class SaveGameResultRequest(
    val child_id: Int,
    val game_type: String,
    val score: Int,
    val total_questions: Int,
    val duration_seconds: Int,
)

class GamesApiService(private val client: HttpClient) {

    // GET /api/v1/games/questions
    suspend fun getQuestions(
        gameType: String,
        level: Int = 1,
    ): ApiResponse<ApiListDto<GameQuestionDto>> =
        safeApiCall {
            client.get("${ApiConfig.BASE_URL}/games/questions") {
                parameter("type", gameType)
                parameter("level", level)
            }
        }

    // POST /api/v1/games/results
    suspend fun saveResult(
        childId: Int,
        gameType: String,
        score: Int,
        totalQuestions: Int,
        durationSeconds: Int,
    ): ApiResponse<ApiListDto<GameQuestionDto>> =
        safeApiCall {
            client.post("${ApiConfig.BASE_URL}/games/results") {
                contentType(ContentType.Application.Json)
                setBody(
                    SaveGameResultRequest(
                        child_id = childId,
                        game_type = gameType,
                        score = score,
                        total_questions = totalQuestions,
                        duration_seconds = durationSeconds,
                    )
                )
            }
        }

    // GET /api/v1/games/results/:child_id
    suspend fun getChildResults(
        childId: Int,
    ): ApiResponse<ApiListDto<GameQuestionDto>> =
        safeApiCall {
            client.get("${ApiConfig.BASE_URL}/games/results/$childId")
        }
}