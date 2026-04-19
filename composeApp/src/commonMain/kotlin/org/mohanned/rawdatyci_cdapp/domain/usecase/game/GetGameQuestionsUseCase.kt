package org.mohanned.rawdatyci_cdapp.domain.usecase.game

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.GameQuestion
import org.mohanned.rawdatyci_cdapp.domain.repository.GamesRepository

class GetGameQuestionsUseCase(private val repository: GamesRepository) {
    operator fun invoke(gameType: String, childId: String? = null): Flow<UiState<List<GameQuestion>>> = flow {
        emit(UiState.Loading)
        when (val result = repository.getQuestions(gameType, childId)) {
            is ApiResponse.Success -> emit(UiState.Success(result.data))
            is ApiResponse.Error -> emit(UiState.Error(result.message))
            is ApiResponse.NetworkError -> emit(UiState.Error("لا يوجد اتصال بالإنترنت"))
        }
    }
}
