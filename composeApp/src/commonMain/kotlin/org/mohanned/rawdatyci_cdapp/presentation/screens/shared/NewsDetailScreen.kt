package org.mohanned.rawdatyci_cdapp.presentation.screens.shared

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.presentation.components.GlassHeader
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyCard
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.NewsIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.NewsViewModel

data class NewsDetailScreen(val newsId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: NewsViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(newsId) {
            viewModel.onIntent(NewsIntent.LoadNewsDetail(newsId))
        }

        Scaffold(
            containerColor = AppBg,
            topBar = {
                GlassHeader(
                    title = "تفاصيل الخبر",
                    onBack = { navigator.pop() },
                    gradient = RawdatyGradients.Primary,
                    headerHeight = 120.dp
                )
            }
        ) { padding ->
            val news = state.currentNews
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BluePrimary)
                }
            } else if (news == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("الخبر غير موجود", fontFamily = CairoFontFamily)
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    if (news.imageUrl != null) {
                        // Image implementation with Coil would go here
                        Box(Modifier.fillMaxWidth().height(250.dp).background(Gray200))
                    }

                    Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Surface(color = BlueLight.copy(0.4f), shape = RoundedCornerShape(8.dp)) {
                            Text(
                                text = news.type.name,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = BluePrimary,
                                fontWeight = FontWeight.Bold,
                                fontFamily = CairoFontFamily
                            )
                        }

                        Text(
                            text = news.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Gray900,
                            fontFamily = CairoFontFamily
                        )

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.AccessTime, null, tint = Gray400, modifier = Modifier.size(16.dp))
                            Text(news.createdAt, style = MaterialTheme.typography.labelSmall, color = Gray500, fontFamily = CairoFontFamily)
                            Spacer(Modifier.width(16.dp))
                            Icon(Icons.Default.Person, null, tint = Gray400, modifier = Modifier.size(16.dp))
                            Text(news.authorName, style = MaterialTheme.typography.labelSmall, color = Gray500, fontFamily = CairoFontFamily)
                        }

                        Divider(color = Gray100)

                        Text(
                            text = news.body,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Gray700,
                            lineHeight = 28.sp,
                            fontFamily = CairoFontFamily
                        )
                    }
                }
            }
        }
    }
}
