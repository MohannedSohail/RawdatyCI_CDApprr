package org.mohanned.rawdatyci_cdapp.presentation.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Groups
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
import org.mohanned.rawdatyci_cdapp.domain.model.Classroom
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ClassroomsIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ClassroomsViewModel

object TeacherMyClassesScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ClassroomsViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.onIntent(ClassroomsIntent.Load)
        }

        Scaffold(
            containerColor = AppBg,
            topBar = {
                ModernHeader(
                    title = "فصولي الدراسية",
                    subtitle = "إدارة الطلاب وتسجيل الحضور",
                    onBack = null,
                    gradient = RawdatyGradients.Primary,
                    headerHeight = 140.dp
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                if (state.isLoading) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        repeat(5) { ShimmerBox(Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(22.dp))) }
                    }
                } else if (state.error != null && state.classrooms.isEmpty()) {
                    EmptyState(
                        title = "خطأ في الاتصال",
                        subtitle = state.error!!,
                        icon = Icons.Default.CloudOff,
                        actionText = "إعادة المحاولة",
                        onAction = { viewModel.onIntent(ClassroomsIntent.Load) }
                    )
                } else if (state.classrooms.isEmpty()) {
                    EmptyState(
                        title = "لا توجد فصول",
                        subtitle = "لم يتم تعيين أي فصل دراسي لكِ بعد.",
                        icon = Icons.Default.Groups
                    )
                } else {
                    AnimateEntrance {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 10.dp, bottom = 30.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.classrooms) { classroom ->
                                TeacherClassListItem(
                                    classroom = classroom,
                                    onClick = { 
                                        navigator.push(TeacherAttendanceScreen(classroom.id, classroom.name))
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeacherClassListItem(classroom: Classroom, onClick: () -> Unit) {
    RawdatyCard(
        modifier = Modifier.padding(horizontal = 20.dp),
        onClick = onClick, 
        containerColor = White, 
        elevation = 3.dp,
        shape = RoundedCornerShape(22.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BlueLight.copy(0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Groups, null, tint = BluePrimary, modifier = Modifier.size(30.dp))
            }
            
            Column(Modifier.weight(1f)) {
                Text(
                    text = classroom.name, 
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, 
                    color = BlueDark,
                    fontFamily = CairoFontFamily
                )
                Text(
                    text = "${classroom.childrenCount} طفل مسجل", 
                    style = MaterialTheme.typography.labelMedium, 
                    color = Gray500, 
                    fontFamily = CairoFontFamily
                )
                
                Surface(
                    color = MintPrimary.copy(0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "تم تسجيل حضور اليوم",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = MintPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CairoFontFamily
                    )
                }
            }
            
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft, 
                null, 
                tint = Gray300,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
