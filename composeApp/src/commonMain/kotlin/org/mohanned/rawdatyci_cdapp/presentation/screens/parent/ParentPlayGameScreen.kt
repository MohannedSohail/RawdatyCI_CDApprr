package org.mohanned.rawdatyci_cdapp.presentation.screens.parent

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.domain.model.GameType
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.GameEffect
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.GameIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.GameViewModel

data class ParentPlayGameScreen(val childId: String, val gameType: GameType) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: GameViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        var showResultDialog by remember { mutableStateOf(false) }
        var resultData by remember { mutableStateOf<GameEffect.ShowResult?>(null) }

        LaunchedEffect(childId, gameType) {
            viewModel.onIntent(GameIntent.Start(gameType, childId as Int))
            viewModel.effect.collect { effect ->
                if (effect is GameEffect.ShowResult) {
                    resultData = effect
                    showResultDialog = true
                }
            }
        }

        Scaffold(
            containerColor = AppBg,
            topBar = {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(Icons.Default.Close, null, tint = Gray500)
                    }
                    if (state.totalQuestions > 0) {
                        LinearProgressIndicator(
                            progress = { (state.currentIndex + 1).toFloat() / state.totalQuestions },
                            modifier = Modifier.weight(1f).height(10.dp).padding(horizontal = 20.dp)
                                .clip(CircleShape),
                            color = BluePrimary,
                            trackColor = Gray200
                        )
                        Text(
                            "${state.currentIndex + 1}/${state.totalQuestions}",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = BlueDark
                        )
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                if (state.isLoading) {
                    Column(
                        Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        ShimmerBox(
                            Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(20.dp))
                        )
                        repeat(4) {
                            ShimmerBox(
                                Modifier.fillMaxWidth().height(60.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                        }
                    }
                } else if (state.error != null) {
                    // Even if error, keep shimmer or show a mock question if needed
                    ErrorState(
                        message = state.error!!,
                        onRetry = { viewModel.onIntent(GameIntent.Start(gameType, childId as Int)) }
                    )
                } else {
                    val currentQuestion = state.currentQuestion
                    if (currentQuestion != null) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            Text(
                                currentQuestion.questionText,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black,
                                fontFamily = CairoFontFamily,
                                textAlign = TextAlign.Center,
                                color = BlueDark
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                currentQuestion.options.forEach { option ->
                                    ModernOptionCard(
                                        text = option,
                                        isSelected = state.selectedOption == option,
                                        isAnswered = state.isAnswered,
                                        isCorrect = currentQuestion.correctAnswer == option,
                                        onClick = {
                                            if (!state.isAnswered) viewModel.onIntent(
                                                GameIntent.SelectOption(option)
                                            )
                                        }
                                    )
                                }
                            }

                            Spacer(Modifier.weight(1f))

                            RawdatyButton(
                                text = if (!state.isAnswered) "تحقق من الإجابة" else "السؤال التالي",
                                onClick = {
                                    if (!state.isAnswered) viewModel.onIntent(GameIntent.CheckAnswer)
                                    else viewModel.onIntent(GameIntent.NextQuestion)
                                },
                                enabled = state.selectedOption != null || state.isAnswered,
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                backgroundColor = if (state.isAnswered) MintPrimary else BluePrimary
                            )
                        }
                    }
                }
            }
        }

        if (showResultDialog && resultData != null) {
            ResultDialog(result = resultData!!, onClose = {
                showResultDialog = false
                navigator.pop()
            })
        }
    }
}

@Composable
private fun ModernOptionCard(
    text: String,
    isSelected: Boolean,
    isAnswered: Boolean,
    isCorrect: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isAnswered && isCorrect -> ColorSuccess.copy(0.1f)
        isAnswered && isSelected && !isCorrect -> ColorError.copy(0.1f)
        isSelected -> BluePrimary.copy(0.1f)
        else -> White
    }

    val borderColor = when {
        isAnswered && isCorrect -> ColorSuccess
        isAnswered && isSelected && !isCorrect -> ColorError
        isSelected -> BluePrimary
        else -> Gray200
    }

    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, borderColor),
        shadowElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = CairoFontFamily,
                color = if (isAnswered && isCorrect) ColorSuccess else if (isAnswered && isSelected) ColorError else BlueDark
            )
        }
    }
}

@Composable
private fun ResultDialog(result: GameEffect.ShowResult, onClose: () -> Unit) {
    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {
            RawdatyButton(
                text = "تم العودة للرئيسية",
                onClick = onClose,
                modifier = Modifier.fillMaxWidth()
            )
        },
        title = {
            Text(
                "أحسنت يا بطل!",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Black,
                color = BluePrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(3) { i ->
                        Icon(
                            Icons.Default.Star,
                            null,
                            tint = if (i < result.stars) AmberPrimary else Gray200,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }
                Text(
                    "نتيجتك هي ${result.score} من ${result.total}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = CairoFontFamily,
                    color = BlueDark
                )
            }
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = White
    )
}
