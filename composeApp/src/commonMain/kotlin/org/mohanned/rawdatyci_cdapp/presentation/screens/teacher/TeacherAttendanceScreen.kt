package org.mohanned.rawdatyci_cdapp.presentation.screens.teacher

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.datetime.*
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceStatus
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.*

data class TeacherAttendanceScreen(val classId: String, val className: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: AttendanceViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(classId) {
            viewModel.onIntent(AttendanceIntent.LoadChildren(classId))
            viewModel.onIntent(AttendanceIntent.LoadWeeklyReport(classId))
            viewModel.effect.collect { effect ->
                if (effect is AttendanceEffect.ShowMessage) {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        Scaffold(
            containerColor = AppBg,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                ModernHeader(
                    title = "تحضير الطلاب",
                    subtitle = className,
                    onBack = { navigator.pop() },
                    gradient = RawdatyGradients.Primary,
                    headerHeight = 140.dp
                )
            },
            bottomBar = {
                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                if (state.children.isNotEmpty() && !state.isAlreadyRecorded && state.selectedDate == today) {
                    Surface(
                        color = White,
                        shadowElevation = 15.dp,
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp).navigationBarsPadding()) {
                            RawdatyButton(
                                text = "حفظ وإرسال الحضور",
                                onClick = { viewModel.onIntent(AttendanceIntent.Save(classId)) },
                                isLoading = state.isSaving,
                                modifier = Modifier.fillMaxWidth(),
                                icon = Icons.Default.CloudDone,
                                backgroundColor = BluePrimary
                            )
                        }
                    }
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                
                // شريط الأيام (7 أيام، يبدأ باليوم الحالي)
                WeeklyCalendarStrip(
                    selectedDate = state.selectedDate,
                    weeklySummaries = state.weeklySummaries,
                    onDateSelected = { viewModel.onIntent(AttendanceIntent.ChangeDate(classId, it)) }
                )

                if (state.isAlreadyRecorded) {
                    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                    val message = if (state.selectedDate == today) 
                        "تم تسجيل حضور هذا الفصل اليوم بنجاح." 
                    else 
                        "عرض سجل حضور يوم ${state.selectedDate}"

                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MintPrimary.copy(0.1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MintPrimary.copy(0.2f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, null, tint = MintPrimary, modifier = Modifier.size(20.dp))
                            Text(
                                message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MintPrimary,
                                fontWeight = FontWeight.Bold,
                                fontFamily = CairoFontFamily
                            )
                        }
                    }
                }

                // ملخص سريع
                AttendanceStatsHeader(
                    total = state.children.size,
                    presentCount = state.attendanceMap.values.count { it == AttendanceStatus.PRESENT },
                    onSelectAll = { viewModel.onIntent(AttendanceIntent.SelectAll) },
                    enabled = !state.isAlreadyRecorded && state.selectedDate == Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                )

                if (state.isLoading) {
                    LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(6) { ShimmerBox(Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(22.dp))) }
                    }
                } else {
                    AnimateEntrance {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.children, key = { it.id }) { child ->
                                AttendanceChildCard(
                                    child = child,
                                    currentStatus = state.attendanceMap[child.id] ?: AttendanceStatus.ABSENT,
                                    onStatusChange = { viewModel.onIntent(AttendanceIntent.UpdateStatus(child.id, it)) },
                                    onDetailClick = { navigator.push(TeacherStudentDetailScreen(child.id)) },
                                    enabled = !state.isAlreadyRecorded && state.selectedDate == Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
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
private fun WeeklyCalendarStrip(
    selectedDate: LocalDate,
    weeklySummaries: List<org.mohanned.rawdatyci_cdapp.domain.model.AttendanceSummary>,
    onDateSelected: (LocalDate) -> Unit
) {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    // الترتيب: [اليوم، أمس، أول أمس، ... وصولاً لـ 6 أيام مضت]
    val days = (0..6).map { now.minus(it, DateTimeUnit.DAY) }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(White)
            .padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(days) { date ->
            val isSelected = date == selectedDate
            val summary = weeklySummaries.find { it.date == date.toString() }
            val isRecorded = summary != null && (summary.present > 0 || summary.absent > 0 || summary.late > 0)
            val isToday = date == now
            val isFriday = date.dayOfWeek == DayOfWeek.FRIDAY
            
            val dayName = when(date.dayOfWeek) {
                DayOfWeek.SUNDAY -> "أحد"
                DayOfWeek.MONDAY -> "إثنين"
                DayOfWeek.TUESDAY -> "ثلاثاء"
                DayOfWeek.WEDNESDAY -> "أربعاء"
                DayOfWeek.THURSDAY -> "خميس"
                DayOfWeek.FRIDAY -> "جمعة"
                DayOfWeek.SATURDAY -> "سبت"
                else -> ""
            }

            Surface(
                onClick = { onDateSelected(date) },
                color = if (isSelected) BluePrimary else if (isToday) BlueLight.copy(0.2f) else Gray50,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.width(60.dp),
                border = if (isToday && !isSelected) BorderStroke(1.dp, BluePrimary.copy(0.5f)) else null
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = dayName,
                        fontSize = 10.sp,
                        color = if (isSelected) White else if (isFriday) ColorError.copy(0.6f) else Gray500,
                        fontFamily = CairoFontFamily
                    )
                    Text(
                        text = date.dayOfMonth.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) White else if (isFriday) ColorError else BlueDark,
                        fontFamily = CairoFontFamily
                    )
                    
                    if (isToday && !isFriday) {
                        Box(
                            Modifier
                                .padding(top = 4.dp)
                                .size(6.dp)
                                .background(if (isSelected) White else BluePrimary, CircleShape)
                        )
                    } else if (isFriday) {
                         Box(
                            Modifier
                                .padding(top = 4.dp)
                                .size(6.dp)
                                .background(if (isSelected) ColorError else ColorError.copy(0.5f), CircleShape)
                        )
                    }else if (isRecorded && !isFriday) {
                         Box(
                            Modifier
                                .padding(top = 4.dp)
                                .size(6.dp)
                                .background(if (isSelected) White else MintPrimary, CircleShape)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AttendanceStatsHeader(total: Int, presentCount: Int, onSelectAll: () -> Unit, enabled: Boolean) {
    Surface(
        color = White,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("إحصائية اليوم", style = MaterialTheme.typography.labelSmall, color = Gray500, fontFamily = CairoFontFamily)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("$presentCount / $total", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = BlueDark, fontFamily = CairoFontFamily)
                }
            }

            if (enabled) {
                RawdatyButton(
                    text = "تحضير الكل",
                    onClick = onSelectAll,
                    icon = Icons.Default.DoneAll,
                    useSmallText = true,
                    backgroundColor = Gray50,
                    textColor = BluePrimary,
                    modifier = Modifier.width(130.dp).height(36.dp)
                )
            }
        }
    }
}

@Composable
private fun AttendanceChildCard(
    child: Child,
    currentStatus: AttendanceStatus,
    onStatusChange: (AttendanceStatus) -> Unit,
    onDetailClick: () -> Unit,
    enabled: Boolean
) {
    RawdatyCard(
        modifier = Modifier.padding(horizontal = 20.dp),
        containerColor = White,
        elevation = 2.dp,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.clickable { onDetailClick() }) {
                RawdatyAvatar(
                    name = child.fullName,
                    size = 50.dp,
                    gradient = if (child.gender == "male") RawdatyGradients.AvatarBlue else RawdatyGradients.AvatarMint
                )
            }

            Column(Modifier.weight(1f)) {
                Text(
                    text = child.fullName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = BlueDark,
                    fontFamily = CairoFontFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable { onDetailClick() }
                )
                Text("${child.stars} نجوم تميز", fontSize = 10.sp, color = Gray400, fontFamily = CairoFontFamily)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                AttendanceActionCircle(
                    icon = Icons.Default.Check,
                    isSelected = currentStatus == AttendanceStatus.PRESENT,
                    activeColor = ColorSuccess,
                    onClick = { if (enabled) onStatusChange(AttendanceStatus.PRESENT) }
                )
                AttendanceActionCircle(
                    icon = Icons.Default.Close,
                    isSelected = currentStatus == AttendanceStatus.ABSENT,
                    activeColor = ColorError,
                    onClick = { if (enabled) onStatusChange(AttendanceStatus.ABSENT) }
                )
                AttendanceActionCircle(
                    icon = Icons.Default.AccessTime,
                    isSelected = currentStatus == AttendanceStatus.LATE,
                    activeColor = AmberPrimary,
                    onClick = { if (enabled) onStatusChange(AttendanceStatus.LATE) }
                )
            }
        }
    }
}

@Composable
private fun AttendanceActionCircle(
    icon: ImageVector,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(if (isSelected) 1.2f else 1f)
    Surface(
        onClick = onClick,
        color = if (isSelected) activeColor else Gray100,
        shape = CircleShape,
        modifier = Modifier.size(34.dp).graphicsLayer(scaleX = scale, scaleY = scale)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) White else Gray400,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
