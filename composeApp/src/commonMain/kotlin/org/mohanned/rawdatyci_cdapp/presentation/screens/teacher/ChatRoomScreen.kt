package org.mohanned.rawdatyci_cdapp.presentation.screens.teacher

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import org.mohanned.rawdatyci_cdapp.domain.model.Message
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

@Composable
fun ChatRoomScreen(
    recipientName: String,
    childName: String?,
    messages: List<Message>,
    currentUserId: Int,
    messageText: String,
    isSending: Boolean,
    isOnline: Boolean,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    canLoadMore: Boolean,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onLoadMore: () -> Unit,
    onBack: () -> Unit,
) {
    val listState = rememberLazyListState()

    Scaffold(
        containerColor = AppBg,
        topBar = {
            Surface(
                color = White,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.statusBarsPadding().padding(8.dp, 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = BlueDark)
                    }
                    Spacer(Modifier.width(8.dp))
                    Box {
                        RawdatyAvatar(
                            name = recipientName,
                            size = 44.dp,
                            gradient = RawdatyGradients.AvatarBlue
                        )
                        if (isOnline) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(White)
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(ColorSuccess)
                                    .align(Alignment.BottomEnd)
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            recipientName,
                            style = MaterialTheme.typography.titleMedium,
                            color = BlueDark,
                            fontWeight = FontWeight.Bold,
                            fontFamily = CairoFontFamily
                        )
                        if (childName != null) {
                            Text(
                                "ولي أمر: $childName",
                                style = MaterialTheme.typography.labelSmall,
                                color = Gray500,
                                fontFamily = CairoFontFamily
                            )
                        } else {
                            Text(
                                if (isOnline) "متصل الآن" else "غير متصل",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isOnline) ColorSuccess else Gray400,
                                fontFamily = CairoFontFamily
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = White,
                shadowElevation = 12.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = { /* Attachment */ },
                        modifier = Modifier.size(40.dp).background(Gray50, CircleShape)
                    ) {
                        Icon(Icons.Default.Add, null, tint = BluePrimary)
                    }

                    OutlinedTextField(
                        value = messageText,
                        onValueChange = onTextChange,
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                "اكتب رسالتك هنا...",
                                fontFamily = CairoFontFamily,
                                color = Gray400
                            )
                        },
                        shape = RoundedCornerShape(24.dp),
                        textStyle = LocalTextStyle.current.copy(fontFamily = CairoFontFamily),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Gray50,
                            unfocusedContainerColor = Gray50,
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Gray200
                        ),
                        maxLines = 4
                    )

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (messageText.isBlank() && !isSending) BluePrimary.copy(
                                    0.4f
                                ) else BluePrimary
                            )
                            .clickable(
                                enabled = messageText.isNotBlank() && !isSending,
                                onClick = onSend
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSending) {
                            CircularProgressIndicator(
                                color = White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                null,
                                tint = White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (isLoading && messages.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BluePrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                state = listState
            ) {
                items(messages, key = { it.id }) { msg ->
                    val isMe = msg.senderId == currentUserId
                    ChatBubble(message = msg, isMe = isMe)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message, isMe: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (isMe) BluePrimary else White,
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = if (isMe) 20.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 20.dp
            ),
            shadowElevation = if (isMe) 2.dp else 1.dp,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text(
                    text = message.content,
                    color = if (isMe) White else Gray800,
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = CairoFontFamily
                )
                Row(
                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = message.sentAt,
                        color = if (isMe) White.copy(0.7f) else Gray400,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = CairoFontFamily
                    )
                    if (isMe) {
                        Icon(
                            Icons.Default.DoneAll,
                            null,
                            tint = White.copy(0.7f),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ChatRoomPreview() {
    RawdatyTheme {
        val dummyMessages = listOf(
            Message(
                1, 1, 2, "مرحباً أ. سارة، كيف حال أحمد اليوم؟", "09:00 ص", "",
                isRead = false,
                sentAt = "Mohanned"
            ),
            Message(
                2, 2, 1, "أهلاً بك، أحمد ممتاز اليوم وقد شارك بفعالية في حصة الرسم.", "09:05 ص", "",
                isRead = true,
                sentAt = "Mohanned"
            ),
            Message(
                3, 1, 2, "شكراً جزيلاً لكِ على اهتمامك.", "09:10 ص", "",
                isRead = false,
                sentAt = "Mohanned"
            ),
            Message(
                4, 2, 1, "لا شكر على واجب، نحن هنا من أجلهم.", "09:12 ص", "",
                isRead = true,
                sentAt = "Mohanned"
            )
        )
        ChatRoomScreen(
            recipientName = "محمد العلي",
            childName = "أحمد العلي",
            messages = dummyMessages,
            currentUserId = 2,
            messageText = "",
            isSending = false,
            isOnline = true,
            isLoading = false,
            isLoadingMore = false,
            canLoadMore = false,
            onTextChange = {},
            onSend = {},
            onLoadMore = {},
            onBack = {}
        )
    }
}
