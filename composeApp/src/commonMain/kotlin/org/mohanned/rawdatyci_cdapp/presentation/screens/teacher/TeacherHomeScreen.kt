package org.mohanned.rawdatyci_cdapp.presentation.screens.teacher

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.domain.model.Classroom
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.screens.shared.NotificationsScreen
import org.mohanned.rawdatyci_cdapp.presentation.screens.shared.ProfileScreen
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.TeacherHomeIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.TeacherHomeState
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.TeacherHomeViewModel

object TeacherHomeScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: TeacherHomeViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.onIntent(TeacherHomeIntent.Load)
        }

    TeacherHomeScreenContent(
        state = state,
        onHomeClick = { /* Already here */ },
        onClassClick = { classroom ->
            navigator.push(TeacherMyClassesScreen)
        },
        onAttendanceClick = { classroom ->
            navigator.push(TeacherAttendanceScreen(classroom.id, classroom.name))
        },
        onMessagesClick = { navigator.push(TeacherConversationsScreen) },
        onProfileClick = { navigator.push(ProfileScreen) },
        onNotificationsClick = { navigator.push(NotificationsScreen) }
    )
}
}

@Composable
fun TeacherHomeScreenContent(
    state: TeacherHomeState,
    onHomeClick: () -> Unit,
    onClassClick: (Classroom) -> Unit,
    onAttendanceClick: (Classroom) -> Unit,
    onMessagesClick: () -> Unit,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Scaffold(
        containerColor = AppBg,
        bottomBar = {
            // Simplified Teacher Bottom Nav
            Surface(color = White, shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    IconButton(onClick = onHomeClick) { Icon(Icons.Default.Home, null, tint = BluePrimary) }
                    IconButton(onClick = onMessagesClick) { Icon(Icons.Default.ChatBubbleOutline, null, tint = Gray400) }
                    IconButton(onClick = onNotificationsClick) { Icon(Icons.Default.NotificationsNone, null, tint = Gray400) }
                    IconButton(onClick = onProfileClick) { Icon(Icons.Default.PersonOutline, null, tint = Gray400) }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            WaveHeader(
                title = "أهلاً بكِ، ${state.teacherName}",
                subtitle = "يوم دراسي سعيد وموفق",
                gradient = RawdatyGradients.Primary,
                headerHeight = 220.dp
            )

            Column(
                modifier = Modifier.padding(horizontal = 20.dp).offset(y = (-40).dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Quick Stats Card
                RawdatyCard(containerColor = White, elevation = 2.dp) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatItem("الفصول", state.classes.size.toString(), Icons.Default.Class, BluePrimary)
                        VerticalDivider(modifier = Modifier.height(40.dp).width(1.dp), color = Gray100)
                        StatItem("الحضور", state.attendanceSummary, Icons.Default.HowToReg, MintPrimary)
                        VerticalDivider(modifier = Modifier.height(40.dp).width(1.dp), color = Gray100)
                        StatItem("تنبيهات", "3", Icons.Default.NewReleases, AmberPrimary)
                    }
                }

                SectionHeader("فصولي الدراسية", actionText = "كل الفصول", onSeeAll = { onClassClick(state.classes.first()) })
                
                if (state.isLoading) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(3) { ShimmerBox(Modifier.size(160.dp, 180.dp).clip(RoundedCornerShape(20.dp))) }
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        items(state.classes) { classroom ->
                            TeacherClassCard(
                                classroom = classroom,
                                onClick = { onAttendanceClick(classroom) }
                            )
                        }
                    }
                }

                SectionHeader("أهم النشاطات اليومية")
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ActivityItem("تسجيل حضور طلاب 'البراعم'", "قبل ساعة", Icons.Default.CheckCircle, ColorSuccess)
                    ActivityItem("رسالة جديدة من والدة 'ليان'", "قبل ساعتين", Icons.Default.Message, BluePrimary)
                    ActivityItem("تذكير: موعد الرحلة القادم", "غداً - 9:00 ص", Icons.Default.Event, AmberPrimary)
                }
                
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, icon: ImageVector, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, null, tint = color.copy(0.6f), modifier = Modifier.size(20.dp))
        Text(value, fontWeight = FontWeight.Black, fontSize = 18.sp, color = Gray900, fontFamily = CairoFontFamily)
        Text(label, fontSize = 11.sp, color = Gray500, fontFamily = CairoFontFamily)
    }
}

@Composable
private fun TeacherClassCard(classroom: Classroom, onClick: () -> Unit) {
    RawdatyCard(
        modifier = Modifier.width(160.dp),
        containerColor = White,
        onClick = onClick,
        elevation = 2.dp
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(16.dp)) {
            Box(Modifier.size(60.dp).clip(CircleShape).background(BlueLight), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Groups, null, tint = BluePrimary, modifier = Modifier.size(32.dp))
            }
            Text(classroom.name, fontWeight = FontWeight.Bold, color = Gray900, maxLines = 1, overflow = TextOverflow.Ellipsis, fontFamily = CairoFontFamily)
            Text("${classroom.childrenCount} طفل", fontSize = 12.sp, color = Gray500, fontFamily = CairoFontFamily)
            RawdatyButton(
                text = "الحضور",
                onClick = onClick,
                backgroundColor = BluePrimary.copy(0.1f),
                textColor = BluePrimary,
                modifier = Modifier.fillMaxWidth().height(36.dp),
                useSmallText = true
            )
        }
    }
}

@Composable
private fun ActivityItem(title: String, time: String, icon: ImageVector, color: Color) {
    RawdatyCard(containerColor = White, elevation = 1.dp) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(color.copy(0.1f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Gray800, fontFamily = CairoFontFamily)
                Text(time, fontSize = 11.sp, color = Gray400, fontFamily = CairoFontFamily)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = Gray300, modifier = Modifier.size(18.dp))
        }
    }
}
