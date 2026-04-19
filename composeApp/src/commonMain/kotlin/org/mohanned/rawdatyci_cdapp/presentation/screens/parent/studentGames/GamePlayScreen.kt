package org.mohanned.rawdatyci_cdapp.presentation.screens.parent.studentGames

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.ktor.util.reflect.*
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.domain.model.GameQuestion
import org.mohanned.rawdatyci_cdapp.domain.model.GameType
import org.mohanned.rawdatyci_cdapp.presentation.components.GlassHeader
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyButton
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyCard
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.GameEffect
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.GameIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.GameViewModel

data class GamePlayScreen(val gameType: GameType, val childId: String = "") : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: GameViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.onIntent(GameIntent.Start(gameType, childId))
            viewModel.effect.collect { effect ->
                if (effect is GameEffect.ShowResult) {
                    navigator.replace(GameResultScreen(gameType, effect.score, effect.total, effect.stars, effect.elapsedSeconds, childId))
                }
            }
        }

        Scaffold(
            containerColor = AppBg,
            topBar = {
                GlassHeader(
                    title = when(gameType) {
                        GameType.LETTERS -> "لعبة الحروف"
                        GameType.NUMBERS -> "لعبة الأرقام"
                        GameType.COLORS -> "لعبة الألوان"
                    },
                    onBack = { navigator.pop() },
                    gradient = RawdatyGradients.HeroBlue,
                    headerHeight = 100.dp
                )
            }
        ) { padding ->
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BluePrimary)
                }
            } else if (state.error != null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    org.mohanned.rawdatyci_cdapp.presentation.components.EmptyState(
                        title = "خطأ في التحميل",
                        subtitle = state.error!!,
                        icon = Icons.Default.Close,
                        actionText = "إعادة المحاولة",
                        onAction = { viewModel.onIntent(GameIntent.Start(gameType, childId)) }
                    )
                }
            } else if (state.currentQuestion != null) {
                GamePlayContent(
                    modifier = Modifier.padding(padding),
                    question = state.currentQuestion!!,
                    currentIndex = state.currentIndex,
                    totalQuestions = state.totalQuestions,
                    selectedOption = state.selectedOption,
                    isAnswered = state.isAnswered,
                    isCorrect = state.isCorrect,
                    onOptionSelected = { viewModel.onIntent(GameIntent.SelectOption(it)) },
                    onCheckAnswer = { viewModel.onIntent(GameIntent.CheckAnswer) },
                    onNextQuestion = { viewModel.onIntent(GameIntent.NextQuestion) }
                )
            }
        }
    }
}

@Composable
fun GamePlayContent(
    modifier: Modifier = Modifier,
    question: GameQuestion,
    currentIndex: Int,
    totalQuestions: Int,
    selectedOption: String?,
    isAnswered: Boolean,
    isCorrect: Boolean,
    onOptionSelected: (String) -> Unit,
    onCheckAnswer: () -> Unit,
    onNextQuestion: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Progress
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("السؤال ${currentIndex + 1} من $totalQuestions", fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold, color = Gray500)
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / totalQuestions },
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
                color = BluePrimary,
                trackColor = Gray200
            )
        }

        // Question Card
        RawdatyCard(containerColor = White, elevation = 4.dp, shape = RoundedCornerShape(32.dp)) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(question.questionText, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontFamily = CairoFontFamily, color = BlueDark)
                
                // If question has image logic could go here
                Box(Modifier.size(140.dp).background(BlueLight.copy(0.3f), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Star, null, tint = AmberPrimary, modifier = Modifier.size(80.dp))
                }
            }
        }

        // Options
        Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            question.options.forEach { option ->
                val isSelected = selectedOption == option
                val color = if (isAnswered) {
                    if (option == question.correctAnswer) ColorSuccess
                    else if (isSelected) ColorError
                    else Gray100
                } else {
                    if (isSelected) BluePrimary else White
                }

                Surface(
                    modifier = Modifier.fillMaxWidth().height(64.dp).clip(RoundedCornerShape(20.dp))
                        .clickable(enabled = !isAnswered) { onOptionSelected(option) },
                    color = if (isAnswered) {
                        if (option == question.correctAnswer) ColorSuccess.copy(0.1f)
                        else if (isSelected) ColorError.copy(0.1f)
                        else White
                    } else if (isSelected) BluePrimary.copy(0.1f) else White,
                    border = BorderStroke(2.dp, color),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(option, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 18.sp, fontFamily = CairoFontFamily, color = if (isSelected || (isAnswered && option == question.correctAnswer)) color else Gray700)
                        if (isAnswered) {
                            if (option == question.correctAnswer) Icon(Icons.Default.Check, null, tint = ColorSuccess)
                            else if (isSelected) Icon(Icons.Default.Close, null, tint = ColorError)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // Action Button
        RawdatyButton(
            text = if (isAnswered) (if (currentIndex + 1 == totalQuestions) "عرض النتيجة" else "السؤال التالي") else "تأكيد الإجابة",
            onClick = { if (isAnswered) onNextQuestion() else onCheckAnswer() },
            enabled = selectedOption != null,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            backgroundColor = if (isAnswered) MintPrimary else BluePrimary
        )
    }
}
