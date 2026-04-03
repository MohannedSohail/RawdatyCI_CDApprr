package org.mohanned.rawdatyci_cdapp.presentation.screens.shared

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import org.mohanned.rawdatyci_cdapp.domain.model.AppNotification
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

@Composable
fun NotificationsScreen(
    notifications: List<AppNotification>,
    unreadCount: Int,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    canLoadMore: Boolean,
    selectedTab: Int,
    onTabChange: (Int) -> Unit,
    onLoadMore: () -> Unit,
    onMarkRead: (Int) -> Unit,
    onMarkAllRead: () -> Unit,
    onBack: () -> Unit,
) {
    val tabs = listOf("الكل", "غير مقروء")
    val listState = rememberLazyListState()

    // Infinite Scrolling
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && lastVisibleIndex >= notifications.size - 2 && canLoadMore && !isLoadingMore && !isLoading) {
                    onLoadMore()
                }
            }
    }

    Scaffold(
        containerColor = AppBg,
        topBar = { 
            Surface(
                color = White,
                shadowElevation = 4.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth().statusBarsPadding()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = BlueDark)
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "مركز التنبيهات",
                                style = MaterialTheme.typography.titleLarge,
                                color = BlueDark,
                                fontWeight = FontWeight.Bold,
                                fontFamily = CairoFontFamily
                            )
                        }
                        
                        if (unreadCount > 0) {
                            TextButton(onClick = onMarkAllRead) {
                                Text(
                                    "تحديد الكل كمقروء",
                                    color = BluePrimary,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = CairoFontFamily
                                )
                            }
                        }
                    }
                    
                    // Tabs Section
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Gray50)
                            .padding(4.dp)
                    ) {
                        tabs.forEachIndexed { i, tab ->
                            val isSelected = selectedTab == i
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) White else Color.Transparent)
                                    .clickable { onTabChange(i) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        tab,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) BluePrimary else Gray500,
                                        fontFamily = CairoFontFamily
                                    )
                                    if (i == 1 && unreadCount > 0) {
                                        Box(
                                            modifier = Modifier.size(18.dp).clip(CircleShape).background(ColorError),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "$unreadCount",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = CairoFontFamily
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(8) { NotificationItemShimmer() }
                }
            } else if (notifications.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(Modifier.size(120.dp).clip(CircleShape).background(BlueLight.copy(0.3f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.NotificationsNone, null, tint = BluePrimary, modifier = Modifier.size(64.dp))
                    }
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "لا توجد تنبيهات جديدة",
                        style = MaterialTheme.typography.titleLarge,
                        color = Gray800,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CairoFontFamily
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "سيصلك إشعار هنا عند وجود أي تحديث بخصوص نشاطات أطفالك أو إعلانات الروضة",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Gray500,
                        textAlign = TextAlign.Center,
                        fontFamily = CairoFontFamily
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(notifications, key = { it.id }) { notif ->
                        val icon = when (notif.type) {
                            "news" -> Icons.Default.Campaign
                            "attendance" -> Icons.Default.EventAvailable
                            "message" -> Icons.AutoMirrored.Filled.SpeakerNotes
                            "complaint" -> Icons.Default.AssignmentLate
                            else -> Icons.Default.NotificationsActive
                        }
                        
                        NotificationItem(
                            title = notif.title,
                            body = notif.body,
                            time = notif.createdAt,
                            isUnread = !notif.isRead,
                            icon = icon,
                            onClick = { onMarkRead(notif.id) }
                        )
                    }

                    if (isLoadingMore) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = BluePrimary, strokeWidth = 2.dp, modifier = Modifier.size(32.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
