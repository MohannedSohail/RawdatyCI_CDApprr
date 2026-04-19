package org.mohanned.rawdatyci_cdapp.presentation.screens.parent.studentGames

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.mohanned.rawdatyci_cdapp.domain.model.GameType
import org.mohanned.rawdatyci_cdapp.presentation.components.GlassHeader
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyCard
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

data class GamesHubScreen(val childId: String = "") : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            containerColor = AppBg,
            topBar = {
                GlassHeader(
                    title = "عالم الألعاب التعليمية",
                    onBack = { navigator.pop() },
                    gradient = RawdatyGradients.HeroBlue,
                    headerHeight = 140.dp
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    "اختر لعبة لنبدأ التعلم والمرح!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontFamily = CairoFontFamily,
                    color = BlueDark
                )

                GameCategoryCard(
                    title = "لعبة الحروف",
                    desc = "تعلم حروف اللغة العربية بطريقة شيقة",
                    icon = Icons.Default.SortByAlpha,
                    color = BluePrimary,
                    onClick = { navigator.push(GamePlayScreen(GameType.LETTERS, childId)) }
                )

                GameCategoryCard(
                    title = "لعبة الأرقام",
                    desc = "مغامرة ممتعة في عالم الأرقام والحساب",
                    icon = Icons.Default.Filter1,
                    color = MintPrimary,
                    onClick = { navigator.push(GamePlayScreen(GameType.NUMBERS, childId)) }
                )

                GameCategoryCard(
                    title = "لعبة الألوان",
                    desc = "تعرف على الألوان والأشكال من حولك",
                    icon = Icons.Default.Palette,
                    color = AmberPrimary,
                    onClick = { navigator.push(GameType.COLORS.let { GamePlayScreen(it, childId) }) }
                )
            }
        }
    }
}

@Composable
private fun GameCategoryCard(
    title: String,
    desc: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    RawdatyCard(
        onClick = onClick,
        containerColor = White,
        elevation = 4.dp,
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(color.copy(0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(32.dp))
            }

            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Gray900, fontFamily = CairoFontFamily)
                Text(desc, style = MaterialTheme.typography.bodySmall, color = Gray500, fontFamily = CairoFontFamily)
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Gray50),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PlayArrow, null, tint = color, modifier = Modifier.size(20.dp))
            }
        }
    }
}
