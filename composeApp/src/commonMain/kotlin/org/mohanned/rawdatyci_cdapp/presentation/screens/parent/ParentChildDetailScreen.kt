package org.mohanned.rawdatyci_cdapp.presentation.screens.parent

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarMonth
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
import org.mohanned.rawdatyci_cdapp.presentation.screens.teacher.ChatRoomScreen
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AttendanceIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AttendanceViewModel
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ChildrenIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ChildrenViewModel

data class ParentChildDetailScreen(val childId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val childViewModel: ChildrenViewModel = koinViewModel()
        val attendanceViewModel: AttendanceViewModel = koinViewModel()
        
        val childState by childViewModel.state.collectAsState()
        val attendanceState by attendanceViewModel.state.collectAsState()

        LaunchedEffect(childId) {
            childViewModel.onIntent(ChildrenIntent.LoadChildDetail(childId))
            attendanceViewModel.onIntent(AttendanceIntent.LoadChildAttendance(childId))
        }

        val child = childState.currentChild
        
        if (childState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BluePrimary)
            }
        } else if (child == null) {
            EmptyState(title = "الطفل غير موجود", icon = Icons.Default.Error, actionText = "رجوع", onAction = { navigator.pop() })
        } else {
            ParentChildDetailScreenContent(
                child = child,
                attendanceRate = (attendanceState.attendanceRate * 100).toInt(),
                onBack = { navigator.pop() },
                onAttendanceClick = { /* Navigate to full history if needed */ },
                onMessageTeacher = { 
                    navigator.push(ChatRoomScreen(child.classId, child.className, child.fullName)) 
                }
            )
        }
    }
}

@Composable
fun ParentChildDetailScreenContent(
    child: Child,
    attendanceRate: Int,
    onBack: () -> Unit,
    onAttendanceClick: () -> Unit,
    onMessageTeacher: () -> Unit,
) {
    var activeTab by remember { mutableStateOf(0) }
    val tabs = listOf("النمو والتعلم", "سجل الحضور", "طاقم الفصل")

    Scaffold(
        containerColor = AppBg,
        topBar = {
            GlassHeader(
                title = "ملف طفلي",
                onBack = onBack,
                gradient = RawdatyGradients.HeroBlue,
                headerHeight = 140.dp
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Surface(color = White, shadowElevation = 0.dp, modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(top = 24.dp, bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box {
                        RawdatyAvatar(child.fullName, size = 100.dp, gradient = if (child.gender == "male") RawdatyGradients.AvatarBlue else RawdatyGradients.AvatarMint)
                        Surface(
                            modifier = Modifier.size(32.dp).align(Alignment.BottomEnd),
                            color = MintPrimary, shape = CircleShape, border = BorderStroke(3.dp, White)
                        ) {
                            Icon(Icons.Default.Verified, null, tint = White, modifier = Modifier.padding(6.dp))
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(child.fullName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = BlueDark, fontFamily = CairoFontFamily)
                        Surface(color = BlueLight.copy(0.4f), shape = RoundedCornerShape(8.dp)) {
                            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.School, null, tint = BluePrimary, modifier = Modifier.size(16.dp))
                                Text(child.className, style = MaterialTheme.typography.labelMedium, color = BluePrimary, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp).fillMaxWidth(),
                        color = Gray100, shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(modifier = Modifier.padding(4.dp)) {
                            tabs.forEachIndexed { i, tab ->
                                val isSelected = activeTab == i
                                Box(
                                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(if (isSelected) White else Color.Transparent)
                                        .clickable { activeTab = i }.padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(tab, style = MaterialTheme.typography.labelMedium, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = if (isSelected) BluePrimary else Gray500, fontFamily = CairoFontFamily)
                                }
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                AnimatedContent(targetState = activeTab, label = "tab_content") { targetTab ->
                    when (targetTab) {
                        0 -> GrowthAndLearningTab(child)
                        1 -> AttendanceTab(attendanceRate, onAttendanceClick)
                        2 -> TeacherTab(onMessageTeacher)
                    }
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun GrowthAndLearningTab(child: Child) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        SectionHeader("المهارات المكتسبة")
        RawdatyCard(elevation = 1.dp, containerColor = White) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                ProgressItem("اللغة العربية والقرآن", 0.92f, BluePrimary)
                ProgressItem("الذكاء المنطقي والرياضي", 0.78f, BlueDark)
                ProgressItem("المهارات الاجتماعية", 0.95f, MintPrimary)
                ProgressItem("السلوك والمشاركة", 1.00f, AmberPrimary)
            }
        }

        SectionHeader("لوحة التميز")
        RawdatyCard(elevation = 1.dp, containerColor = White) {
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("عدد النجوم", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = BlueDark, fontFamily = CairoFontFamily)
                    Text("تقييم المعلمة لهذا الأسبوع", style = MaterialTheme.typography.labelSmall, color = Gray500, fontFamily = CairoFontFamily)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(5) { i ->
                        Icon(if (i < child.stars) Icons.Default.Stars else Icons.Outlined.StarOutline, null, tint = if (i < child.stars) AmberPrimary else Gray200, modifier = Modifier.size(32.dp))
                    }
                }
            }
        }

        if (!child.notes.isNullOrBlank()) {
            SectionHeader("كلمة المعلمة")
            RawdatyCard(containerColor = White) {
                Text(child.notes ?: "", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium, color = Gray700, lineHeight = 24.sp, fontFamily = CairoFontFamily)
            }
        }
    }
}

@Composable
private fun AttendanceTab(attendanceRate: Int, onAttendanceClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        SectionHeader("ملخص الحضور")
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(label = "نسبة الحضور", value = "$attendanceRate%", icon = Icons.Default.CheckCircle, color = MintPrimary, gradient = RawdatyGradients.AvatarMint, modifier = Modifier.weight(1f))
            StatCard(label = "نسبة الغياب", value = "${100 - attendanceRate}%", icon = Icons.Default.Cancel, color = ColorError, gradient = RawdatyGradients.AvatarAmber, modifier = Modifier.weight(1f))
        }
        RawdatyButton(text = "عرض سجل الحضور الكامل", onClick = onAttendanceClick, icon = Icons.Outlined.CalendarMonth, backgroundColor = MintPrimary, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun TeacherTab(onMessageTeacher: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        SectionHeader("معلمة الفصل")
        RawdatyCard(elevation = 1.dp, containerColor = White) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                RawdatyAvatar("سارة أحمد", size = 64.dp, gradient = RawdatyGradients.AvatarMint)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("أ. سارة أحمد", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BlueDark, fontFamily = CairoFontFamily)
                    Text("المعلمة المسؤولة عن الفصل", style = MaterialTheme.typography.bodySmall, color = Gray500, fontFamily = CairoFontFamily)
                }
            }
        }
        RawdatyButton(text = "بدء محادثة مع المعلمة", onClick = onMessageTeacher, icon = Icons.AutoMirrored.Filled.Chat, modifier = Modifier.fillMaxWidth(), backgroundColor = BluePrimary)
    }
}

@Composable
private fun ProgressItem(label: String, progress: Float, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
            Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = BlueDark, fontFamily = CairoFontFamily)
            Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black, color = color, fontFamily = CairoFontFamily)
        }
        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape), color = color, trackColor = Gray100)
    }
}
