package org.mohanned.rawdatyci_cdapp.presentation.screens.parent.studentGames

import org.mohanned.rawdatyci_cdapp.domain.model.GameType
import org.mohanned.rawdatyci_cdapp.presentation.theme.RawdatyTheme
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyButton
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyOutlinedButton
import org.mohanned.rawdatyci_cdapp.presentation.theme.AppBg
import org.mohanned.rawdatyci_cdapp.presentation.theme.*


@Composable
fun GameResultScreen(
    gameType: GameType,
    score: Int,
    total: Int,
    stars: Int,
    elapsedSeconds: Int,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit,
) {
    val pct = if (total > 0) score * 100 / total else 0

    val (headline, emoji) = when {
        pct == 100 -> "ممتاز! 🏆"  to "🥇"
        pct >= 66  -> "أحسنت يا بطل!" to "🎉"
        pct >= 33  -> "عمل جيد!"       to "👍"
        else       -> "حاول مجدداً!"   to "💪"
    }

    val accentColor = when (gameType) {
        GameType.LETTERS -> BluePrimary
        GameType.NUMBERS -> MintPrimary
        GameType.COLORS  -> AmberPrimary
    }

    Box(modifier = Modifier.fillMaxSize().background(AppBg)) {
        // Points badge
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(8.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(AmberLight.copy(0.4f))
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(Icons.Default.Star, null, tint = AmberPrimary, modifier = Modifier.size(16.dp))
                Text(
                    "+${stars * 50} نقطة",
                    style = MaterialTheme.typography.labelLarge,
                    color = AmberPrimary,
                )
            }
        }

        // Close button
        IconButton(
            onClick  = onHome,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(8.dp),
        ) {
            Icon(Icons.Default.Close, null, tint = Gray600)
        }

        Column(
            modifier            = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Spacer(Modifier.height(48.dp))

            // Result emoji
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(emoji, fontSize = 80.sp)
            }

            Text(
                headline,
                style = MaterialTheme.typography.headlineLarge,
                color = BluePrimary,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Black
            )

            Text(
                when (gameType) {
                    GameType.LETTERS -> "لقد أتقنت الحروف العربية بنجاح!"
                    GameType.NUMBERS -> "لقد أكملت لعبة الأرقام بنجاح!"
                    GameType.COLORS  -> "لقد تعرفت على الألوان بشكل رائع!"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = Gray600,
            )

            // Score card
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
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        "النتيجة النهائية",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray400,
                    )
                    Text(
                        "$pct%",
                        style    = MaterialTheme.typography.displayLarge,
                        color    = accentColor,
                        fontSize = 64.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Black
                    )
                    // Stars
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(3) { i ->
                            Icon(
                                if (i < stars) Icons.Default.Star else Icons.Outlined.StarOutline,
                                null,
                                tint     = AmberPrimary,
                                modifier = Modifier.size(36.dp),
                            )
                        }
                    }
                    Text(
                        "$score / $total إجابة صحيحة",
                        style = MaterialTheme.typography.titleSmall,
                        color = Gray600,
                    )
                }
            }

            // Stats row
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Time
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(White)
                        .padding(14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(Icons.Default.Timer, null, tint = Gray500, modifier = Modifier.size(24.dp))
                        Text("الوقت", style = MaterialTheme.typography.labelSmall, color = Gray400)
                        val min = (elapsedSeconds / 60).toString().padStart(2, '0')
                        val sec = (elapsedSeconds % 60).toString().padStart(2, '0')
                        Text("$min:$sec", style = MaterialTheme.typography.titleMedium, color = Gray800)
                    }
                }

                // Accuracy
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(White)
                        .padding(14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(Icons.Default.FlashOn, null, tint = accentColor, modifier = Modifier.size(24.dp))
                        Text("الدقة", style = MaterialTheme.typography.labelSmall, color = Gray400)
                        Text(
                            if (pct == 100) "ممتاز!" else "$pct%",
                            style = MaterialTheme.typography.titleMedium,
                            color = accentColor,
                        )
                    }
                }
            }

            // Buttons
            RawdatyButton(
                text = "↺  إعادة اللعب",
                onClick = onPlayAgain,
                icon = Icons.Default.Refresh,
                backgroundColor = MintPrimary,
                modifier = Modifier.fillMaxWidth()
            )
            
            RawdatyOutlinedButton(
                text = "🏠 الرئيسية",
                onClick = onHome,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Previews ──────────────────────────────────────────
@Preview
@Composable
fun GameResultPreview() {
    RawdatyTheme {
        GameResultScreen(
            gameType       = GameType.LETTERS,
            score          = 10,
            total          = 10,
            stars          = 3,
            elapsedSeconds = 95,
            onPlayAgain    = {},
            onHome         = {},
        )
    }
}
