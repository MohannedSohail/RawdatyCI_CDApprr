package org.mohanned.rawdatyci_cdapp.data.repository

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.data.remote.api.GamesApiService
import org.mohanned.rawdatyci_cdapp.data.remote.dto.GameAnswerDto
import org.mohanned.rawdatyci_cdapp.data.remote.dto.GameResultRequest
import org.mohanned.rawdatyci_cdapp.data.remote.dto.toDomain
import org.mohanned.rawdatyci_cdapp.domain.model.GameAnswer
import org.mohanned.rawdatyci_cdapp.domain.model.GameQuestion
import org.mohanned.rawdatyci_cdapp.domain.repository.GamesRepository

class GamesRepositoryImpl(
    private val api: GamesApiService
) : GamesRepository {
    override suspend fun getQuestions(gameType: String, childId: String?): ApiResponse<List<GameQuestion>> {
        return try {
            val response = api.getQuestions(gameType)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.map { it.toDomain() })
            } else {
                response as ApiResponse<List<GameQuestion>>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun saveResult(childId: String, gameType: String, score: Int, answers: List<GameAnswer>): ApiResponse<Unit> {
        return try {
            val request = GameResultRequest(
                childId = childId,
                gameType = gameType,
                score = score,
                answers = answers.map { GameAnswerDto(it.questionId, it.selectedAnswer) }
            )
            api.saveResult(request)
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun getChildResults(childId: String, gameType: String?): ApiResponse<List<GameQuestion>> {
        return try {
            val response = api.getChildHistory(childId, gameType)
            ApiResponse.Success(emptyList()) 
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun updateQuestion(id: String, text: String?, options: List<String>?, correctAnswer: String?): ApiResponse<GameQuestion> {
        return try {
            val response = api.updateQuestion(id, text, options, correctAnswer)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toDomain())
            } else {
                response as ApiResponse<GameQuestion>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }
}
