package org.mohanned.rawdatyci_cdapp.presentation.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.domain.model.News
import org.mohanned.rawdatyci_cdapp.domain.model.NewsType
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.NewsIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.NewsViewModel

object ParentNewsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: NewsViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.onIntent(NewsIntent.Load)
        }

        Scaffold(
            containerColor = AppBg,
            topBar = {
                ModernHeader(
                    title = "الأخبار والتعميمات",
                    onBack = { navigator.pop() },
                    gradient = RawdatyGradients.HeroBlue,
                    headerHeight = 110.dp
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                if (state.isLoading) {
                    // ✅ Shimmer احترافي عند التحميل
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        repeat(5) { ShimmerBox(Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(18.dp))) }
                    }
                } else {
                    AnimateEntrance {
                        val news = state.news
                        val announcements = news.filter { it.type == NewsType.ANNOUNCEMENT }
                        val generalNews = news.filter { it.type == NewsType.NEWS }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 30.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // ✅ فصل التعميمات عن الأخبار
                            if (announcements.isNotEmpty()) {
                                item { SectionHeaderSmall("تعميمات هامة", Icons.Outlined.Campaign, AmberPrimary) }
                                items(announcements) { item ->
                                    SmallNewsCard(item, AmberPrimary) {
                                        // ✅ ربط الانتقال لشاشة التفاصيل
                                        navigator.push(ParentDetailContentScreen(
                                            title = item.title,
                                            body = item.body,
                                            author = item.authorName,
                                            date = item.createdAt,
                                            type = "announcement",
                                            icon = Icons.Outlined.Campaign
                                        ))
                                    }
                                }
                            }

                            if (generalNews.isNotEmpty()) {
                                item { SectionHeaderSmall("آخر الأخبار", Icons.Outlined.Newspaper, BluePrimary) }
                                items(generalNews) { item ->
                                    SmallNewsCard(item, BluePrimary) {
                                        navigator.push(ParentDetailContentScreen(
                                            title = item.title,
                                            body = item.body,
                                            author = item.authorName,
                                            date = item.createdAt,
                                            type = "news",
                                            icon = Icons.Outlined.Newspaper
                                        ))
                                    }
                                }
                            }
                            
                            if (news.isEmpty()) {
                                item { EmptyState(title = "لا توجد أخبار حالياً") }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeaderSmall(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Row(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        Text(title, fontWeight = FontWeight.Black, color = BlueDark, fontSize = 15.sp, fontFamily = CairoFontFamily)
    }
}

@Composable
private fun SmallNewsCard(item: News, accentColor: Color, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth().clickable(onClick = onClick),
        color = White,
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ✅ حجم Item رشيق
            Box(
                modifier = Modifier.size(44.dp).background(accentColor.copy(0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (item.type == NewsType.ANNOUNCEMENT) Icons.Outlined.Campaign else Icons.Outlined.Newspaper,
                    null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.title,
                    fontWeight = FontWeight.Bold,
                    color = BlueDark,
                    fontSize = 14.sp,
                    fontFamily = CairoFontFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    item.body,
                    color = Gray500,
                    fontSize = 11.sp,
                    fontFamily = CairoFontFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = Gray300, modifier = Modifier.size(20.dp))
        }
    }
}
