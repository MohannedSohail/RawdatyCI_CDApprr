package org.mohanned.rawdatyci_cdapp.presentation.screens.admin

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.domain.model.ComplaintStatus
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ComplaintsIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ComplaintsViewModel

data class AdminComplaintDetailScreen(val complaintId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ComplaintsViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(complaintId) {
            viewModel.onIntent(ComplaintsIntent.LoadComplaintDetail(complaintId))
        }

        Scaffold(
            containerColor = AppBg,
            topBar = {
                GlassHeader(
                    title = "تفاصيل الشكوى",
                    onBack = { navigator.pop() },
                    gradient = RawdatyGradients.AdminHeader,
                    headerHeight = 140.dp
                )
            }
        ) { padding ->
            val complaint = state.selectedComplaint
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BluePrimary)
                }
            } else if (complaint == null) {
                EmptyState(title = "الشكوى غير موجودة", icon = Icons.Default.Error)
            } else {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    RawdatyCard(containerColor = White, elevation = 2.dp) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                RawdatyAvatar(complaint.parentName, size = 56.dp, gradient = RawdatyGradients.AvatarBlue)
                                Column {
                                    Text(complaint.parentName, fontWeight = FontWeight.Bold, color = Gray900, fontFamily = CairoFontFamily)
                                    Text(complaint.createdAt, style = MaterialTheme.typography.labelSmall, color = Gray400, fontFamily = CairoFontFamily)
                                }
                                Spacer(Modifier.weight(1f))
                                StatusBadge(status = complaint.status)
                            }
                            Divider(color = Gray100)
                            Text(complaint.content, style = MaterialTheme.typography.bodyLarge, color = Gray700, lineHeight = 26.sp, fontFamily = CairoFontFamily)
                        }
                    }

                    if (complaint.reply != null) {
                        Text("رد الإدارة", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
                        RawdatyCard(containerColor = BlueLight.copy(0.2f), elevation = 0.dp) {
                            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(Icons.Default.QuestionAnswer, null, tint = BluePrimary)
                                Text(complaint.reply ?: "", style = MaterialTheme.typography.bodyMedium, color = Gray800, fontFamily = CairoFontFamily)
                            }
                        }
                    } else {
                        Text("الرد على الشكوى", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
                        OutlinedTextField(
                            value = state.replyText,
                            onValueChange = { viewModel.onIntent(ComplaintsIntent.ReplyTextChanged(it)) },
                            placeholder = { Text("اكتب رد الإدارة هنا...", fontFamily = CairoFontFamily) },
                            modifier = Modifier.fillMaxWidth().height(150.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BluePrimary, unfocusedBorderColor = Gray200)
                        )
                        RawdatyButton(
                            text = "إرسال الرد",
                            onClick = { viewModel.onIntent(ComplaintsIntent.SubmitReply) },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            isLoading = state.isActionLoading
                        )
                    }
                }
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
        Text(label, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = fg, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
    }
}
