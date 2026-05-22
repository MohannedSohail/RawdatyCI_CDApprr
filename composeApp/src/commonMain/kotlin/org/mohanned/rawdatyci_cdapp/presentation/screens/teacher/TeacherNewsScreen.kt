package org.mohanned.rawdatyci_cdapp.presentation.screens.teacher

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.domain.model.News
import org.mohanned.rawdatyci_cdapp.domain.model.NewsType
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.screens.parent.ParentDetailContentScreen
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.*

object TeacherNewsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
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
                    subtitle = "آخر المستجدات التربوية والإدارية",
                    onBack = if (navigator?.canPop == true) { { navigator.pop() } } else null,
                    gradient = RawdatyGradients.HeroBlue,
                    headerHeight = 150.dp
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                if (state.isLoading && state.news.isEmpty()) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        repeat(5) { ShimmerBox(Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(18.dp))) }
                    }
                } else if (state.news.isEmpty()) {
                    EmptyState(
                        icon = Icons.Outlined.Newspaper,
                        title = "لا توجد أخبار حالياً",
                        subtitle = "سيتم إرسال تنبيه لكِ عند نشر أي تعميم جديد."
                    )
                } else {
                    AnimateEntrance {
                        val announcements = state.news.filter { it.type == NewsType.ANNOUNCEMENT || it.isVisible }
                        val generalNews = state.news.filter { it.type == NewsType.NEWS && !it.isVisible }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 10.dp, bottom = 30.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (announcements.isNotEmpty()) {
                                item { SectionHeaderSmall("تعميمات وإعلانات هامة", Icons.Outlined.Campaign, AmberPrimary) }
                                items(announcements) { item ->
                                    SmallTeacherNewsCard(item, AmberPrimary) {
                                        navigator?.push(ParentDetailContentScreen(
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
                                item { SectionHeaderSmall("آخر الأخبار والمستجدات", Icons.Outlined.Newspaper, BluePrimary) }
                                items(generalNews) { item ->
                                    SmallTeacherNewsCard(item, BluePrimary) {
                                        navigator?.push(ParentDetailContentScreen(
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
                            
                            item { Spacer(Modifier.height(20.dp)) }
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
private fun SmallTeacherNewsCard(item: News, accentColor: Color, onClick: () -> Unit) {
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
                Text(
                    item.createdAt,
                    color = Gray400,
                    fontSize = 10.sp,
                    fontFamily = CairoFontFamily,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = Gray300, modifier = Modifier.size(20.dp))
        }
    }
}
