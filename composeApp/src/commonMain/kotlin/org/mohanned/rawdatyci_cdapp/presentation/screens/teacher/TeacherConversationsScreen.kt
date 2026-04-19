package org.mohanned.rawdatyci_cdapp.presentation.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.domain.model.Conversation
import org.mohanned.rawdatyci_cdapp.presentation.components.EmptyState
import org.mohanned.rawdatyci_cdapp.presentation.components.GlassHeader
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyAvatar
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ChatIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ChatViewModel

object TeacherConversationsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ChatViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.onIntent(ChatIntent.LoadConversations)
        }

        TeacherConversationsScreenContent(
            conversations = state.conversations,
            isLoading = state.isLoading,
            onConversationClick = { conversation ->
                navigator.push(ChatRoomScreen(conversation.id, conversation.participantName, conversation.childName ?: ""))
            },
            onBack = { navigator.pop() }
        )
    }
}

@Composable
fun TeacherConversationsScreenContent(
    conversations: List<Conversation>,
    isLoading: Boolean,
    onConversationClick: (Conversation) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = AppBg,
        topBar = {
            GlassHeader(
                title = "رسائل المعلمة",
                onBack = onBack,
                gradient = RawdatyGradients.HeroBlue,
                headerHeight = 140.dp
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading && conversations.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BluePrimary)
                }
            } else if (conversations.isEmpty()) {
                EmptyState(
                    icon = Icons.Outlined.ChatBubbleOutline,
                    title = "لا توجد رسائل حالياً",
                    subtitle = "ابدأ بالتواصل مع أولياء الأمور لمتابعة حالة الطلاب."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(conversations, key = { it.id }) { conversation ->
                        ConversationItem(conversation) { onConversationClick(conversation) }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            thickness = 0.5.dp,
                            color = Gray100.copy(0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationItem(conversation: Conversation, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            RawdatyAvatar(
                name = conversation.participantName,
                size = 56.dp,
                gradient = if (conversation.unreadCount > 0) RawdatyGradients.AvatarBlue else RawdatyGradients.AvatarMint
            )

            if (conversation.isOnline) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(White)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(ColorSuccess)
                        .align(Alignment.BottomEnd)
                )
            }
        }

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    conversation.participantName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Gray900,
                    fontFamily = CairoFontFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (conversation.childName != null) {
                    Surface(
                        color = BlueLight.copy(0.4f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            conversation.childName,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = BluePrimary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = CairoFontFamily
                        )
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                conversation.lastMessage ?: "لا توجد رسائل سابقة",
                style = MaterialTheme.typography.bodySmall,
                color = if (conversation.unreadCount > 0) BluePrimary else Gray500,
                fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = CairoFontFamily
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                conversation.lastMessageAt ?: "",
                style = MaterialTheme.typography.labelSmall,
                color = Gray400,
                fontFamily = CairoFontFamily
            )
            if (conversation.unreadCount > 0) {
                Surface(
                    color = BluePrimary,
                    shape = CircleShape,
                ) {
                    Text(
                        "${conversation.unreadCount}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = White,
                        fontWeight = FontWeight.Black,
                        fontFamily = CairoFontFamily
                    )
                }
            }
        }
    }
}
