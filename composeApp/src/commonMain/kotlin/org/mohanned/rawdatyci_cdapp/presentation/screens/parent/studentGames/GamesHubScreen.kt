package org.mohanned.rawdatyci_cdapp.presentation.screens.parent.studentGames


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import org.mohanned.rawdatyci_cdapp.domain.model.GameType
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyHeader
import org.mohanned.rawdatyci_cdapp.presentation.components.SectionHeader
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.RawdatyTheme
import org.mohanned.rawdatyci_cdapp.presentation.theme.White


@Composable
fun GamesHubScreen(
    onGameSelect: (GameType) -> Unit,
    onBack: () -> Unit,
) {
    val games = listOf(
        Triple(GameType.LETTERS, "حروف الهجاء",  " أ تعلم الحروف العربية بطريقة ممتعة"),
        Triple(GameType.NUMBERS, "عالم الأرقام",  "تعلم الأرقام والعد ١٢٣"),
        Triple(GameType.COLORS,  "مملكة الألوان", "تعرف على الألوان وأسماءها 🎨"),
    )

    Column(modifier = Modifier.fillMaxSize().background(AppBg)) {
        RawdatyHeader("الألعاب التعليمية", onBack = onBack, gradient = RawdatyGradients.Header)

        LazyColumn(
            contentPadding      = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Featured banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(RawdatyGradients.HeroBlue)
                        .padding(20.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AmberPrimary)
                                    .padding(horizontal = 10.dp, vertical = 3.dp),
                            ) {
                                Text(
                                    "جديد اليوم",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = White,
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "تعلم وألعب مع روضتي 🎓",
                                style = MaterialTheme.typography.titleLarge,
                                color = White,
                            )
                            Text(
                                "ألعاب تعليمية تنمي مهارات طفلك",
                                style = MaterialTheme.typography.bodySmall,
                                color = White.copy(.8f),
                            )
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick        = { onGameSelect(GameType.LETTERS) },
                                colors         = ButtonDefaults.buttonColors(containerColor = White),
                                shape          = RoundedCornerShape(10.dp),
                                modifier       = Modifier.height(36.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp),
                            ) {
                                Text(
                                    "▶ ابدأ اللعب",
                                    color = BluePrimary,
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        }
                        Text("🚀", fontSize = 60.sp)
                    }
                }
            }

            item { SectionHeader("اختر لعبة") }

            // Games list
           // items(games) { (type, title, subtitle, emoji) ->
            items(games) { (type, title, subtitle) ->
                val (gradient, _) = when (type) {
                    GameType.LETTERS -> RawdatyGradients.StatBlue to BlueLight.copy(0.4f)
                    GameType.NUMBERS -> RawdatyGradients.StatMint  to MintLight.copy(0.4f)
                    GameType.COLORS  -> RawdatyGradients.StatAmber to AmberLight.copy(0.4f)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(White)
                        .clickable { onGameSelect(type) }
                        .padding(16.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    // Emoji icon
//                    Box(
//                        modifier = Modifier
//                            .size(64.dp)
//                            .clip(RoundedCornerShape(16.dp))
//                            .background(bgColor),
//                        contentAlignment = Alignment.Center,
//                    ) {
//                        Text(emoji, fontSize = 28.sp)
//                    }

                    Column(Modifier.weight(1f)) {
                        Text(
                            title,
                            style = MaterialTheme.typography.titleSmall,
                            color = Gray800,
                        )
                        Text(
                            subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = Gray500,
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            repeat(3) {
                                Icon(
                                    Icons.Default.Star,
                                    null,
                                    tint     = AmberPrimary,
                                    modifier = Modifier.size(14.dp),
                                )
                            }
                            Text(
                                "مناسب للمستوى التمهيدي",
                                style = MaterialTheme.typography.labelSmall,
                                color = Gray400,
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(gradient),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            null,
                            tint     = White,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────
@Preview
@Composable
fun GamesHubPreview() {
    RawdatyTheme {
        GamesHubScreen(
            onGameSelect = {},
            onBack       = {},
        )
    }
}
