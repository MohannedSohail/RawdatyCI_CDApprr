package org.mohanned.rawdatyci_cdapp.presentation.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PieChart
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
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceSummary
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AttendanceIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AttendanceViewModel

data class TeacherAttendanceHistoryScreen(val classId: String, val className: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: AttendanceViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(classId) {
            viewModel.onIntent(AttendanceIntent.LoadWeeklyReport(classId))
            viewModel.onIntent(AttendanceIntent.LoadMonthlyReport("current", classId))
        }

        Scaffold(
            topBar = {
                ModernHeader(
                    title = "سجل الحضور",
                    subtitle = className,
                    onBack = { navigator.pop() },
                    gradient = RawdatyGradients.Primary,
                    headerHeight = 140.dp
                )
            },
            containerColor = AppBg
        ) { padding ->
            Box(Modifier.padding(padding).fillMaxSize()) {
                if (state.isLoading && state.weeklySummaries.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BluePrimary)
                    }
                } else if (state.error != null && state.weeklySummaries.isEmpty()) {
                    ErrorState(message = state.error!!, onRetry = { 
                        viewModel.onIntent(AttendanceIntent.LoadWeeklyReport(classId))
                    })
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            StatCard(
                                label = "نسبة الحضور لهذا الشهر",
                                value = "${(state.attendanceRate * 100).toInt()}%",
                                icon = Icons.Default.PieChart,
                                color = BluePrimary
                            )
                        }

                        item {
                            Text(
                                "حضور الأسبوع الحالي",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = BlueDark,
                                fontFamily = CairoFontFamily,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        if (state.weeklySummaries.isEmpty()) {
                            item {
                                EmptyState(
                                    title = "لا توجد سجلات",
                                    subtitle = "لم يتم تسجيل حضور لأي يوم في هذا الأسبوع بعد.",
                                    icon = Icons.Default.CalendarMonth
                                )
                            }
                        } else {
                            items(state.weeklySummaries) { summary ->
                                AttendanceSummaryItem(summary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceSummaryItem(summary: AttendanceSummary) {
    RawdatyCard(
        containerColor = White,
        elevation = 2.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MintPrimary.copy(0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CheckCircle, null, tint = MintPrimary)
            }

            Column(Modifier.weight(1f)) {
                Text(
                    text = summary.date,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Gray900,
                    fontFamily = CairoFontFamily
                )
                Text(
                    text = "${summary.present} حاضر • ${summary.absent} غائب • ${summary.late} متأخر",
                    style = MaterialTheme.typography.labelMedium,
                    color = Gray500,
                    fontFamily = CairoFontFamily
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${(summary.presentPct * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = BluePrimary,
                    fontFamily = CairoFontFamily
                )
                Text(
                    text = "نسبة الحضور",
                    style = MaterialTheme.typography.labelSmall,
                    color = Gray400,
                    fontFamily = CairoFontFamily
                )
            }
        }
    }
}
