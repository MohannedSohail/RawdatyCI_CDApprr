package org.mohanned.rawdatyci_cdapp.presentation.screens.parent.studentGames

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import org.mohanned.rawdatyci_cdapp.domain.model.GameType
import org.mohanned.rawdatyci_cdapp.presentation.components.AnimateEntrance
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

        // ✅ أنيميشن عداد النتيجة
        var animatedScore by remember { mutableStateOf(0) }
        LaunchedEffect(Unit) {
            repeat(score + 1) { i ->
                animatedScore = i
                delay(100)
            }
        }

        Box(
            modifier = Modifier.fillMaxSize().background(RawdatyGradients.Splash),
            contentAlignment = Alignment.Center
        ) {
            AnimateEntrance {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(30.dp)
                ) {
                    // Header الاحتفالي
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            Icons.Default.WorkspacePremium,
                            null,
                            tint = AmberPrimary,
                            modifier = Modifier.size(80.dp).animateContentSize()
                        )
                        Text(
                            text = if (stars >= 2) "أحسنت يا بطل!" else "محاولة رائعة!",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Black,
                            color = White,
                            fontFamily = CairoFontFamily,
                            textAlign = TextAlign.Center
                        )
                    }

                    RawdatyCard(
                        containerColor = White,
                        elevation = 15.dp,
                        shape = RoundedCornerShape(40.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            // ✅ نجوم التميز بأنيميشن ظهور متتابع
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                repeat(3) { i ->
                                    val isFilled = i < stars
                                    var starVisible by remember { mutableStateOf(false) }
                                    LaunchedEffect(Unit) {
                                        delay(500L + (i * 300L))
                                        starVisible = true
                                    }

                                    AnimatedVisibility(
                                        visible = starVisible,
                                        enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn()
                                    ) {
                                        Icon(
                                            Icons.Default.Star,
                                            null,
                                            tint = if (isFilled) AmberPrimary else Gray200,
                                            modifier = Modifier.size(70.dp)
                                        )
                                    }
                                }
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "لقد حصلت على",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Gray500,
                                    fontFamily = CairoFontFamily
                                )
                                Text(
                                    "$animatedScore من $total",
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Black,
                                    color = BluePrimary,
                                    fontFamily = CairoFontFamily
                                )
                            }

                            // تفاصيل إضافية فخمة
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                StatBadge("الزمن", "$elapsedSeconds ثانية", Icons.Default.Star, MintPrimary, Modifier.weight(1f))
                                StatBadge("المستوى", "مكتمل", Icons.Default.Check, BluePrimary, Modifier.weight(1f))
                            }
                        }
                    }

                    // أزرار التحكم الرشيقة
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        RawdatyButton(
                            text = "لعب مجدداً",
                            onClick = { navigator.replace(GamePlayScreen(gameType, 1, childId)) },
                            icon = Icons.Default.Replay,
                            modifier = Modifier.weight(1.2f).height(60.dp)
                        )
                        RawdatyButton(
                            text = "الرئيسية",
                            onClick = { navigator.popUntilRoot() },
                            icon = Icons.Default.Home,
                            backgroundColor = AmberPrimary,
                            textColor = White,
                            modifier = Modifier.weight(1f).height(60.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBadge(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(label, fontSize = 11.sp, color = color, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
            Text(value, fontSize = 14.sp, color = BlueDark, fontWeight = FontWeight.Black, fontFamily = CairoFontFamily)
        }
    }
}
