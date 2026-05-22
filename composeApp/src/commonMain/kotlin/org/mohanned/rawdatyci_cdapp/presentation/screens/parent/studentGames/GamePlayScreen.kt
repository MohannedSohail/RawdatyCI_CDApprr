package org.mohanned.rawdatyci_cdapp.presentation.screens.parent.studentGames

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
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

data class GamePlayScreen(val gameType: GameType, val level: Int, val childId: String = "") : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: GameViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.onIntent(GameIntent.Start(gameType, level, childId))
        }

        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                if (effect is GameEffect.ShowResult) {
                    navigator.replace(GameResultScreen(gameType, effect.score, effect.total, effect.stars, effect.elapsedSeconds, childId))
                }
            }
        }

        // ✅ أنيميشن انسيابي لشريط التقدم
        val progressAnimated by animateFloatAsState(
            targetValue = if (state.totalQuestions > 0) (state.currentIndex + 1).toFloat() / state.totalQuestions else 0f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
            label = "progress"
        )

        Scaffold(
            containerColor = AppBg,
            topBar = {
                GlassHeader(
                    title = "مغامرة التعلم",
                    subtitle = "المستوى $level",
                    onBack = { navigator.pop() },
                    gradient = RawdatyGradients.HeroBlue,
                    headerHeight = 110.dp
                )
            },
            bottomBar = {
                Surface(
                    color = White,
                    shadowElevation = 16.dp,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp).navigationBarsPadding()) {
                        if (state.isAnswered) {
                            FeedbackSection(state.isCorrect)
                            Spacer(Modifier.height(16.dp))
                        }
                        
                        RawdatyButton(
                            text = if (state.isAnswered) {
                                if (state.currentIndex + 1 == state.totalQuestions) "مشاهدة النتيجة" else "السؤال التالي"
                            } else "تأكيد الإجابة",
                            onClick = { 
                                if (state.isAnswered) viewModel.onIntent(GameIntent.NextQuestion)
                                else viewModel.onIntent(GameIntent.CheckAnswer)
                            },
                            enabled = state.selectedOption != null || state.isAnswered,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            backgroundColor = if (state.isAnswered) MintPrimary else BluePrimary
                        )
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                if (state.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BluePrimary)
                    }
                } else if (state.currentQuestion != null) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Progress Section
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("السؤال ${state.currentIndex + 1} من ${state.totalQuestions}", fontWeight = FontWeight.Bold, color = BlueDark, fontFamily = CairoFontFamily)
                            LinearProgressIndicator(
                                progress = { progressAnimated }, // ✅ استخدام القيمة المتحركة
                                modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
                                color = BluePrimary,
                                trackColor = Gray200
                            )
                        }

                        // ✅ أنيميشن الانتقال بين الأسئلة (Slide + Fade)
                        AnimatedContent(
                            targetState = state.currentIndex,
                            transitionSpec = {
                                (slideInHorizontally { width -> width } + fadeIn(tween(400))).togetherWith(
                                    slideOutHorizontally { width -> -width } + fadeOut(tween(400))
                                )
                            },
                            label = "question_transition",
                            modifier = Modifier.fillMaxWidth().weight(1f)
                        ) { index ->
                            // سيتم تحديث المحتوى بناءً على السؤال الحالي في الـ state
                            val question = state.questions.getOrNull(index) ?: state.currentQuestion!!
                            
                            Column(
                                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Question Card
                                RawdatyCard(containerColor = White, elevation = 4.dp) {
                                    Text(
                                        text = question.questionText,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                                        fontFamily = CairoFontFamily
                                    )
                                }

                                // Options
                                question.options.forEach { option ->
                                    OptionItem(
                                        text = option,
                                        isSelected = state.selectedOption == option,
                                        isAnswered = state.isAnswered,
                                        isCorrect = option == question.correctAnswer,
                                        onClick = { if (!state.isAnswered) viewModel.onIntent(GameIntent.SelectOption(option)) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionItem(text: String, isSelected: Boolean, isAnswered: Boolean, isCorrect: Boolean, onClick: () -> Unit) {
    val borderColor = when {
        isAnswered && isCorrect -> ColorSuccess
        isAnswered && isSelected && !isCorrect -> ColorError
        isSelected -> BluePrimary
        else -> Gray200
    }
    
    val bgColor = when {
        isAnswered && isCorrect -> ColorSuccess.copy(0.1f)
        isAnswered && isSelected && !isCorrect -> ColorError.copy(0.1f)
        isSelected -> BluePrimary.copy(0.1f)
        else -> White
    }

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(64.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(2.dp, borderColor),
        color = bgColor
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text, 
                fontWeight = FontWeight.Bold, 
                fontSize = 18.sp, 
                fontFamily = CairoFontFamily, 
                color = if (isAnswered && isCorrect) ColorSuccess else if (isAnswered && isSelected) ColorError else Color.Black
            )
        }
    }
}

@Composable
private fun FeedbackSection(isCorrect: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (isCorrect) Icons.Default.Check else Icons.Default.Close,
            null,
            tint = if (isCorrect) ColorSuccess else ColorError
        )
        Spacer(Modifier.width(8.dp))
        Text(
            if (isCorrect) "أحسنت! إجابة صحيحة" else "للأسف، إجابة خاطئة",
            color = if (isCorrect) ColorSuccess else ColorError,
            fontWeight = FontWeight.Bold,
            fontFamily = CairoFontFamily
        )
    }
}
