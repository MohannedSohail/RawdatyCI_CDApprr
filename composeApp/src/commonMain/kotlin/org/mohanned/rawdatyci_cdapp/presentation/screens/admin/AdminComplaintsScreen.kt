package org.mohanned.rawdatyci_cdapp.presentation.screens.admin

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import org.mohanned.rawdatyci_cdapp.domain.model.Complaint
import org.mohanned.rawdatyci_cdapp.domain.model.ComplaintStatus
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

@Composable
@Preview
fun AdminComplaintsScreen(
    complaints: List<Complaint>,
    selectedTab: Int,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    canLoadMore: Boolean,
    replyText: String,
    showReplyDialog: Boolean,
    selectedComplaint: Complaint?,
    onTabChange: (Int) -> Unit,
    onLoadMore: () -> Unit,
    onReplyTextChange: (String) -> Unit,
    onOpenReply: (Complaint) -> Unit,
    onSubmitReply: () -> Unit,
    onDismissReply: () -> Unit,
    onBack: () -> Unit,
) {
    val tabs = listOf("قيد الانتظار", "تم الحل")
    val listState = rememberLazyListState()

    // Infinite Scrolling
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && lastVisibleIndex >= complaints.size - 2 && canLoadMore && !isLoadingMore && !isLoading) {
                    onLoadMore()
                }
            }
    }

    // Modern Reply Dialog
    if (showReplyDialog && selectedComplaint != null) {
        AlertDialog(
            onDismissRequest = onDismissReply,
            title = { 
                Text(
                    "الرد على الشكوى", 
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = BlueDark,
                    fontFamily = CairoFontFamily
                ) 
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Surface(
                        color = Gray50,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "المشتكي: ${selectedComplaint.parentName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = BluePrimary,
                                fontWeight = FontWeight.Bold,
                                fontFamily = CairoFontFamily
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                selectedComplaint.body,
                                style = MaterialTheme.typography.bodySmall,
                                color = Gray600,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                fontFamily = CairoFontFamily
                            )
                        }
                    }
                    
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = onReplyTextChange,
                        placeholder = { Text("اكتب ردك هنا...", fontFamily = CairoFontFamily, color = Gray400) },
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        shape = RoundedCornerShape(16.dp),
                        textStyle = LocalTextStyle.current.copy(fontFamily = CairoFontFamily),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Gray200
                        )
                    )
                }
            },
            confirmButton = {
                RawdatyButton(
                    text = "إرسال الرد",
                    onClick = onSubmitReply,
                    backgroundColor = MintPrimary,
                    enabled = replyText.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            dismissButton = {
                TextButton(onClick = onDismissReply, modifier = Modifier.fillMaxWidth()) {
                    Text("إلغاء", color = Gray500, fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = White
        )
    }

    Scaffold(
        containerColor = AppBg,
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
            // Premium Segmented Control
            Surface(
                color = White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Gray100)
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
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                tab,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) BluePrimary else Gray500,
                                fontFamily = CairoFontFamily
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(6) { NotificationItemShimmer() }
                }
            } else if (complaints.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.Inbox,
                    title = "الصندوق الآن فارغ",
                    subtitle = "لا توجد شكاوى أو مقترحات جديدة حالياً في هذا القسم"
                )
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(complaints, key = { it.id }) { complaint ->
                        ComplaintCard(complaint = complaint, onReply = { onOpenReply(complaint) })
                    }

                    if (isLoadingMore) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = BluePrimary, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                    
                    item { Spacer(Modifier.height(40.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ComplaintCard(complaint: Complaint, onReply: () -> Unit) {
    RawdatyCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RawdatyAvatar(complaint.parentName, size = 48.dp, gradient = RawdatyGradients.AvatarBlue)
                
                Column(Modifier.weight(1f)) {
                    Text(
                        complaint.parentName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Gray900,
                        fontFamily = CairoFontFamily
                    )
                    Text(
                        complaint.createdAt,
                        style = MaterialTheme.typography.labelSmall,
                        color = Gray400,
                        fontFamily = CairoFontFamily
                    )
                }

                // Status Badge
                StatusBadge(status = complaint.status)
            }

            // Content
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    complaint.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = BluePrimary,
                    fontFamily = CairoFontFamily
                )
                Text(
                    complaint.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray600,
                    lineHeight = 22.sp,
                    fontFamily = CairoFontFamily
                )
            }

            // Admin Reply Section
            if (complaint.adminReply != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = BlueLight.copy(0.3f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.QuestionAnswer, null, tint = BluePrimary, modifier = Modifier.size(18.dp))
                        Column {
                            Text(
                                "رد الإدارة:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = BluePrimary,
                                fontFamily = CairoFontFamily
                            )
                            Text(
                                complaint.adminReply,
                                style = MaterialTheme.typography.bodySmall,
                                color = Gray700,
                                fontFamily = CairoFontFamily
                            )
                        }
                    }
                }
            }

            // Reply Action Button
            if (complaint.status != ComplaintStatus.RESOLVED) {
                RawdatyButton(
                    text = "الرد الفوري",
                    onClick = onReply,
                    backgroundColor = BluePrimary.copy(0.08f),
                    icon = Icons.AutoMirrored.Filled.Reply,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    useSmallText = true
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
    
    Surface(
        color = bg,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            label, 
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall, 
            color = fg,
            fontWeight = FontWeight.Bold,
            fontFamily = CairoFontFamily
        )
    }
}

@Preview
@Composable
fun AdminComplaintsPreview() {
    RawdatyTheme {
        AdminComplaintsScreen(
            complaints = emptyList(),
            selectedTab = 0,
            isLoading = false,
            isLoadingMore = false,
            canLoadMore = true,
            replyText = "",
            showReplyDialog = false,
            selectedComplaint = null,
            onTabChange = {},
            onLoadMore = {},
            onReplyTextChange = {},
            onOpenReply = {},
            onSubmitReply = {},
            onDismissReply = {},
            onBack = {}
        )
    }
}
