package org.mohanned.rawdatyci_cdapp.presentation.screens.parent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.foundation.shape.RoundedCornerShape
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
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceRecord
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceStatus
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AttendanceIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AttendanceViewModel

data class ParentAttendanceScreen(val childId: String, val childName: String) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: AttendanceViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(childId) {
            viewModel.onIntent(AttendanceIntent.LoadChildAttendance(childId))
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("حضور $childName", fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع") }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = AppBg)
                )
            },
            containerColor = AppBg
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                RawdatyCard(modifier = Modifier.padding(16.dp).fillMaxWidth(), containerColor = BluePrimary) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, null, tint = White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("هذا السجل يعرض حضور وغياب طفلك خلال الفترة الحالية.", style = MaterialTheme.typography.bodySmall, fontFamily = CairoFontFamily, color = White)
                    }
                }

                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = BluePrimary) }
                } else if (state.attendanceRecords.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        EmptyState(title = "لا يوجد سجلات", subtitle = "سيظهر سجل الحضور هنا بمجرد تحديثه.")
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.attendanceRecords) { record ->
                            ParentAttendanceItem(record)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ParentAttendanceItem(record: AttendanceRecord) {
    RawdatyCard(modifier = Modifier.fillMaxWidth(), containerColor = White) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(text = record.date, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
                record.notes?.let { notes ->
                    if (notes.isNotBlank()) {
                        Text(text = notes, style = MaterialTheme.typography.labelSmall, color = Gray500, fontFamily = CairoFontFamily)
                    }
                }
            }
            StatusBadge(status = record.status)
        }
    }
}

@Composable
private fun StatusBadge(status: AttendanceStatus) {
    val (label, color) = when (status) {
        AttendanceStatus.PRESENT -> "حاضر" to ColorSuccess
        AttendanceStatus.ABSENT -> "غائب" to ColorError
        AttendanceStatus.LATE -> "متأخر" to AmberPrimary
        AttendanceStatus.EXCUSED -> "بعذر" to BluePrimary
    }
    Surface(color = color.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
        Text(label, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), color = color, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
    }
}
