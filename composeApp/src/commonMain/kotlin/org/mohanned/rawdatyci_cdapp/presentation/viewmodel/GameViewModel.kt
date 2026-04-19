package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.GameAnswer
import org.mohanned.rawdatyci_cdapp.domain.model.GameQuestion
import org.mohanned.rawdatyci_cdapp.domain.model.GameType
import org.mohanned.rawdatyci_cdapp.domain.usecase.game.GetChildGameHistoryUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.game.GetGameQuestionsUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.game.SaveGameResultUseCase
import kotlin.time.TimeMark
import kotlin.time.TimeSource

data class GameState(
    val questions: List<GameQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val currentQuestion: GameQuestion? = null,
    val selectedOption: String? = null,
    val isAnswered: Boolean = false,
    val isCorrect: Boolean = false,
    val score: Int = 0,
    val answers: List<GameAnswer> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val childId: String = "",
    val totalQuestions: Int = 0,
    val history: List<GameQuestion> = emptyList()
)

sealed class GameEffect {
    data class ShowResult(val score: Int, val total: Int, val stars: Int, val elapsedSeconds: Int) : GameEffect()
}

sealed class GameIntent {
    data class Start(val type: GameType, val childId: String = "") : GameIntent()
    data class SelectOption(val option: String) : GameIntent()
    object CheckAnswer : GameIntent()
    object NextQuestion : GameIntent()
    data class LoadHistory(val childId: String) : GameIntent()
}

class GameViewModel(
    private val getGameQuestionsUseCase: GetGameQuestionsUseCase,
    private val saveGameResultUseCase: SaveGameResultUseCase,
    private val getChildGameHistoryUseCase: GetChildGameHistoryUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(GameState())
    val state = _state.asStateFlow()

    private val _effect = Channel<GameEffect>()
    val effect = _effect.receiveAsFlow()
    
    private var startTime: TimeMark? = null

    fun onIntent(intent: GameIntent) {
        when (intent) {
            is GameIntent.Start -> startGame(intent.type, intent.childId)
            is GameIntent.SelectOption -> _state.update { it.copy(selectedOption = intent.option) }
            GameIntent.CheckAnswer -> checkAnswer()
            GameIntent.NextQuestion -> nextQuestion()
            is GameIntent.LoadHistory -> loadHistory(intent.childId)
        }
    }

    private fun startGame(type: GameType, childId: String) {
        viewModelScope.launch {
            getGameQuestionsUseCase(type.name.lowercase(), childId).collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> _state.update { it.copy(isLoading = true) }
                    is UiState.Success -> {
                        startTime = TimeSource.Monotonic.markNow()
                        _state.update { it.copy(
                            questions = uiState.data,
                            currentIndex = 0,
                            currentQuestion = uiState.data.firstOrNull(),
                            totalQuestions = uiState.data.size,
                            score = 0,
                            answers = emptyList(),
                            childId = childId,
                            isLoading = false
                        ) }
                    }
                    is UiState.Error -> _state.update { it.copy(isLoading = false, error = uiState.message) }
                }
            }
        }
    }

    private fun checkAnswer() {
        _state.update { s ->
            val correct = s.currentQuestion?.correctAnswer == s.selectedOption
            s.copy(
                isAnswered = true,
                isCorrect = correct,
                score = if (correct) s.score + 1 else s.score,
                answers = s.answers + GameAnswer(s.currentQuestion?.id ?: "", s.selectedOption ?: "")
            )
        }
    }

    private fun nextQuestion() {
        viewModelScope.launch {
            val s = _state.value
            val nextIndex = s.currentIndex + 1
            if (nextIndex >= s.questions.size) {
                val elapsed = startTime?.elapsedNow()?.inWholeSeconds?.toInt() ?: 0
                val stars = when {
                    s.score.toFloat() / s.questions.size >= 0.9 -> 3
                    s.score.toFloat() / s.questions.size >= 0.6 -> 2
                    else -> 1
                }
                
                if (s.childId.isNotBlank()) {
                    saveGameResultUseCase(s.childId, s.currentQuestion?.gameType?.name?.lowercase() ?: "", s.score, s.answers)
                }
                
                _effect.send(GameEffect.ShowResult(s.score, s.questions.size, stars, elapsed))
            } else {
                _state.update { it.copy(
                    currentIndex = nextIndex,
                    currentQuestion = it.questions[nextIndex],
                    isAnswered = false,
                    isCorrect = false,
                    selectedOption = null
                ) }
            }
        }
    }

    private fun loadHistory(childId: String) {
        viewModelScope.launch {
            getChildGameHistoryUseCase(childId).collect { uiState ->
                if (uiState is UiState.Success) {
                    _state.update { it.copy(history = uiState.data) }
                }
            }
        }
    }
}
