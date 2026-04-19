package org.mohanned.rawdatyci_cdapp.presentation.screens.admin

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.domain.model.Classroom
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ClassroomsIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ClassroomsViewModel

data class AdminClassDetailScreen(val classId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ClassroomsViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(classId) {
            viewModel.onIntent(ClassroomsIntent.LoadClassDetail(classId))
        }

        AdminClassDetailScreenContent(
            classroom = state.currentClass,
            children = state.children,
            isLoading = state.isLoading,
            onEditClick = { navigator.push(AdminAddClassroomScreen(classId)) },
            onDeleteClick = {
                viewModel.onIntent(ClassroomsIntent.DeleteRequest(classId))
                navigator.pop()
            },
            onChildClick = { child ->
                // navigator.push(TeacherStudentDetailScreen(child.id)) 
            },
            onBack = { navigator.pop() }
        )
    }
}

@Composable
fun AdminClassDetailScreenContent(
    classroom: Classroom?,
    children: List<Child>,
    isLoading: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onChildClick: (Child) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = AppBg,
        topBar = {
            GlassHeader(
                title = classroom?.name ?: "تفاصيل الفصل",
                onBack = onBack,
                gradient = RawdatyGradients.AdminHeader,
                headerHeight = 140.dp
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BluePrimary)
            }
        } else if (classroom == null) {
            EmptyState(title = "الفصل غير موجود", icon = Icons.Default.Error)
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    RawdatyCard(containerColor = White, elevation = 2.dp) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("المعلومات الأساسية", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
                                Row {
                                    IconButton(onClick = onEditClick) { Icon(Icons.Default.Edit, null, tint = BluePrimary) }
                                    IconButton(onClick = onDeleteClick) { Icon(Icons.Default.DeleteOutline, null, tint = ColorError) }
                                }
                            }
                            InfoItem(Icons.Default.Person, "المعلمة", classroom.teacherName ?: "غير محدد")
                            InfoItem(Icons.Default.Groups, "عدد الطلاب", "${classroom.childrenCount} / ${classroom.capacity ?: 25}")
                            InfoItem(Icons.Default.CalendarToday, "السنة الدراسية", classroom.academicYear)
                        }
                    }
                }

                item {
                    Text("قائمة الطلاب", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
                }

                if (children.isEmpty()) {
                    item {
                        Surface(color = White, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                            Text("لا يوجد طلاب مسجلين في هذا الفصل حالياً", modifier = Modifier.padding(24.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontFamily = CairoFontFamily, color = Gray500)
                        }
                    }
                } else {
                    items(children) { child ->
                        ChildItem(child = child, onClick = { onChildClick(child) })
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(icon, null, tint = Gray400, modifier = Modifier.size(18.dp))
        Text(label, color = Gray500, style = MaterialTheme.typography.bodySmall, fontFamily = CairoFontFamily)
        Spacer(Modifier.weight(1f))
        Text(value, fontWeight = FontWeight.Bold, color = Gray900, fontFamily = CairoFontFamily)
    }
}

@Composable
private fun ChildItem(child: Child, onClick: () -> Unit) {
    RawdatyCard(onClick = onClick, containerColor = White, elevation = 1.dp) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(12.dp)) {
            RawdatyAvatar(child.fullName, size = 44.dp, gradient = RawdatyGradients.AvatarAmber)
            Column(Modifier.weight(1f)) {
                Text(child.fullName, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
                Text(child.parentName ?: "", style = MaterialTheme.typography.labelSmall, color = Gray400, fontFamily = CairoFontFamily)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = Gray300)
        }
    }
}
