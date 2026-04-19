package org.mohanned.rawdatyci_cdapp.presentation.screens.admin

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.domain.model.News
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.NewsEffect
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.NewsIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.NewsState
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.NewsViewModel

object AdminNewsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: NewsViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            viewModel.onIntent(NewsIntent.Load)
            viewModel.effect.collect { effect ->
                when (effect) {
                    is NewsEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        AdminNewsScreenContent(
            state = state,
            snackbarHostState = snackbarHostState,
            onIntent = viewModel::onIntent,
            onAdd = { navigator.push(AdminAddNewsScreen(null)) },
            onEdit = { news -> navigator.push(AdminAddNewsScreen(news.id)) },
            onBack = { navigator.pop() }
        )
    }
}

@Composable
fun AdminNewsScreenContent(
    state: NewsState,
    snackbarHostState: SnackbarHostState,
    onIntent: (NewsIntent) -> Unit,
    onAdd: () -> Unit,
    onEdit: (News) -> Unit,
    onBack: () -> Unit,
) {
    val listState = rememberLazyListState()

    if (state.showDeleteDialog) {
        DeleteConfirmDialog(
            title = "تأكيد حذف الخبر",
            message = "هل أنت متأكد من رغبتك في حذف هذا الخبر؟ لا يمكن التراجع عن هذا الإجراء.",
            onConfirm = { onIntent(NewsIntent.ConfirmDelete) },
            onDismiss = { onIntent(NewsIntent.DismissDelete) }
        )
    }

    Scaffold(
        containerColor = AppBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            GlassHeader(
                title = "إدارة الأخبار والإعلانات",
                onBack = onBack,
                gradient = RawdatyGradients.AdminHeader,
                headerHeight = 140.dp
            )
        },
        floatingActionButton = {
            RawdatyFAB(onClick = onAdd, icon = Icons.Default.PostAdd)
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Surface(
                color = White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = "", 
                        onValueChange = { onIntent(NewsIntent.Search(it)) },
                        placeholder = { Text("بحث في الأخبار...", fontFamily = CairoFontFamily, color = Gray400) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = BluePrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontFamily = CairoFontFamily),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Gray200,
                            focusedContainerColor = Gray50,
                            unfocusedContainerColor = Gray50
                        )
                    )
                }
            }

            if (state.isLoading && state.news.isEmpty()) {
                LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(6) { ShimmerBox(Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(16.dp))) }
                }
            } else if (state.news.isEmpty()) {
                EmptyState(
                    icon = Icons.Outlined.Newspaper,
                    title = if (state.error != null) "خطأ في التحميل" else "لا توجد أخبار حالياً",
                    subtitle = state.error ?: "ابدأ بإضافة أول خبر أو إعلان لمشاركته مع الجميع.",
                    actionText = if (state.error != null) "إعادة المحاولة" else "إضافة خبر جديد",
                    onAction = { if (state.error != null) onIntent(NewsIntent.Load) else onAdd() }
                )
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.news, key = { it.id }) { newsItem ->
                        AdminNewsItem(
                            news = newsItem,
                            onEdit = { onEdit(newsItem) },
                            onDelete = { onIntent(NewsIntent.DeleteRequest(newsItem.id)) }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun AdminNewsItem(news: News, onEdit: () -> Unit, onDelete: () -> Unit) {
    RawdatyCard(modifier = Modifier.fillMaxWidth(), elevation = 2.dp, containerColor = White) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(12.dp)) {
            Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(if (news.isVisible) AmberLight.copy(0.4f) else BlueLight.copy(0.4f)), contentAlignment = Alignment.Center) {
                Icon(if (news.isVisible) Icons.Default.PushPin else Icons.Default.Article, null, tint = if (news.isVisible) AmberPrimary else BluePrimary, modifier = Modifier.size(24.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(news.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Gray900, maxLines = 1, overflow = TextOverflow.Ellipsis, fontFamily = CairoFontFamily)
                Text(news.body, style = MaterialTheme.typography.labelSmall, color = Gray600, maxLines = 2, overflow = TextOverflow.Ellipsis, fontFamily = CairoFontFamily)
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp).background(Gray50, CircleShape)) {
                    Icon(Icons.Default.Edit, null, tint = BluePrimary, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp).background(Gray50, CircleShape)) {
                    Icon(Icons.Default.DeleteOutline, null, tint = ColorError.copy(0.8f), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
