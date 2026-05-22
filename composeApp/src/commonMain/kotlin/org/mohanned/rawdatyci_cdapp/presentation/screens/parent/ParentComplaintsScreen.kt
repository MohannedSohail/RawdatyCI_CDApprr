package org.mohanned.rawdatyci_cdapp.presentation.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
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
import org.mohanned.rawdatyci_cdapp.domain.model.Complaint
import org.mohanned.rawdatyci_cdapp.domain.model.ComplaintStatus
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ComplaintsIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ComplaintsViewModel

object ParentComplaintsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ComplaintsViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.onIntent(ComplaintsIntent.Load)
        }

        Scaffold(
            containerColor = AppBg,
            topBar = {
                GlassHeader(
                    title = "الشكاوى والاقتراحات",
                    onBack = { navigator.pop() },
                    gradient = RawdatyGradients.Primary,
                    headerHeight = 120.dp
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navigator.push(ParentAddComplaintScreen) },
                    containerColor = BluePrimary,
                    contentColor = White,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "إضافة شكوى")
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                if (state.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BluePrimary)
                    }
                } else if (state.error != null) {
                    ErrorState(
                        message = state.error!!,
                        onRetry = { viewModel.onIntent(ComplaintsIntent.Load) }
                    )
                } else {
                    val complaints = state.complaints
                    if (complaints.isEmpty()) {
                        EmptyState(
                            title = "سجل الشكاوى فارغ",
                            subtitle = "يمكنك إرسال استفساراتك أو مقترحاتك للإدارة بسهولة عبر زر الإضافة.",
                            icon = Icons.Default.ChatBubbleOutline
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(complaints, key = { it.id }) { complaint ->
                                ParentComplaintItem(complaint)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ParentComplaintItem(complaint: Complaint) {
    RawdatyCard(
        modifier = Modifier.padding(horizontal = 20.dp),
        containerColor = White, 
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, 
                verticalAlignment = Alignment.CenterVertically, 
                modifier = Modifier.fillMaxWidth()
            ) {
                StatusBadge(status = complaint.status)
                Text(
                    complaint.createdAt, 
                    fontSize = 11.sp, 
                    color = Gray400, 
                    fontFamily = CairoFontFamily
                )
            }
            
            Text(
                text = complaint.content,
                style = MaterialTheme.typography.bodyMedium,
                color = Gray800,
                fontFamily = CairoFontFamily,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 22.sp
            )
            
            if (!complaint.reply.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(BlueLight.copy(alpha = 0.3f))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(Modifier.size(8.dp).clip(CircleShape).background(BluePrimary))
                            Text(
                                "رد الإدارة:", 
                                fontWeight = FontWeight.Bold, 
                                fontSize = 12.sp, 
                                color = BluePrimary, 
                                fontFamily = CairoFontFamily
                            )
                        }
                        Text(
                            complaint.reply!!, 
                            fontSize = 12.sp, 
                            color = Gray700, 
                            fontFamily = CairoFontFamily,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: ComplaintStatus) {
    val (label, color) = when (status) {
        ComplaintStatus.PENDING -> "قيد المراجعة" to AmberPrimary
        ComplaintStatus.IN_REVIEW -> "يتم الرد" to BluePrimary
        ComplaintStatus.RESOLVED -> "تم الرد" to MintPrimary
    }
    Surface(color = color.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
        Text(
            label, 
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), 
            color = color, 
            fontWeight = FontWeight.Bold, 
            fontSize = 10.sp,
            fontFamily = CairoFontFamily
        )
    }
}
