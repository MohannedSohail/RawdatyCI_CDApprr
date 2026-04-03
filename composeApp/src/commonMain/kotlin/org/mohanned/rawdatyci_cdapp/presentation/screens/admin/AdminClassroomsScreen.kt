package org.mohanned.rawdatyci_cdapp.presentation.screens.admin

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import org.mohanned.rawdatyci_cdapp.domain.model.Classroom
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

@Composable
fun AdminClassroomsScreen(
    classrooms: List<Classroom>,
    query: String,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    canLoadMore: Boolean,
    showDeleteDialog: Boolean,
    onSearch: (String) -> Unit,
    onLoadMore: () -> Unit,
    onAdd: () -> Unit,
    onClassClick: (Classroom) -> Unit,
    onDeleteRequest: (Classroom) -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit,
    onBack: () -> Unit,
) {
    val listState = rememberLazyListState()

    // Infinite Scrolling Logic
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && lastVisibleIndex >= classrooms.size - 2 && canLoadMore && !isLoadingMore && !isLoading) {
                    onLoadMore()
                }
            }
    }

    if (showDeleteDialog) {
        DeleteConfirmDialog(
            title = "تأكيد الحذف",
            message = "سيتم حذف الفصل وجميع بياناته نهائياً. هل أنت متأكد من هذا الإجراء؟",
            onConfirm = onConfirmDelete,
            onDismiss = onDismissDelete
        )
    }

    Scaffold(
        containerColor = AppBg,
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
            // Premium Search Box with Cairo
            Surface(
                color = White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = onSearch,
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

            if (isLoading) {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(6) { NotificationItemShimmer() }
                }
            } else if (classrooms.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.Class,
                    title = "لا توجد فصول حالياً",
                    subtitle = "ابدأ بإضافة فصول دراسية لتوزيع الطلاب والمعلمات عليها",
                    actionText = "إضافة فصل دراسي",
                    onAction = onAdd
                )
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(classrooms, key = { it.id }) { classroom ->
                        ClassroomCard(
                            classroom = classroom, 
                            onClick = { onClassClick(classroom) },
                            onDelete = { onDeleteRequest(classroom) }
                        )
                    }

                    if (isLoadingMore) {
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
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Premium Icon Box
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(BlueLight.copy(0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.School,
                        null,
                        tint = BluePrimary,
                        modifier = Modifier.size(28.dp)
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
                    if (classroom.teacherName != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, null, tint = Gray400, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "المعلمة: ${classroom.teacherName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Gray500,
                                fontFamily = CairoFontFamily
                            )
                        }
                    } else {
                        Text(
                            "لم يتم تزويد معلمة",
                            style = MaterialTheme.typography.bodySmall,
                            color = AmberPrimary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = CairoFontFamily
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.background(Gray50, CircleShape).size(36.dp)
                ) {
                    Icon(Icons.Default.DeleteOutline, null, tint = ColorError.copy(0.8f), modifier = Modifier.size(20.dp))
                }
            }

            // Student Capacity Stats with Premium Progress
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
                        Icon(Icons.Default.Groups, null, tint = progressColor, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "الطلاب: ${classroom.childrenCount} من $capacity",
                            style = MaterialTheme.typography.labelMedium,
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
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                    color = progressColor,
                    trackColor = Gray100
                )
            }
        }
    }
}
