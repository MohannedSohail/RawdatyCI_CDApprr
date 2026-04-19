package org.mohanned.rawdatyci_cdapp.presentation.screens.teacher

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceStatus
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AttendanceEffect
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AttendanceIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AttendanceViewModel

data class TeacherAttendanceScreen(val classId: String, val className: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: AttendanceViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(classId) {
            viewModel.onIntent(AttendanceIntent.LoadChildren(classId))
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
                GlassHeader(
                    title = "تحضير الطلاب - $className",
                    onBack = { navigator.pop() },
                    gradient = RawdatyGradients.Primary,
                    headerHeight = 120.dp
                )
            },
            bottomBar = {
                Surface(color = White, shadowElevation = 8.dp) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.onIntent(AttendanceIntent.SelectAll) },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, BluePrimary)
                        ) {
                            Text("حضور الكل", fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold, color = BluePrimary)
                        }
                        RawdatyButton(
                            text = "حفظ الكشف",
                            onClick = { viewModel.onIntent(AttendanceIntent.Save(classId)) },
                            modifier = Modifier.weight(1f).height(52.dp),
                            isLoading = state.isSaving
                        )
                    }
                }
            }
        ) { padding ->
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BluePrimary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(padding).fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.children, key = { it.id }) { child ->
                        AttendanceChildCard(
                            child = child,
                            status = state.attendanceMap[child.id] ?: AttendanceStatus.PRESENT,
                            onStatusChange = { status ->
                                viewModel.onIntent(AttendanceIntent.UpdateStatus(child.id, status))
                            }
                        )
                    }
                    item { Spacer(Modifier.height(20.dp)) }
                }
            }
        }
    }
}

@Composable
private fun AttendanceChildCard(
    child: Child,
    status: AttendanceStatus,
    onStatusChange: (AttendanceStatus) -> Unit
) {
    RawdatyCard(containerColor = White, elevation = 2.dp) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RawdatyAvatar(child.fullName, size = 48.dp, gradient = RawdatyGradients.AvatarAmber)
            Text(
                child.fullName,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                fontFamily = CairoFontFamily,
                fontSize = 14.sp
            )
            
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                AttendanceStatusButton(AttendanceStatus.PRESENT, "ح", status == AttendanceStatus.PRESENT, onStatusChange)
                AttendanceStatusButton(AttendanceStatus.ABSENT, "غ", status == AttendanceStatus.ABSENT, onStatusChange)
                AttendanceStatusButton(AttendanceStatus.LATE, "ت", status == AttendanceStatus.LATE, onStatusChange)
            }
        }
    }
}

@Composable
private fun AttendanceStatusButton(
    targetStatus: AttendanceStatus,
    label: String,
    isSelected: Boolean,
    onClick: (AttendanceStatus) -> Unit
) {
    val color = when (targetStatus) {
        AttendanceStatus.PRESENT -> ColorSuccess
        AttendanceStatus.ABSENT -> ColorError
        AttendanceStatus.LATE -> AmberPrimary
        else -> Gray400
    }

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (isSelected) color else color.copy(0.1f))
            .border(1.dp, color.copy(0.2f), CircleShape)
            .clickable { onClick(targetStatus) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            color = if (isSelected) White else color,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            fontFamily = CairoFontFamily
        )
    }
}
