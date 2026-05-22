package org.mohanned.rawdatyci_cdapp.presentation.screens.parent

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
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
import org.mohanned.rawdatyci_cdapp.presentation.components.AnimateEntrance
import org.mohanned.rawdatyci_cdapp.presentation.components.ModernHeader
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyCard
import org.mohanned.rawdatyci_cdapp.presentation.screens.parent.studentGames.GamePlayScreen
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

data class ParentGamesScreen(val childId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var selectedType by remember { mutableStateOf<GameType?>(null) }

        val gameCategories = listOf(
            GameCategory("أرقامنا الممتعة", "تعلم العد والحساب البسيط", Icons.Default.Numbers, BluePrimary, GameType.NUMBERS),
            GameCategory("حروفي الجميلة", "اكتشف الحروف والكلمات الأولى", Icons.Default.Abc, AmberPrimary, GameType.LETTERS),
            GameCategory("عالم الألوان", "لون حياتك وتعلم أسماء الألوان", Icons.Default.Palette, MintPrimary, GameType.COLORS),
            GameCategory("صديقي الحيوان", "تعرف على أسماء وأصوات الحيوانات", Icons.Default.Pets, Color(0xFFE91E63), GameType.ANIMALS),
            GameCategory("سلة الفواكه", "اكتشف أنواع الفواكه اللذيذة", Icons.Default.EmojiFoodBeverage, Color(0xFFFF5722), GameType.FRUITS)
        )

        Scaffold(
            containerColor = AppBg,
            topBar = {
                ModernHeader(
                    title = if (selectedType == null) "رحلة التعلم" else "اختر المستوى",
                    subtitle = if (selectedType == null) "ألعاب تعليمية ممتعة لطفلك" else "تدرج في الصعوبة لتنمية المهارات",
                    onBack = { if (selectedType == null) navigator.pop() else selectedType = null },
                    gradient = RawdatyGradients.HeroBlue,
                    headerHeight = 150.dp
                )
            }
        ) { padding ->
            AnimateEntrance {
                Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                    if (selectedType == null) {
                        // عرض تصنيفات الألعاب
                        LazyColumn(
                            contentPadding = PaddingValues(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(gameCategories) { cat ->
                                CategoryCard(cat) { selectedType = cat.type }
                            }
                        }
                    } else {
                        // عرض المستويات للتصنيف المختار
                        LevelSelectionGrid(selectedType!!, childId) { level ->
                            navigator.push(GamePlayScreen(selectedType!!, level, childId))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(cat: GameCategory, onClick: () -> Unit) {
    RawdatyCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        containerColor = White,
        elevation = 4.dp,
        shape = RoundedCornerShape(28.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(20.dp)).background(cat.color.copy(0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(cat.icon, null, tint = cat.color, modifier = Modifier.size(34.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(cat.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = BlueDark, fontFamily = CairoFontFamily)
                Text(cat.subtitle, style = MaterialTheme.typography.bodySmall, color = Gray500, fontFamily = CairoFontFamily)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = Gray300)
        }
    }
}

@Composable
private fun LevelSelectionGrid(type: GameType, childId: String, onLevelClick: (Int) -> Unit) {
    val levels = listOf(
        LevelItem(1, "مبتدئ", "أساسيات ممتعة", Icons.Default.SentimentSatisfiedAlt, BluePrimary),
        LevelItem(2, "متوسط", "تحديات ذكية", Icons.Default.Star, AmberPrimary),
        LevelItem(3, "محترف", "أنت بطل حقيقي", Icons.Default.WorkspacePremium, Color(0xFF9C27B0))
    )

    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        levels.forEach { level ->
            RawdatyCard(
                onClick = { onLevelClick(level.id) },
                containerColor = White,
                elevation = 6.dp,
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(4.dp)) {
                    Box(
                        modifier = Modifier.size(50.dp).background(level.color.copy(0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${level.id}", color = level.color, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(level.name, fontWeight = FontWeight.Bold, color = BlueDark, fontFamily = CairoFontFamily)
                        Text(level.desc, fontSize = 12.sp, color = Gray500, fontFamily = CairoFontFamily)
                    }
                    Icon(level.icon, null, tint = level.color)
                }
            }
        }
    }
}

private data class GameCategory(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val type: GameType
)

private data class LevelItem(
    val id: Int,
    val name: String,
    val desc: String,
    val icon: ImageVector,
    val color: Color
)
