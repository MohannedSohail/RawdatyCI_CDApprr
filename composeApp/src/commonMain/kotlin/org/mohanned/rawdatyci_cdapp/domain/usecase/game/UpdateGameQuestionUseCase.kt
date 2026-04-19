package org.mohanned.rawdatyci_cdapp.domain.usecase.game

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.model.GameQuestion
import org.mohanned.rawdatyci_cdapp.domain.repository.GamesRepository

class UpdateGameQuestionUseCase(private val repository: GamesRepository) {
    suspend operator fun invoke(id: String, text: String? = null, options: List<String>? = null, correctAnswer: String? = null): Result<GameQuestion> {
        return when (val result = repository.updateQuestion(id, text, options, correctAnswer)) {
            is ApiResponse.Success -> Result.success(result.data)
            is ApiResponse.Error -> Result.failure(Exception(result.message))
            is ApiResponse.NetworkError -> Result.failure(Exception(result.message))
        }
    }
}
