package org.mohanned.rawdatyci_cdapp.presentation.screens.teacher

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarOutline
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
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ChildrenIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ChildrenViewModel

data class TeacherStudentDetailScreen(val childId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ChildrenViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(childId) {
            viewModel.onIntent(ChildrenIntent.LoadChildDetail(childId))
        }

        Scaffold(
            containerColor = AppBg,
            topBar = {
                GlassHeader(
                    title = "تفاصيل الطالب",
                    onBack = { navigator.pop() },
                    gradient = RawdatyGradients.Primary,
                    headerHeight = 120.dp
                )
            }
        ) { padding ->
            val child = state.currentChild
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BluePrimary)
                }
            } else if (child == null) {
                EmptyState(title = "الطالب غير موجود", icon = Icons.Default.Error)
            } else {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    RawdatyCard(containerColor = White, elevation = 2.dp) {
                        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            RawdatyAvatar(child.fullName, size = 100.dp, gradient = RawdatyGradients.AvatarAmber)
                            Text(child.fullName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
                            Text("ولي الأمر: ${child.parentName ?: "غير محدد"}", color = Gray500, fontFamily = CairoFontFamily)
                        }
                    }

                    SectionHeader("التقييم الأسبوعي")
                    RawdatyCard(containerColor = White) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                                repeat(5) { i ->
                                    val isSelected = i < child.stars
                                    Icon(
                                        if (isSelected) Icons.Default.Star else Icons.Outlined.StarOutline,
                                        null,
                                        tint = if (isSelected) AmberPrimary else Gray200,
                                        modifier = Modifier.size(40.dp).clickable {
                                            viewModel.onIntent(ChildrenIntent.RateChild(child.id, i + 1, child.notes))
                                        }
                                    )
                                }
                            }
                            OutlinedTextField(
                                value = child.notes ?: "",
                                onValueChange = { viewModel.onIntent(ChildrenIntent.RateChild(child.id, child.stars, it)) },
                                modifier = Modifier.fillMaxWidth().height(100.dp),
                                placeholder = { Text("ملاحظات المعلمة حول أداء الطالب...", fontFamily = CairoFontFamily) },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    RawdatyButton(
                        text = "مراسلة ولي الأمر",
                        onClick = { 
                            navigator.push(ChatRoomScreen(child.parentId ?: "", child.parentName ?: "", child.fullName)) 
                        },
                        icon = Icons.AutoMirrored.Filled.Chat,
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    )
                }
            }
        }
    }
}
