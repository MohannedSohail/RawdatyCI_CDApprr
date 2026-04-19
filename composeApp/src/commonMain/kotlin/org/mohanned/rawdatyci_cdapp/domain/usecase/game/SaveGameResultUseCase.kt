package org.mohanned.rawdatyci_cdapp.domain.usecase.game

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.model.GameAnswer
import org.mohanned.rawdatyci_cdapp.domain.repository.GamesRepository

class SaveGameResultUseCase(private val repository: GamesRepository) {
    suspend operator fun invoke(
        childId: String,
        gameType: String,
        score: Int,
        answers: List<GameAnswer> = emptyList()
    ): Result<Unit> {
        return when (val result = repository.saveResult(childId, gameType, score, answers)) {
            is ApiResponse.Success -> Result.success(Unit)
            is ApiResponse.Error -> Result.failure(Exception(result.message))
            is ApiResponse.NetworkError -> Result.failure(Exception(result.message))
        }
    }
}
