package org.mohanned.rawdatyci_cdapp.presentation.screens.teacher

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.domain.model.Complaint
import org.mohanned.rawdatyci_cdapp.domain.model.ComplaintStatus
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.*

object TeacherComplaintsScreen : Screen {
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
                ModernHeader(
                    title = "الشكاوى والاقتراحات",
                    subtitle = "طلبات واستفسارات أولياء الأمور",
                    onBack = { navigator.pop() },
                    gradient = RawdatyGradients.HeroBlue,
                    headerHeight = 150.dp
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                if (state.isLoading) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        repeat(5) { ShimmerBox(Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(22.dp))) }
                    }
                } else if (state.complaints.isEmpty()) {
                    EmptyState(
                        icon = Icons.Outlined.Feedback,
                        title = "لا توجد شكاوى حالياً",
                        subtitle = "ستظهر هنا أي استفسارات أو مقترحات مرسلة من أولياء الأمور لفصلك."
                    )
                } else {
                    AnimateEntrance {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 10.dp, bottom = 30.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.complaints, key = { it.id }) { complaint ->
                                TeacherComplaintItem(complaint)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeacherComplaintItem(complaint: Complaint) {
    RawdatyCard(
        modifier = Modifier.padding(horizontal = 20.dp),
        containerColor = White,
        elevation = 3.dp,
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TeacherStatusBadge(status = complaint.status)
                Text(
                    complaint.createdAt,
                    fontSize = 11.sp,
                    color = Gray400,
                    fontFamily = CairoFontFamily
                )
            }

            // محتوى الشكوى
            Text(
                text = complaint.content,
                style = MaterialTheme.typography.bodyMedium,
                color = BlueDark,
                fontWeight = FontWeight.Medium,
                fontFamily = CairoFontFamily,
                lineHeight = 22.sp
            )

            HorizontalDivider(color = Gray50, thickness = 1.dp)

            // معلومات ولي الأمر
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(BlueLight.copy(0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, tint = BluePrimary, modifier = Modifier.size(18.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        text = complaint.parentName ?: "ولي الأمر",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Gray700,
                        fontFamily = CairoFontFamily
                    )
                    Text(
                        text = if (complaint.type == "complaint") "شكوى مرسلة" else "مقترح مقدم",
                        style = MaterialTheme.typography.labelSmall,
                        color = Gray500,
                        fontFamily = CairoFontFamily
                    )
                }
                
                // زر التفاعل (اختياري للمعلمة)
                IconButton(
                    onClick = { /* Navigate to chat or reply */ },
                    modifier = Modifier.size(32.dp).background(Gray50, CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = Gray400)
                }
            }
        }
    }
}

@Composable
private fun TeacherStatusBadge(status: ComplaintStatus) {
    val (label, color) = when (status) {
        ComplaintStatus.PENDING -> "قيد المراجعة" to AmberPrimary
        ComplaintStatus.IN_REVIEW -> "جاري الرد" to BluePrimary
        ComplaintStatus.RESOLVED -> "تمت المعالجة" to MintPrimary
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
