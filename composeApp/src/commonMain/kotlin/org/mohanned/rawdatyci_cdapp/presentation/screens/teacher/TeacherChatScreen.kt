package org.mohanned.rawdatyci_cdapp.presentation.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyAvatar
import org.mohanned.rawdatyci_cdapp.presentation.theme.AppBg
import org.mohanned.rawdatyci_cdapp.presentation.theme.BlueDark
import org.mohanned.rawdatyci_cdapp.presentation.theme.BluePrimary
import org.mohanned.rawdatyci_cdapp.presentation.theme.CairoFontFamily
import org.mohanned.rawdatyci_cdapp.presentation.theme.ColorSuccess
import org.mohanned.rawdatyci_cdapp.presentation.theme.Gray200
import org.mohanned.rawdatyci_cdapp.presentation.theme.Gray400
import org.mohanned.rawdatyci_cdapp.presentation.theme.Gray50
import org.mohanned.rawdatyci_cdapp.presentation.theme.Gray800
import org.mohanned.rawdatyci_cdapp.presentation.theme.RawdatyGradients
import org.mohanned.rawdatyci_cdapp.presentation.theme.RawdatyTheme
import org.mohanned.rawdatyci_cdapp.presentation.theme.White

data class Message(
    val id: String,
    val text: String,
    val senderId: String,
    val timestamp: Long,
    val isFromMe: Boolean
)

@Composable
fun TeacherChatScreen(
    parentName: String,
    messages: List<Message>,
    onSendMessage: (String) -> Unit,
    onBack: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }

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
                    RawdatyAvatar(
                        name = parentName,
                        size = 44.dp,
                        gradient = RawdatyGradients.AvatarBlue
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            parentName,
                            style = MaterialTheme.typography.titleMedium,
                            color = BlueDark,
                            fontWeight = FontWeight.Bold,
                            fontFamily = CairoFontFamily
                        )
                        Text(
                            "متصل الآن",
                            style = MaterialTheme.typography.labelSmall,
                            color = ColorSuccess,
                            fontFamily = CairoFontFamily
                        )
                    }
                    IconButton(onClick = { /* Call */ }) {
                        Icon(Icons.Default.Phone, null, tint = BluePrimary)
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
                        onClick = { /* Attach */ },
                        modifier = Modifier.size(40.dp)
                            .background(Gray50, RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Outlined.AddCircleOutline, null, tint = BluePrimary)
                    }

                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
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
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (messageText.isBlank()) BluePrimary.copy(0.4f) else BluePrimary)
                            .clickable(enabled = messageText.isNotBlank()) {
                                onSendMessage(messageText)
                                messageText = ""
                            },
                        contentAlignment = Alignment.Center
                    ) {
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
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            reverseLayout = true
        ) {
            items(messages.reversed(), key = { it.id }) { message ->
                ModernMessageBubble(message)
            }
        }
    }
}

@Composable
fun ModernMessageBubble(message: Message) {
    val alignment = if (message.isFromMe) Alignment.CenterEnd else Alignment.CenterStart
    val color = if (message.isFromMe) BluePrimary else White
    val textColor = if (message.isFromMe) White else Gray800
    val shape = if (message.isFromMe) {
        RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
    } else {
        RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Surface(
            color = color,
            shape = shape,
            shadowElevation = if (message.isFromMe) 2.dp else 1.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text(
                    text = message.text,
                    color = textColor,
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = CairoFontFamily
                )
                Text(
                    text = "١٢:٣٠ م", // Placeholder for time
                    color = if (message.isFromMe) White.copy(0.7f) else Gray400,
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = CairoFontFamily,
                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun TeacherChatPreview() {
    RawdatyTheme {
        TeacherChatScreen(
            parentName = "أبو محمد",
            messages = listOf(
                Message("1", "السلام عليكم، هل غداً رحلة؟", "parent", 12345, false),
                Message("2", "وعليكم السلام، نعم غداً رحلة حديقة الحيوان.", "teacher", 12346, true),
            ),
            onSendMessage = {},
            onBack = {}
        )
    }
}
