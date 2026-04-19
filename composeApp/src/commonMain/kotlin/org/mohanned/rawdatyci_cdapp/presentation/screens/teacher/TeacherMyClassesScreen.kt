package org.mohanned.rawdatyci_cdapp.presentation.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
                GlassHeader(
                    title = "فصولي الدراسية",
                    onBack = { navigator.pop() },
                    gradient = RawdatyGradients.Primary,
                    headerHeight = 120.dp
                )
            }
        ) { padding ->
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BluePrimary)
                }
            } else if (state.error != null) {
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
                    subtitle = "لم يتم تعيين فصول لكِ بعد",
                    icon = Icons.Default.Groups
                )
            } else {
                LazyColumn(
                    modifier = Modifier.padding(padding).fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.classrooms) { classroom ->
                        TeacherClassItem(
                            classroom = classroom,
                            onClick = { 
                                // Navigation to class detail or attendance
                                navigator.push(TeacherAttendanceScreen(classroom.id, classroom.name))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TeacherClassItem(classroom: Classroom, onClick: () -> Unit) {
    RawdatyCard(onClick = onClick, containerColor = White, elevation = 2.dp) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(Modifier.size(50.dp).background(BlueLight, MaterialTheme.shapes.medium), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Groups, null, tint = BluePrimary)
            }
            Column(Modifier.weight(1f)) {
                Text(classroom.name, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
                Text("${classroom.childrenCount} طفل مسجل", style = MaterialTheme.typography.bodySmall, color = Gray500, fontFamily = CairoFontFamily)
            }
            RawdatyButton(
                text = "فتح",
                onClick = onClick,
                useSmallText = true,
                modifier = Modifier.width(80.dp).height(36.dp)
            )
        }
    }
}
