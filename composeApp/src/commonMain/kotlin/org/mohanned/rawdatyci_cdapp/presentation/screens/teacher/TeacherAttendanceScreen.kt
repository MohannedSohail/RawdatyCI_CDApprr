package org.mohanned.rawdatyci_cdapp.presentation.screens.teacher

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceStatus
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

@Composable
fun TeacherAttendanceScreen(
    children: List<Child>,
    attendance: Map<Int, AttendanceStatus>,
    classroomName: String,
    date: String,
    isSaving: Boolean,
    isOffline: Boolean,
    onToggle: (Int) -> Unit,
    onSelectAll: () -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
) {
    val presentCount = attendance.values.count { it == AttendanceStatus.PRESENT }

    Scaffold(
        containerColor = AppBg,
        topBar = { 
            GlassHeader(
                title = "تحضير الطلاب",
                subtitle = "$classroomName — $date",
                onBack = onBack,
                gradient = RawdatyGradients.HeroBlue, // Blue feels professional for admin tasks
                headerHeight = 140.dp
            ) 
        },
        bottomBar = {
            AttendanceBottomSummary(
                presentCount = presentCount,
                totalCount = children.size,
                isSaving = isSaving,
                onSave = onSave
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isOffline) OfflineIndicator()

            // Control Header
            Surface(
                color = White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "قائمة الحضور",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Gray900,
                            fontFamily = CairoFontFamily
                        )
                        Text(
                            "تم رصد $presentCount من أصل ${children.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = BluePrimary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = CairoFontFamily
                        )
                    }

                    RawdatyButton(
                        text = "تحديد الكل",
                        onClick = onSelectAll,
                        icon = Icons.Default.LibraryAddCheck,
                        backgroundColor = BluePrimary.copy(0.1f),
                        modifier = Modifier.height(36.dp),
                        useSmallText = true
                    )
                }
            }

            // Students Grid with better spacing and premium feel
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(children, key = { it.id }) { child ->
                    val isPresent = attendance[child.id] == AttendanceStatus.PRESENT
                    StudentAttendanceItem(
                        child = child,
                        isPresent = isPresent,
                        onToggle = { onToggle(child.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StudentAttendanceItem(child: Child, isPresent: Boolean, onToggle: () -> Unit) {
    val borderColor = if (isPresent) MintPrimary else Gray200
    val bgColor = if (isPresent) MintLight.copy(0.3f) else White

    Column(
        modifier = Modifier
            .aspectRatio(0.85f)
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .clickable(onClick = onToggle)
            .border(if (isPresent) 2.dp else 1.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Modern Avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (isPresent) White else Gray100),
            contentAlignment = Alignment.Center
        ) {
            Text(
                child.fullName.take(1),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = if (isPresent) MintPrimary else Gray400,
                fontFamily = CairoFontFamily
            )
        }

        Text(
            child.fullName,
            style = MaterialTheme.typography.labelSmall,
            color = if (isPresent) Gray900 else Gray600,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontFamily = CairoFontFamily,
            fontWeight = if (isPresent) FontWeight.Bold else FontWeight.Medium
        )

        // Status Indicator with Animation
        AnimatedContent(
            targetState = isPresent,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "attendance_status"
        ) { present ->
            Icon(
                if (present) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                null,
                tint = if (present) MintPrimary else Gray300,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun AttendanceBottomSummary(
    presentCount: Int,
    totalCount: Int,
    isSaving: Boolean,
    onSave: () -> Unit
) {
    Surface(
        color = White,
        shadowElevation = 12.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp).navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Present Stats Card
                Surface(
                    modifier = Modifier.weight(1f),
                    color = MintLight.copy(0.3f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("$presentCount", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = MintPrimary, fontFamily = CairoFontFamily)
                        Spacer(Modifier.width(8.dp))
                        Text("حاضرون", style = MaterialTheme.typography.labelSmall, color = MintPrimary.copy(0.8f), fontFamily = CairoFontFamily)
                    }
                }

                // Absent Stats Card
                Surface(
                    modifier = Modifier.weight(1f),
                    color = ColorError.copy(0.05f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("${totalCount - presentCount}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = ColorError, fontFamily = CairoFontFamily)
                        Spacer(Modifier.width(8.dp))
                        Text("غائبون", style = MaterialTheme.typography.labelSmall, color = ColorError.copy(0.8f), fontFamily = CairoFontFamily)
                    }
                }
            }

            // Interactive Progress Bar
            val progress = if (totalCount > 0) presentCount.toFloat() / totalCount else 0f
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("اكتمال الحضور", style = MaterialTheme.typography.labelSmall, color = Gray500, fontFamily = CairoFontFamily)
                    Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = BluePrimary, fontFamily = CairoFontFamily)
                }
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
                    color = BluePrimary,
                    trackColor = Gray100
                )
            }

            RawdatyButton(
                text = "إرسال التقرير النهائي",
                onClick = onSave,
                isLoading = isSaving,
                icon = Icons.Default.Send,
                backgroundColor = BluePrimary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
