package org.mohanned.rawdatyci_cdapp.presentation.screens.admin

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        var showDeleteDialog by remember { mutableStateOf(false) }

        LaunchedEffect(classId) {
            viewModel.onIntent(ClassroomsIntent.LoadClassDetail(classId))
        }

        RawdatyConfirmDialog(
            show = showDeleteDialog,
            title = "حذف الفصل الدراسي؟",
            message = "هل أنت متأكد من حذف فصل \"${state.currentClass?.name}\"؟\nسيتم حذف جميع سجلات الطلاب والحضور المتعلقة بهذا الفصل بشكل نهائي.",
            confirmText = "حذف نهائي",
            onConfirm = {
                viewModel.onIntent(ClassroomsIntent.DeleteRequest(classId))
                showDeleteDialog = false
                navigator.pop()
            },
            onDismiss = { showDeleteDialog = false }
        )

        Scaffold(
            containerColor = AppBg,
            topBar = {
                GlassHeader(
                    title = state.currentClass?.name ?: "تفاصيل الفصل",
                    onBack = { navigator.pop() },
                    gradient = RawdatyGradients.AdminHeader,
                    headerHeight = 140.dp
                )
            }
        ) { padding ->
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BluePrimary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(padding).fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // 1. بطاقة المعلومات الأساسية (تم نقل الأزرار وتصحيح اسم المعلمة)
                    item {
                        state.currentClass?.let { classroom ->
                            ModernInfoCard(
                                classroom = classroom,
                                onEdit = { navigator.push(AdminAddClassroomScreen(classId)) },
                                onDelete = { showDeleteDialog = true }
                            )
                        }
                    }

                    // 2. عنوان قائمة الطلاب
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "قائمة الطلاب",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                fontFamily = CairoFontFamily,
                                color = Gray900
                            )
                            Surface(
                                color = BluePrimary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(100)
                            ) {
                                Text(
                                    "${state.children.size} طالب",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    fontFamily = CairoFontFamily,
                                    color = BluePrimary,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }

                    // 3. قائمة الطلاب (تم إزالة شريط البحث كما هو موضح في آخر طلب)
                    if (state.children.isEmpty()) {
                        item {
                            EmptyState(
                                title = "لا يوجد طلاب حالياً",
                                icon = Icons.Default.PeopleOutline
                            )
                        }
                    } else {
                        items(state.children) { child ->
                            ModernChildItem(child = child)
                        }
                    }

                    item { Spacer(Modifier.height(40.dp)) }
                }
            }
        }
    }
}

@Composable
fun ModernInfoCard(classroom: Classroom, onEdit: () -> Unit, onDelete: () -> Unit) {
    RawdatyCard(containerColor = White, elevation = 0.dp, accentBorder = Gray100) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            // الصف العلوي: العنوان والأيقونات
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier.size(50.dp).background(RawdatyGradients.AdminHeader, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.School, null, tint = White, modifier = Modifier.size(26.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(classroom.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Gray900, fontFamily = CairoFontFamily)
                        Text("السنة الدراسية: ${classroom.academicYear}", style = MaterialTheme.typography.bodySmall, color = Gray600, fontFamily = CairoFontFamily)
                    }
                }
                
                // أزرار الحذف والتعديل داخل الكارت بجانب اسم الفصل
                Row {
//                    IconButton(onClick = onEdit) {
//                        Icon(Icons.Default.Edit, "تعديل", tint = BluePrimary, modifier = Modifier.size(22.dp))
//                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.DeleteOutline, "حذف", tint = ColorError, modifier = Modifier.size(22.dp))
                    }
                }
            }

            HorizontalDivider(color = Gray100)

            // شبكة المعلومات: المعلمة والحالة
            Row(modifier = Modifier.fillMaxWidth()) {
                InfoStatItem(Modifier.weight(1f), Icons.Default.Person, "المعلمة", classroom.teacherName ?: "غير محدد")
                Box(Modifier.width(1.dp).height(40.dp).background(Gray100).align(Alignment.CenterVertically))
                InfoStatItem(Modifier.weight(1f), Icons.Default.EventAvailable, "الحالة", if(classroom.isActive) "نشط" else "متوقف")
            }

            // مؤشر السعة
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("سعة الاستيعاب", style = MaterialTheme.typography.bodySmall, color = Gray600, fontFamily = CairoFontFamily)
                    Text("${classroom.children?.size}/${classroom.capacity ?: 25}", fontWeight = FontWeight.Bold, color = Gray900, fontFamily = CairoFontFamily)
                }
                val progress = (classroom.childrenCount.toFloat() / (classroom.capacity ?: 25).toFloat()).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                    color = if (progress > 0.9f) ColorError else BluePrimary,
                    trackColor = Gray100,
                )
            }
        }
    }
}

@Composable
fun InfoStatItem(modifier: Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = Gray400, modifier = Modifier.size(18.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = Gray600, fontFamily = CairoFontFamily)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Gray900, fontFamily = CairoFontFamily)
    }
}

@Composable
fun ModernChildItem(child: Child) {
    RawdatyCard(
        containerColor = White,
        elevation = 0.dp,
        accentBorder = Gray100
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            RawdatyAvatar(child.fullName, size = 56.dp, gradient = RawdatyGradients.AvatarAmber)
            Column(Modifier.weight(1f)) {
                Text(child.fullName, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily, color = Gray900, style = MaterialTheme.typography.bodyLarge)
//                Text(
//                    text = "ولي الأمر: ${child.parentName ?: "غير مسجل"}",
//                    style = MaterialTheme.typography.bodySmall,
//                    color = Gray600,
//                    fontFamily = CairoFontFamily
//                )
            }
//            Surface(
//                color = Gray50,
//                shape = CircleShape,
//                modifier = Modifier.size(36.dp)
//            ) {
//                Box(contentAlignment = Alignment.Center) {
//                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = Gray900, modifier = Modifier.size(20.dp))
//                }
//            }
        }
    }
}
