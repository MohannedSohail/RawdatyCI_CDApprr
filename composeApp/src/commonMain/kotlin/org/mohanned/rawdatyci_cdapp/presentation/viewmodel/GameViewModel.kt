package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
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
    val level: Int = 1
)

sealed class GameEffect {
    data class ShowResult(val score: Int, val total: Int, val stars: Int, val elapsedSeconds: Int) : GameEffect()
}

sealed class GameIntent {
    data class Start(val type: GameType, val level: Int, val childId: String = "") : GameIntent()
    data class SelectOption(val option: String) : GameIntent()
    object CheckAnswer : GameIntent()
    object NextQuestion : GameIntent()
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
            is GameIntent.Start -> startGame(intent.type, intent.level, intent.childId)
            is GameIntent.SelectOption -> _state.update { it.copy(selectedOption = intent.option) }
            GameIntent.CheckAnswer -> checkAnswer()
            GameIntent.NextQuestion -> nextQuestion()
        }
    }

    private fun startGame(type: GameType, level: Int, childId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, questions = emptyList(), level = level) }
            delay(600) // ✅ Shimmer سريع جداً كما طلبت
            
            getGameQuestionsUseCase(type.name.lowercase(), childId).collect { uiState ->
                val dummyItems = getDummyQuestions(type, level)
                _state.update { it.copy(
                    questions = dummyItems,
                    currentIndex = 0,
                    currentQuestion = dummyItems.firstOrNull(),
                    totalQuestions = dummyItems.size,
                    score = 0,
                    answers = emptyList(),
                    childId = childId,
                    isLoading = false
                ) }
                startTime = TimeSource.Monotonic.markNow()
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
                    s.score.toFloat() / s.questions.size >= 0.8 -> 3
                    s.score.toFloat() / s.questions.size >= 0.5 -> 2
                    else -> 1
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

    private fun getDummyQuestions(type: GameType, level: Int): List<GameQuestion> {
        return when (type) {
            GameType.NUMBERS -> when(level) {
                1 -> listOf(
                    GameQuestion("n1", GameType.NUMBERS, 1, "كم عدد أصابع اليد الواحدة؟", null, "5", listOf("4", "5", "6", "1"), null),
                    GameQuestion("n2", GameType.NUMBERS, 1, "أي رقم هو (اثنين)؟", null, "2", listOf("1", "2", "3", "0"), null),
                    GameQuestion("n3", GameType.NUMBERS, 1, "ما هو الرقم الذي يأتي قبل 2؟", null, "1", listOf("0", "1", "3", "4"), null)
                )
                2 -> listOf(
                    GameQuestion("n4", GameType.NUMBERS, 2, "2 + 1 كم يساوي؟", null, "3", listOf("2", "3", "4", "5"), null),
                    GameQuestion("n5", GameType.NUMBERS, 2, "ما هو الرقم (عشرة)؟", null, "10", listOf("1", "10", "100", "0"), null),
                    GameQuestion("n6", GameType.NUMBERS, 2, "كم عدد أرجل القطة؟", null, "4", listOf("2", "4", "6", "8"), null)
                )
                else -> listOf(GameQuestion("n7", GameType.NUMBERS, 3, "ما هو ناتج 5 + 5؟", null, "10", listOf("5", "10", "15", "20"), null))
            }
            GameType.LETTERS -> when(level) {
                1 -> listOf(
                    GameQuestion("l1", GameType.LETTERS, 1, "أرنب يبدأ بحرف:", null, "أ", listOf("أ", "ب", "ت", "ث"), null),
                    GameQuestion("l2", GameType.LETTERS, 1, "بطة تبدأ بحرف:", null, "ب", listOf("أ", "ب", "م", "ك"), null)
                )
                else -> listOf(GameQuestion("l3", GameType.LETTERS, 2, "ما هو الحرف الذي فوقه نقطتان؟", null, "ت", listOf("ب", "ت", "ث", "ن"), null))
            }
            GameType.COLORS -> listOf(
                GameQuestion("c1", GameType.COLORS, 1, "ما هو لون الموز؟", null, "أصفر", listOf("أحمر", "أصفر", "أزرق", "أخضر"), null),
                GameQuestion("c2", GameType.COLORS, 1, "ما هو لون البحر؟", null, "أزرق", listOf("أصفر", "أزرق", "أبيض", "بنفسجي"), null)
            )
            GameType.ANIMALS -> listOf(
                GameQuestion("a1", GameType.ANIMALS, 1, "من يصيح (مياو)؟", null, "القطة", listOf("الكلب", "القطة", "الأسد", "الفيل"), null),
                GameQuestion("a2", GameType.ANIMALS, 1, "من هو ملك الغابة؟", null, "الأسد", listOf("النمر", "الأسد", "الفهد", "الذئب"), null)
            )
            GameType.FRUITS -> listOf(
                GameQuestion("f1", GameType.FRUITS, 1, "فاكهة لونها أحمر ومنها عصير لذيذ:", null, "الفراولة", listOf("الموز", "الفراولة", "الكيوي", "العنب"), null),
                GameQuestion("f2", GameType.FRUITS, 1, "ما هي الفاكهة التي يحبها القرد؟", null, "الموز", listOf("التفاح", "الموز", "البرتقال", "البطيخ"), null)
            )
        }
    }
}
