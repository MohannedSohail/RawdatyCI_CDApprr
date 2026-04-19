package org.mohanned.rawdatyci_cdapp.presentation.screens.admin

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.domain.model.Complaint
import org.mohanned.rawdatyci_cdapp.domain.model.ComplaintStatus
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ComplaintsIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ComplaintsState
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ComplaintsViewModel

object AdminComplaintsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ComplaintsViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            viewModel.onIntent(ComplaintsIntent.Load)
        }

        AdminComplaintsScreenContent(
            state = state,
            snackbarHostState = snackbarHostState,
            onIntent = viewModel::onIntent,
            onBack = { navigator.pop() }
        )
    }
}

@Composable
fun AdminComplaintsScreenContent(
    state: ComplaintsState,
    snackbarHostState: SnackbarHostState,
    onIntent: (ComplaintsIntent) -> Unit,
    onBack: () -> Unit,
) {
    val listState = rememberLazyListState()

    if (state.showReplyDialog && state.selectedComplaint != null) {
        AlertDialog(
            onDismissRequest = { if (!state.isActionLoading && !state.isSuccessInDialog) onIntent(ComplaintsIntent.DismissReply) },
            title = { 
                Text(
                    if (state.isSuccessInDialog) "تم الرد" else "الرد على الشكوى", 
                    style = MaterialTheme.typography.titleLarge, 
                    fontWeight = FontWeight.Bold, 
                    color = BlueDark, 
                    fontFamily = CairoFontFamily 
                ) 
            },
            text = {
                Box(modifier = Modifier.fillMaxWidth().animateContentSize(), contentAlignment = Alignment.Center) {
                    when {
                        state.isSuccessInDialog -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                                Icon(Icons.Default.CheckCircle, null, tint = ColorSuccess, modifier = Modifier.size(64.dp))
                                Spacer(Modifier.height(16.dp))
                                Text("تم الرد بنجاح!", style = MaterialTheme.typography.titleMedium, color = Gray900, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
                            }
                        }
                        state.isActionLoading -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                                CircularProgressIndicator(color = BluePrimary, modifier = Modifier.size(48.dp))
                                Spacer(Modifier.height(16.dp))
                                Text("جاري إرسال الرد...", style = MaterialTheme.typography.bodyMedium, color = Gray600, fontFamily = CairoFontFamily)
                            }
                        }
                        else -> {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                Surface(color = Gray50, shape = RoundedCornerShape(12.dp)) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("المشتكي: ${state.selectedComplaint.parentName}", style = MaterialTheme.typography.labelSmall, color = BluePrimary, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
                                        Spacer(Modifier.height(4.dp))
                                        Text(state.selectedComplaint.content, style = MaterialTheme.typography.bodySmall, color = Gray600, maxLines = 3, overflow = TextOverflow.Ellipsis, fontFamily = CairoFontFamily)
                                    }
                                }
                                OutlinedTextField(
                                    value = state.replyText,
                                    onValueChange = { onIntent(ComplaintsIntent.ReplyTextChanged(it)) },
                                    placeholder = { Text("اكتب ردك هنا...", fontFamily = CairoFontFamily, color = Gray400) },
                                    modifier = Modifier.fillMaxWidth().height(140.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    textStyle = LocalTextStyle.current.copy(fontFamily = CairoFontFamily, color = Color.Black),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BluePrimary, 
                                        unfocusedBorderColor = Gray200,
                                        focusedTextColor = Color.Black,
                                        unfocusedTextColor = Color.Black
                                    )
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (!state.isSuccessInDialog && !state.isActionLoading) {
                    RawdatyButton(
                        text = "إرسال الرد",
                        onClick = { onIntent(ComplaintsIntent.SubmitReply) },
                        backgroundColor = MintPrimary,
                        enabled = state.replyText.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            dismissButton = {
                if (!state.isActionLoading && !state.isSuccessInDialog) {
                    TextButton(onClick = { onIntent(ComplaintsIntent.DismissReply) }, modifier = Modifier.fillMaxWidth()) {
                        Text("إلغاء", color = Gray500, fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold)
                    }
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = White
        )
    }

    Scaffold(
        containerColor = AppBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { 
            GlassHeader(
                title = "الشكاوى والمقترحات", 
                onBack = onBack,
                gradient = RawdatyGradients.AdminHeader,
                headerHeight = 140.dp
            ) 
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (state.isLoading && state.complaints.isEmpty()) {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(6) { ShimmerBox(Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(16.dp))) }
                }
            } else if (state.complaints.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.Inbox,
                    title = if (state.error != null) "خطأ في التحميل" else "الصندوق فارغ",
                    subtitle = state.error ?: "لا توجد شكاوى حالياً في هذا القسم"
                )
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.complaints, key = { it.id }) { complaint ->
                        ComplaintCard(complaint = complaint, onReply = { onIntent(ComplaintsIntent.OpenReply(complaint)) })
                    }
                    item { Spacer(Modifier.height(40.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ComplaintCard(complaint: Complaint, onReply: () -> Unit) {
    RawdatyCard(modifier = Modifier.fillMaxWidth(), elevation = 2.dp, containerColor = White) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                RawdatyAvatar(complaint.parentName, size = 48.dp, gradient = RawdatyGradients.AvatarBlue)
                Column(Modifier.weight(1f)) {
                    Text(complaint.parentName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Black, fontFamily = CairoFontFamily)
                    Text(complaint.createdAt, style = MaterialTheme.typography.labelSmall, color = Gray400, fontFamily = CairoFontFamily)
                }
                StatusBadge(status = complaint.status)
            }
            Text(complaint.content, style = MaterialTheme.typography.bodyMedium, color = Gray700, lineHeight = 22.sp, fontFamily = CairoFontFamily)
            if (complaint.reply != null) {
                Surface(modifier = Modifier.fillMaxWidth(), color = BlueLight.copy(0.3f), shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.QuestionAnswer, null, tint = BluePrimary, modifier = Modifier.size(18.dp))
                        Column {
                            Text("رد الإدارة:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = BluePrimary, fontFamily = CairoFontFamily)
                            Text(complaint.reply ?: "", style = MaterialTheme.typography.bodySmall, color = Gray800, fontFamily = CairoFontFamily)
                        }
                    }
                }
            }
            if (complaint.status != ComplaintStatus.RESOLVED) {
                RawdatyButton(
                    text = "الرد الفوري",
                    onClick = onReply,
                    backgroundColor = BluePrimary,
                    textColor = White,
                    icon = Icons.AutoMirrored.Filled.Reply,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    isPill = true
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(status: ComplaintStatus) {
    val (bg, fg, label) = when (status) {
        ComplaintStatus.PENDING -> Triple(AmberLight.copy(0.5f), AmberPrimary, "انتظار")
        ComplaintStatus.RESOLVED -> Triple(MintLight.copy(0.5f), MintPrimary, "تم الحل")
        ComplaintStatus.IN_REVIEW -> Triple(BlueLight.copy(0.5f), BluePrimary, "مراجعة")
    }
    Surface(color = bg, shape = RoundedCornerShape(8.dp)) {
        Text(label, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = fg, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
    }
}
