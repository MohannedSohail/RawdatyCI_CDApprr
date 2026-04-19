package org.mohanned.rawdatyci_cdapp.presentation.screens.teacher

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Search
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
import org.mohanned.rawdatyci_cdapp.domain.model.Conversation
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ChatIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ChatViewModel

object ChatConversationsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ChatViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.onIntent(ChatIntent.LoadConversations)
        }

        Scaffold(
            containerColor = AppBg,
            topBar = {
                GlassHeader(
                    title = "المحادثات",
                    onBack = { navigator.pop() },
                    gradient = RawdatyGradients.Primary,
                    headerHeight = 140.dp
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                Surface(color = White, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = "",
                            onValueChange = { },
                            placeholder = { Text("بحث في المحادثات...", fontFamily = CairoFontFamily, color = Gray400) },
                            leadingIcon = { Icon(Icons.Default.Search, null, tint = BluePrimary) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BluePrimary, unfocusedBorderColor = Gray200)
                        )
                    }
                }

                if (state.isLoading) {
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(8) { ShimmerBox(Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(16.dp))) }
                    }
                } else if (state.conversations.isEmpty()) {
                    EmptyState(title = "لا توجد محادثات", subtitle = "ابدأ بالتواصل مع أولياء الأمور أو المعلمات", icon = Icons.Default.Chat)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.conversations) { conversation ->
                            ConversationItem(
                                conversation = conversation,
                                onClick = { 
                                    navigator.push(ChatRoomScreen(conversation.id, conversation.participantName, conversation.childName ?: ""))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatConversationItem(conversation: Conversation, onClick: () -> Unit) {
    RawdatyCard(onClick = onClick, containerColor = White, elevation = 1.dp) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box {
                RawdatyAvatar(conversation.participantName, size = 52.dp, gradient = RawdatyGradients.AvatarBlue)
                if (conversation.isOnline) {
                    Box(Modifier.size(12.dp).clip(CircleShape).background(ColorSuccess).border(2.dp, White, CircleShape).align(Alignment.BottomEnd))
                }
            }

            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(conversation.participantName, fontWeight = FontWeight.Bold, color = Gray900, fontFamily = CairoFontFamily)
                    Text(conversation.lastMessageAt ?: "", fontSize = 10.sp, color = Gray400, fontFamily = CairoFontFamily)
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        conversation.lastMessage ?: "لا توجد رسائل بعد",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (conversation.unreadCount > 0) BluePrimary else Gray500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = CairoFontFamily,
                        fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                    )
                    if (conversation.unreadCount > 0) {
                        Box(Modifier.size(18.dp).clip(CircleShape).background(BluePrimary), contentAlignment = Alignment.Center) {
                            Text(conversation.unreadCount.toString(), color = White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
