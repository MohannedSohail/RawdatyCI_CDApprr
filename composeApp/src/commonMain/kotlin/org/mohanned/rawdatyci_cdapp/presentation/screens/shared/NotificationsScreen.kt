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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.domain.model.AppNotification
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.*

object NotificationsScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel: NotificationsViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            viewModel.onIntent(NotificationsIntent.Load)
            viewModel.effect.collect { effect ->
                if (effect is NotificationsEffect.ShowMessage) snackbarHostState.showSnackbar(effect.message)
            }
        }

        Scaffold(
            containerColor = AppBg,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                ModernHeader(
                    title = "التنبيهات",
                    subtitle = "تابع آخر التحديثات والنشاطات",
                    gradient = RawdatyGradients.HeroBlue,
                    headerHeight = 120.dp,
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                // Quick Filters
                NotificationFilters(
                    unreadCount = state.unreadCount,
                    onMarkAllRead = { viewModel.onIntent(NotificationsIntent.MarkAllRead) }
                )

                if (state.isLoading) {
                    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(6) { ShimmerBox(Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(20.dp))) }
                    }
                } else if (state.notifications.isEmpty()) {
                    EmptyState(
                        icon = Icons.Default.NotificationsNone,
                        title = "لا توجد تنبيهات",
                        subtitle = "ستظهر هنا التنبيهات المتعلقة بأطفالك والروضة."
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.notifications) { notification ->
                            NotificationCard(
                                notification = notification,
                                onClick = { viewModel.onIntent(NotificationsIntent.MarkRead(notification.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationFilters(unreadCount: Int, onMarkAllRead: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = BlueLight.copy(0.3f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                "لديك $unreadCount تنبيهات غير مقروءة",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium,
                color = BluePrimary,
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold
            )
        }
        
        if (unreadCount > 0) {
            TextButton(onClick = onMarkAllRead) {
                Text("تحديد الكل كمقروء", color = Gray500, fontFamily = CairoFontFamily, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun NotificationCard(notification: AppNotification, onClick: () -> Unit) {
    val config = when (notification.type) {
        "attendance" -> Triple(Icons.Default.EventAvailable, BluePrimary, "حضور وغياب")
        "message" -> Triple(Icons.AutoMirrored.Filled.SpeakerNotes, AmberPrimary, "رسالة جديدة")
        "news" -> Triple(Icons.Default.Campaign, MintPrimary, "إعلان هام")
        else -> Triple(Icons.Default.NotificationsActive, BlueDark, "تنبيه")
    }

    RawdatyCard(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        containerColor = if (notification.isRead) White.copy(0.7f) else White,
        elevation = if (notification.isRead) 0.dp else 4.dp,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(config.second.copy(0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(config.first, null, tint = config.second, modifier = Modifier.size(24.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        config.third,
                        style = MaterialTheme.typography.labelSmall,
                        color = config.second,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CairoFontFamily
                    )
                    Text(
                        notification.createdAt,
                        style = MaterialTheme.typography.labelSmall,
                        color = Gray400,
                        fontFamily = CairoFontFamily
                    )
                }

                Text(
                    notification.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (notification.isRead) Gray600 else BlueDark,
                    fontFamily = CairoFontFamily,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    notification.body,
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray500,
                    fontFamily = CairoFontFamily,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (!notification.isRead) {
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(ColorError)
                )
            }
        }
    }
}
