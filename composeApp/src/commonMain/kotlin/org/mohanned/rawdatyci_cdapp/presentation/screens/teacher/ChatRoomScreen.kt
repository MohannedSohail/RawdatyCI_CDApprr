package org.mohanned.rawdatyci_cdapp.presentation.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.Message
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyAvatar
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ChatIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ChatViewModel

data class ChatRoomScreen(
    val conversationId: String,
    val recipientName: String,
    val childName: String
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ChatViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(conversationId) {
            viewModel.onIntent(ChatIntent.LoadMessages(conversationId))
        }

        ChatRoomScreenContent(
            recipientName = recipientName,
            childName = childName,
            messages = state.messages,
            messageText = state.messageText,
            isLoading = state.isLoading,
            isSending = state.isSending,
            onMessageTextChange = { viewModel.onIntent(ChatIntent.MessageTextChanged(it)) },
            onSendMessage = { viewModel.onIntent(ChatIntent.SendMessage(conversationId)) },
            onBack = { navigator.pop() }
        )
    }
}

@Composable
fun ChatRoomScreenContent(
    recipientName: String,
    childName: String?,
    messages: List<Message>,
    messageText: String,
    isLoading: Boolean,
    isSending: Boolean,
    onMessageTextChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onBack: () -> Unit,
) {
    val listState = rememberLazyListState()

    Scaffold(
        containerColor = AppBg,
        topBar = {
            Surface(color = White, shadowElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.statusBarsPadding().padding(8.dp, 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = BlueDark) }
                    Spacer(Modifier.width(8.dp))
                    RawdatyAvatar(name = recipientName, size = 44.dp, gradient = RawdatyGradients.AvatarBlue)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(recipientName, style = MaterialTheme.typography.titleMedium, color = BlueDark, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
                        if (childName != null) {
                            Text("ولي أمر: $childName", style = MaterialTheme.typography.labelSmall, color = Gray500, fontFamily = CairoFontFamily)
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(color = White, shadowElevation = 12.dp, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp).fillMaxWidth().navigationBarsPadding().imePadding(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = onMessageTextChange,
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("اكتب رسالتك هنا...", fontFamily = CairoFontFamily, color = Gray400) },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Gray50, unfocusedContainerColor = Gray50, focusedBorderColor = BluePrimary, unfocusedBorderColor = Gray200),
                        maxLines = 4
                    )
                    IconButton(
                        onClick = onSendMessage,
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(if (messageText.isBlank()) BluePrimary.copy(0.4f) else BluePrimary),
                        enabled = messageText.isNotBlank() && !isSending
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, null, tint = White, modifier = Modifier.size(22.dp))
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading && messages.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = BluePrimary) }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    state = listState,
                    reverseLayout = true
                ) {
                    items(messages.reversed(), key = { it.id }) { msg ->
                        ChatBubble(message = msg, isMe = msg.senderId == "me" || msg.senderName == "me")
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message, isMe: Boolean) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = if (isMe) Alignment.End else Alignment.Start) {
        Surface(
            color = if (isMe) BluePrimary else White,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = if (isMe) 20.dp else 4.dp, bottomEnd = if (isMe) 4.dp else 20.dp),
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text(text = message.content, color = if (isMe) White else Gray800, fontFamily = CairoFontFamily)
                Row(modifier = Modifier.align(Alignment.End).padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = message.sentAt, color = if (isMe) White.copy(0.7f) else Gray400, style = MaterialTheme.typography.labelSmall, fontFamily = CairoFontFamily)
                    if (isMe) Icon(Icons.Default.DoneAll, null, tint = if (message.isRead) ColorSuccess else White.copy(0.7f), modifier = Modifier.size(12.dp))
                }
            }
        }
    }
}
