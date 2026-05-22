package org.mohanned.rawdatyci_cdapp.presentation.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceRecord
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceStatus
import org.mohanned.rawdatyci_cdapp.presentation.components.AnimateEntrance
import org.mohanned.rawdatyci_cdapp.presentation.components.EmptyState
import org.mohanned.rawdatyci_cdapp.presentation.components.ModernHeader
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyCard
import org.mohanned.rawdatyci_cdapp.presentation.components.ShimmerBox
import org.mohanned.rawdatyci_cdapp.presentation.theme.AmberPrimary
import org.mohanned.rawdatyci_cdapp.presentation.theme.AppBg
import org.mohanned.rawdatyci_cdapp.presentation.theme.BlueDark
import org.mohanned.rawdatyci_cdapp.presentation.theme.BluePrimary
import org.mohanned.rawdatyci_cdapp.presentation.theme.CairoFontFamily
import org.mohanned.rawdatyci_cdapp.presentation.theme.ColorError
import org.mohanned.rawdatyci_cdapp.presentation.theme.Gray500
import org.mohanned.rawdatyci_cdapp.presentation.theme.MintPrimary
import org.mohanned.rawdatyci_cdapp.presentation.theme.RawdatyGradients
import org.mohanned.rawdatyci_cdapp.presentation.theme.White
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AttendanceIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AttendanceViewModel

data class ParentAttendanceScreen(val childId: String, val childName: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: AttendanceViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        val total = state.attendanceRecords.size
        val present = state.attendanceRecords.count { it.status == AttendanceStatus.PRESENT }
        val absent = state.attendanceRecords.count { it.status == AttendanceStatus.ABSENT }
        val late = state.attendanceRecords.count { it.status == AttendanceStatus.LATE }
        val excused = state.attendanceRecords.count { it.status == AttendanceStatus.EXCUSED }

        LaunchedEffect(childId) {
            viewModel.onIntent(AttendanceIntent.LoadChildAttendance(childId))
        }

        Scaffold(
            containerColor = AppBg,
            topBar = {
                ModernHeader(
                    title = "سجل حضور $childName",
                    subtitle = "سجل الانضباط والمواظبة الشهري",
                    gradient = RawdatyGradients.HeroBlue,
                    headerHeight = 140.dp,
                    onBack = { navigator.pop() }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {

                Spacer(modifier = Modifier.height(15.dp))
                AnimateEntrance {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {


                        AttendanceStatCard(
                            label = "أيام الحضور",
                            value = "$present",
                            icon = Icons.Default.CheckCircle,
                            color = MintPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        AttendanceStatCard(
                            label = "أيام الغياب",
                            value = "${absent}",
                            icon = Icons.Default.Cancel,
                            color = ColorError,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                AnimateEntrance {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {


                        AttendanceStatCard(
                            label = "متأخر",
                            value = "$late",
                            icon = Icons.Default.Schedule,
                            color = AmberPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        AttendanceStatCard(
                            label = "بعذر",
                            value = "${excused}",
                            icon = Icons.Default.AssignmentTurnedIn,
                            color = BlueDark,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(25.dp))

                if (state.isLoading) {
                    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(5) { ShimmerBox(Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(16.dp))) }
                    }
                } else if (state.attendanceRecords.isEmpty()) {
                    EmptyState(
                        icon = Icons.Default.EventNote,
                        title = "لا يوجد سجلات",
                        subtitle = "لم يتم تسجيل أي بيانات حضور لهذا الشهر بعد."
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 30.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.attendanceRecords) { record ->
                            AttendanceListCard(record)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AttendanceStatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        color = White,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(38.dp).background(color.copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
            }
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = BlueDark, fontFamily = CairoFontFamily, modifier = Modifier.padding(top = 8.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, color = Gray500, fontFamily = CairoFontFamily)
        }
    }
}

@Composable
private fun AttendanceListCard(record: AttendanceRecord) {
    val statusColor = when (record.status) {
        AttendanceStatus.PRESENT -> MintPrimary
        AttendanceStatus.ABSENT -> ColorError
        AttendanceStatus.LATE -> AmberPrimary
        AttendanceStatus.EXCUSED -> BluePrimary
    }

    RawdatyCard(containerColor = White, elevation = 2.dp) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(statusColor.copy(0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Today, null, tint = statusColor, modifier = Modifier.size(22.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(record.date, fontWeight = FontWeight.Bold, color = BlueDark, fontFamily = CairoFontFamily)
                if (!record.notes.isNullOrBlank()) {
                    Text(record.notes!!, style = MaterialTheme.typography.labelSmall, color = Gray500, fontFamily = CairoFontFamily)
                }
            }

            Surface(
                color = statusColor.copy(0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = when(record.status) {
                        AttendanceStatus.PRESENT -> "حاضر"
                        AttendanceStatus.ABSENT -> "غائب"
                        AttendanceStatus.LATE -> "متأخر"
                        AttendanceStatus.EXCUSED -> "بعذر"
                    },
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    fontFamily = CairoFontFamily
                )
            }
        }
    }
}
