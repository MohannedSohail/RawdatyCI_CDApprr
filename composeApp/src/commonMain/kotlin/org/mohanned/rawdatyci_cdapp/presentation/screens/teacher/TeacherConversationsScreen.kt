package org.mohanned.rawdatyci_cdapp.presentation.screens.teacher

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mohanned.rawdatyci_cdapp.domain.model.Conversation
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

@Composable
fun TeacherConversationsScreen(
    conversations: List<Conversation>,
    query: String,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    canLoadMore: Boolean,
    onSearch: (String) -> Unit,
    onLoadMore: () -> Unit,
    onConversationClick: (Conversation) -> Unit,
    onBack: () -> Unit,
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
            // Modern Search Bar Section
            Surface(
                color = White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = onSearch,
                        placeholder = { Text("ابحث في المحادثات...", fontFamily = CairoFontFamily, color = Gray400) },
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
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), thickness = 0.5.dp, color = Gray100.copy(0.5f))
                    }
                    
                    if (canLoadMore) {
                        item {
                            LaunchedEffect(Unit) { onLoadMore() }
                            Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = BluePrimary)
                            }
                        }
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
        // Premium Avatar with Activity Status
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
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
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

        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
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

@Preview
@Composable
fun TeacherConversationsPreview() {
    RawdatyTheme {
        val dummyConversations = listOf(
            Conversation(
                id = 1,
                participantId = 101,
                participantName = "محمد العلي",
                participantAvatar = null,
                childName = "أحمد العلي",
                lastMessage = "مرحباً أ. سارة، كيف حال أحمد اليوم؟",
                lastMessageAt = "09:00 ص",
                unreadCount = 2,
                isOnline = true
            ),
            Conversation(
                id = 2,
                participantId = 102,
                participantName = "سارة العلي",
                participantAvatar = null,
                childName = "ليان العلي",
                lastMessage = "شكراً جزيلاً على مجهوداتكم.",
                lastMessageAt = "أمس",
                unreadCount = 0,
                isOnline = false
            ),
            Conversation(
                id = 3,
                participantId = 103,
                participantName = "خالد فهد",
                participantAvatar = null,
                childName = "فهد خالد",
                lastMessage = "هل يوجد واجب منزلي اليوم؟",
                lastMessageAt = "السبت",
                unreadCount = 0,
                isOnline = true
            )
        )
        TeacherConversationsScreen(
            conversations = dummyConversations,
            query = "",
            isLoading = false,
            isLoadingMore = false,
            canLoadMore = false,
            onSearch = {},
            onLoadMore = {},
            onConversationClick = {},
            onBack = {}
        )
    }
}
