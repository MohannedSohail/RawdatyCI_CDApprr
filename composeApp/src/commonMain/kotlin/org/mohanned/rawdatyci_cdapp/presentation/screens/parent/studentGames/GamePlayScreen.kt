package org.mohanned.rawdatyci_cdapp.presentation.screens.parent.studentGames
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import org.mohanned.rawdatyci_cdapp.domain.model.GameQuestion
import org.mohanned.rawdatyci_cdapp.domain.model.GameType
import org.mohanned.rawdatyci_cdapp.presentation.components.LoadingScreen
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyButton
import org.mohanned.rawdatyci_cdapp.presentation.theme.*


@Composable
fun GamePlayScreen(
    gameType: GameType,
    currentQ: Int,
    totalQ: Int,
    question: GameQuestion?,
    selectedOption: String?,
    isAnswered: Boolean,
    isCorrect: Boolean,
    isLoading: Boolean,
    onSelect: (String) -> Unit,
    onCheck: () -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
) {
    val accentColor = when (gameType) {
        GameType.LETTERS -> BluePrimary
        GameType.NUMBERS -> MintPrimary
        GameType.COLORS  -> AmberPrimary
    }

    val title = when (gameType) {
        GameType.LETTERS -> "ألعاب الحروف"
        GameType.NUMBERS -> "تعلم الأرقام"
        GameType.COLORS  -> "لعبة الألوان"
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFB))) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .statusBarsPadding(),
        ) {
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Help, null, tint = Gray500)
                }
                Spacer(Modifier.weight(1f))
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    color = BluePrimary,
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.Close, null, tint = Gray700)
                }
            }
        }

        // Progress bar
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .background(White)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(Icons.Default.Star, null, tint = AmberPrimary, modifier = Modifier.size(16.dp))
                    Text("المستوى الأول", style = MaterialTheme.typography.labelLarge, color = accentColor)
                }
                Text(
                    "$currentQ من $totalQ",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray500,
                )
            }
            LinearProgressIndicator(
                progress   = { if (totalQ > 0) currentQ.toFloat() / totalQ else 0f },
                modifier   = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color      = accentColor,
                trackColor = Gray200,
            )
        }

        if (isLoading || question == null) {
            LoadingScreen()
            return
        }

        Column(
            modifier            = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Question card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(White)
                    .padding(20.dp),
            ) {
                Column(
                    modifier            = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    // Letter/Number display
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(when (gameType) {
                                GameType.LETTERS -> BlueLight.copy(0.4f)
                                GameType.NUMBERS -> MintLight.copy(0.4f)
                                else -> AmberLight.copy(0.4f)
                            }),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            question.questionImage ?: question.correctAnswer,
                            fontSize = if (gameType == GameType.LETTERS) 60.sp else 44.sp,
                            color    = accentColor,
                        )
                    }

                    // Audio button
                    Button(
                        onClick        = {},
                        colors         = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape          = RoundedCornerShape(12.dp),
                        modifier       = Modifier.height(40.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                    ) {
                        Icon(Icons.Default.VolumeUp, null, tint = White, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            when (gameType) {
                                GameType.LETTERS -> "اسمع الحرف"
                                GameType.NUMBERS -> "اسمع العدد"
                                GameType.COLORS  -> "اسمع اللون"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            color = White,
                        )
                    }

                    Text(
                        question.questionText,
                        style     = MaterialTheme.typography.titleMedium,
                        color     = Gray800,
                    )
                }
            }

            // Answer options
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                question.options.forEach { option ->
                    val isSelected    = selectedOption == option
                    val isThisCorrect = isAnswered && option == question.correctAnswer
                    val isThisWrong   = isAnswered && isSelected && !isCorrect

                    val borderColor = when {
                        isThisCorrect -> MintPrimary
                        isThisWrong   -> ColorError
                        isSelected    -> accentColor
                        else          -> Gray200
                    }
                    val bgColor = when {
                        isThisCorrect -> MintLight.copy(0.4f)
                        isThisWrong   -> ColorError.copy(0.1f)
                        isSelected    -> accentColor.copy(.05f)
                        else          -> White
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(bgColor)
                            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
                            .clickable(enabled = !isAnswered) { onSelect(option) }
                            .padding(14.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        // Option icon placeholder
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) borderColor.copy(.15f) else Gray50),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                option.take(1),
                                fontSize = 24.sp,
                                color    = if (isSelected) borderColor else Gray400,
                            )
                        }

                        Text(
                            option,
                            style    = MaterialTheme.typography.titleSmall,
                            color    = if (isSelected) borderColor else Gray800,
                            modifier = Modifier.weight(1f),
                        )

                        // Status icon
                        if (isAnswered) {
                            Icon(
                                if (isThisCorrect) Icons.Default.CheckCircle
                                else if (isThisWrong) Icons.Default.Cancel
                                else Icons.Default.RadioButtonUnchecked,
                                null,
                                tint     = if (isThisCorrect) MintPrimary else if (isThisWrong) ColorError else Gray300,
                                modifier = Modifier.size(22.dp),
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, borderColor, CircleShape)
                                    .background(if (isSelected) borderColor else Color.Transparent),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, null, tint = White, modifier = Modifier.size(12.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Button
            if (!isAnswered) {
                RawdatyButton(
                    "تحقق من الإجابة 🔍",
                    onCheck,
                    enabled = selectedOption != null,
                )
            } else {
                RawdatyButton(
                    if (currentQ >= totalQ) "عرض النتيجة 🏆" else "السؤال التالي ←",
                    onNext,
                    backgroundColor = if (isCorrect) MintPrimary else BluePrimary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// ── Dummy Data ────────────────────────────────────────
private val dummyQuestion = GameQuestion(
    id            = 1,
    gameType      = GameType.LETTERS,
    level         = 1,
    questionText  = "ما هو الشيء الذي يبدأ بحرف (أ)؟",
    questionImage = "أ",
    correctAnswer = "أسد",
    options       = listOf("أسد", "بطة", "قطة", "سمكة"),
    audioUrl      = null,
)

// ── Previews ──────────────────────────────────────────
@Preview
@Composable
fun GamePlayPreview() {
    RawdatyTheme {
        GamePlayScreen(
            gameType       = GameType.LETTERS,
            currentQ       = 3,
            totalQ         = 10,
            question       = dummyQuestion,
            selectedOption = null,
            isAnswered     = false,
            isCorrect      = false,
            isLoading      = false,
            onSelect       = {},
            onCheck        = {},
            onNext         = {},
            onBack         = {},
        )
    }
}

@Preview
@Composable
fun GamePlayCorrectPreview() {
    RawdatyTheme {
        GamePlayScreen(
            gameType       = GameType.LETTERS,
            currentQ       = 3,
            totalQ         = 10,
            question       = dummyQuestion,
            selectedOption = "أسد",
            isAnswered     = true,
            isCorrect      = true,
            isLoading      = false,
            onSelect       = {},
            onCheck        = {},
            onNext         = {},
            onBack         = {},
        )
    }
}

@Preview
@Composable
fun GamePlayWrongPreview() {
    RawdatyTheme {
        GamePlayScreen(
            gameType       = GameType.LETTERS,
            currentQ       = 3,
            totalQ         = 10,
            question       = dummyQuestion,
            selectedOption = "بطة",
            isAnswered     = true,
            isCorrect      = false,
            isLoading      = false,
            onSelect       = {},
            onCheck        = {},
            onNext         = {},
            onBack         = {},
        )
    }
}

@Preview
@Composable
fun GamePlayNumbersPreview() {
    RawdatyTheme {
        GamePlayScreen(
            gameType       = GameType.NUMBERS,
            currentQ       = 5,
            totalQ         = 10,
            question       = dummyQuestion.copy(
                gameType      = GameType.NUMBERS,
                questionImage = "٥",
                questionText  = "كم عدد الأصابع في يد واحدة؟",
                correctAnswer = "خمسة",
                options       = listOf("ثلاثة", "أربعة", "خمسة", "ستة"),
            ),
            selectedOption = null,
            isAnswered     = false,
            isCorrect      = false,
            isLoading      = false,
            onSelect       = {},
            onCheck        = {},
            onNext         = {},
            onBack         = {},
        )
    }
}
