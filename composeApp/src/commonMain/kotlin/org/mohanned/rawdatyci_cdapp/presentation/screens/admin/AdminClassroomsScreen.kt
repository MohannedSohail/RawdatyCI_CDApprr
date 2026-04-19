package org.mohanned.rawdatyci_cdapp.presentation.screens.admin

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ClassroomsEffect
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ClassroomsIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ClassroomsState
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ClassroomsViewModel
import org.mohanned.rawdatyci_cdapp.domain.model.Classroom

object AdminClassroomsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ClassroomsViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            viewModel.onIntent(ClassroomsIntent.Load)
            viewModel.effect.collect { effect ->
                when (effect) {
                    is ClassroomsEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        AdminClassroomsScreenContent(
            state = state,
            snackbarHostState = snackbarHostState,
            onIntent = viewModel::onIntent,
            onClassClick = { navigator.push(AdminClassDetailScreen(it.id)) },
            onAdd = { navigator.push(AdminAddClassroomScreen(null)) },
            onBack = { navigator.pop() }
        )
    }
}

@Composable
fun AdminClassroomsScreenContent(
    state: ClassroomsState,
    snackbarHostState: SnackbarHostState,
    onIntent: (ClassroomsIntent) -> Unit,
    onClassClick: (Classroom) -> Unit,
    onAdd: () -> Unit,
    onBack: () -> Unit,
) {
    val listState = rememberLazyListState()

    if (state.showDeleteDialog) {
        DeleteConfirmDialog(
            title = "تأكيد الحذف",
            message = "سيتم حذف الفصل وجميع بياناته نهائياً. هل أنت متأكد من هذا الإجراء؟",
            onConfirm = { onIntent(ClassroomsIntent.ConfirmDelete) },
            onDismiss = { onIntent(ClassroomsIntent.DismissDelete) }
        )
    }

    Scaffold(
        containerColor = AppBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            GlassHeader(
                title = "إدارة الفصول الدراسية",
                onBack = onBack,
                gradient = RawdatyGradients.AdminHeader,
                headerHeight = 140.dp
            )
        },
        floatingActionButton = {
            RawdatyFAB(onClick = onAdd, icon = Icons.Default.AddHomeWork)
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Surface(
                color = White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = state.query,
                        onValueChange = { onIntent(ClassroomsIntent.Search(it)) },
                        placeholder = { Text("ابحث عن اسم الفصل...", fontFamily = CairoFontFamily, color = Gray400) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = BluePrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontFamily = CairoFontFamily),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Gray200,
                            focusedContainerColor = Gray50,
                            unfocusedContainerColor = Gray50
                        )
                    )
                }
            }

            if (state.isLoading) {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(6) {
                        ShimmerBox(Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(16.dp)))
                    }
                }
            } else if (state.classrooms.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.Class,
                    title = if (state.error != null) "خطأ في التحميل" else "لا توجد فصول حالياً",
                    subtitle = state.error ?: "ابدأ بإضافة فصول دراسية لتوزيع الطلاب والمعلمات عليها",
                    actionText = if (state.error != null) "إعادة المحاولة" else "إضافة فصل دراسي",
                    onAction = { if (state.error != null) onIntent(ClassroomsIntent.Load) else onAdd() }
                )
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.classrooms, key = { it.id }) { classroom ->
                        ClassroomCard(
                            classroom = classroom,
                            onClick = { onClassClick(classroom) },
                            onDelete = { onIntent(ClassroomsIntent.DeleteRequest(classroom.id)) }
                        )
                    }

                    if (state.isLoadingMore) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = BluePrimary, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ClassroomCard(
    classroom: Classroom,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    RawdatyCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BlueLight.copy(0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.School,
                        null,
                        tint = BluePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(Modifier.weight(1f)) {
                    Text(
                        classroom.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Gray900,
                        fontFamily = CairoFontFamily
                    )
                    Text(
                        if (classroom.teacherName != null) "المعلمة: ${classroom.teacherName}" else "لم يتم تعيين معلمة",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (classroom.teacherName != null) Gray500 else AmberPrimary,
                        fontFamily = CairoFontFamily
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.background(Gray50, CircleShape).size(32.dp)
                ) {
                    Icon(Icons.Default.DeleteOutline, null, tint = ColorError.copy(0.8f), modifier = Modifier.size(18.dp))
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val capacity = classroom.capacity ?: 25
                val progress = if (capacity > 0) classroom.childrenCount.toFloat() / capacity else 0f
                val progressColor = when {
                    progress >= 0.9f -> ColorError
                    progress >= 0.7f -> AmberPrimary
                    else -> MintPrimary
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Groups, null, tint = progressColor, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "الطلاب: ${classroom.childrenCount} من $capacity",
                            style = MaterialTheme.typography.labelSmall,
                            color = Gray700,
                            fontWeight = FontWeight.Bold,
                            fontFamily = CairoFontFamily
                        )
                    }
                    Text(
                        "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = progressColor,
                        fontWeight = FontWeight.Black,
                        fontFamily = CairoFontFamily
                    )
                }

                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                    color = progressColor,
                    trackColor = Gray100
                )
            }
        }
    }
}
