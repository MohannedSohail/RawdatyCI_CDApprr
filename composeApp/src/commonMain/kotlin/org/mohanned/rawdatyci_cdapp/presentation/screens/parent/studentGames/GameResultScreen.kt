package org.mohanned.rawdatyci_cdapp.presentation.screens.parent.studentGames

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import org.mohanned.rawdatyci_cdapp.domain.model.GameType
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyButton
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyCard
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

data class GameResultScreen(
    val gameType: GameType,
    val score: Int,
    val total: Int,
    val stars: Int,
    val elapsedSeconds: Int,
    val childId: String = ""
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Box(
            modifier = Modifier.fillMaxSize().background(RawdatyGradients.Splash),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "رائع جداً!",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = White,
                    fontFamily = CairoFontFamily
                )

                RawdatyCard(containerColor = White, elevation = 8.dp, shape = RoundedCornerShape(32.dp)) {
                    Column(
                        modifier = Modifier.padding(32.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            repeat(3) { i ->
                                Icon(
                                    Icons.Default.Star,
                                    null,
                                    tint = if (i < stars) AmberPrimary else Gray200,
                                    modifier = Modifier.size(64.dp)
                                )
                            }
                        }

                        Text(
                            "لقد حصلت على $score من $total",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = BlueDark,
                            fontFamily = CairoFontFamily
                        )

                        Text(
                            "الوقت المستغرق: $elapsedSeconds ثانية",
                            color = Gray500,
                            fontFamily = CairoFontFamily
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    RawdatyButton(
                        text = "لعب مرة أخرى",
                        onClick = { navigator.replace(GamePlayScreen(gameType, childId)) },
                        icon = Icons.Default.Replay,
                        modifier = Modifier.weight(1f).height(56.dp)
                    )
                    RawdatyButton(
                        text = "الرئيسية",
                        onClick = { navigator.popUntilRoot() },
                        icon = Icons.Default.Home,
                        backgroundColor = White,
                        textColor = BluePrimary,
                        modifier = Modifier.weight(1f).height(56.dp)
                    )
                }
            }
        }
    }
}
