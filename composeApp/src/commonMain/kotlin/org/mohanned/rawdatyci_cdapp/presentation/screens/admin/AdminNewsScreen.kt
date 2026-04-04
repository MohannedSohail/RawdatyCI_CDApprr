package org.mohanned.rawdatyci_cdapp.presentation.screens.admin

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import org.mohanned.rawdatyci_cdapp.domain.model.News
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

@Composable
fun AdminNewsScreen(
    news: List<News>,
    query: String,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    canLoadMore: Boolean,
    onSearch: (String) -> Unit,
    onLoadMore: () -> Unit,
    onAdd: () -> Unit,
    onEdit: (News) -> Unit,
    onDelete: (Int) -> Unit,
    onBack: () -> Unit,
) {
    val listState = rememberLazyListState()

    // Load more trigger
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && lastVisibleIndex >= news.size - 2 && canLoadMore && !isLoadingMore && !isLoading) {
                    onLoadMore()
                }
            }
    }

    Scaffold(
        containerColor = AppBg,
        topBar = {
            GlassHeader(
                title = "إدارة الأخبار والإعلانات",
                onBack = onBack,
                gradient = RawdatyGradients.AdminHeader,
                headerHeight = 140.dp
            )
        },
        floatingActionButton = {
            RawdatyFAB(onClick = onAdd, icon = Icons.Default.Add)
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Premium Search Bar with Cairo
            Surface(
                color = White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = onSearch,
                        placeholder = {
                            Text(
                                "بحث في الأخبار أو الإعلانات...",
                                fontFamily = CairoFontFamily,
                                color = Gray400
                            )
                        },
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

            if (isLoading) {
                LazyColumn(
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(6) { NotificationItemShimmer() }
                }
            } else if (news.isEmpty()) {
                EmptyState(
                    icon = Icons.Outlined.Newspaper,
                    title = "لا توجد أخبار منشورة",
                    subtitle = "ابدأ بإضافة أول خبر أو إعلان لمشاركته مع الجميع.",
                    actionText = "إضافة خبر جديد",
                    onAction = onAdd
                )
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(news, key = { it.id }) { item ->
                        AdminNewsItem(
                            news = item,
                            onEdit = { onEdit(item) },
                            onDelete = { onDelete(item.id) }
                        )
                    }

                    if (isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = BluePrimary,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun AdminNewsItem(
    news: News,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    RawdatyCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Priority/Status Icon
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (news.isPinned) AmberLight.copy(0.4f) else BlueLight.copy(0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (news.isPinned) Icons.Default.PushPin else Icons.Default.Article,
                    null,
                    tint = if (news.isPinned) AmberPrimary else BluePrimary,
                    modifier = Modifier.size(28.dp)
                )
            }

            // News Content
            Column(Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        news.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Gray900,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                        fontFamily = CairoFontFamily
                    )
                    if (news.isPinned) {
                        RoleTag("مثبت", useSmallText = true)
                    }
                }
                Text(
                    news.body,
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray600,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = CairoFontFamily
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.AccessTime,
                            null,
                            tint = Gray400,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            news.createdAt,
                            style = MaterialTheme.typography.labelSmall,
                            color = Gray400,
                            fontFamily = CairoFontFamily
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Person,
                            null,
                            tint = Gray400,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            news.authorName,
                            style = MaterialTheme.typography.labelSmall,
                            color = Gray400,
                            fontFamily = CairoFontFamily
                        )
                    }
                }
            }

            // Small Actions in a vertical stack
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(36.dp).background(Gray50, CircleShape)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        null,
                        tint = BluePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp).background(Gray50, CircleShape)
                ) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        null,
                        tint = ColorError.copy(0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun AdminNewsPreview() {
    RawdatyTheme {
        AdminNewsScreen(
            news = listOf(
                News(
                    1,
                    "فتح باب التسجيل",
                    "يسرنا إبلاغكم ببدء فترة التسجيل المبكر للفصل القادم.",
                    "الإدارة",
                    true,
                    "Hossam",
                    "24 مارس"
                ),
                News(
                    2,
                    "صيانة دورية",
                    "سيتم إجراء صيانة دورية للمرافق غداً.",
                    "الخدمات",
                    true,
                    "AlHossam",
                    "30 مارس"
                )
            ),
            query = "",
            isLoading = false,
            isLoadingMore = false,
            canLoadMore = true,
            onSearch = {},
            onLoadMore = {},
            onAdd = {},
            onEdit = {},
            onDelete = {},
            onBack = {}
        )
    }
}
