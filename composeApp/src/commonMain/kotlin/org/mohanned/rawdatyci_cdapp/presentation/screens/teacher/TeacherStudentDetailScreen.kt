package org.mohanned.rawdatyci_cdapp.presentation.screens.teacher

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TeacherStudentDetailScreen(
    child: Child,
    attendanceRate: Int,
    onBack: () -> Unit,
    onMessageParent: () -> Unit,
    onAddNote: () -> Unit,
) {
    var activeTab by remember { mutableStateOf(0) }
    val tabs = listOf("المعلومات", "الأداء", "الحضور")

    Scaffold(
        containerColor = AppBg,
        topBar = { 
            GlassHeader(
                title = "ملف الطالب",
                subtitle = "عرض وتحرير بيانات المستوى",
                onBack = onBack,
                gradient = RawdatyGradients.HeroBlue,
                headerHeight = 140.dp
            ) 
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Premium Hero Profile Section
            Surface(
                color = White,
                shadowElevation = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(top = 24.dp, bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box {
                        RawdatyAvatar(
                            name = child.fullName,
                            size = 100.dp,
                            gradient = RawdatyGradients.AvatarMint
                        )
                        Surface(
                            modifier = Modifier
                                .size(32.dp)
                                .align(Alignment.BottomEnd),
                            color = AmberPrimary,
                            shape = CircleShape,
                            border = BorderStroke(3.dp, White)
                        ) {
                            Icon(Icons.Default.Star, null, tint = White, modifier = Modifier.padding(6.dp))
                        }
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            child.fullName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = BlueDark,
                            fontFamily = CairoFontFamily
                        )
                        Surface(
                            color = BlueLight.copy(0.4f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                child.className,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = BluePrimary,
                                fontWeight = FontWeight.Bold,
                                fontFamily = CairoFontFamily
                            )
                        }
                    }

                    // Achievement Stars Row
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(5) { i ->
                            Icon(
                                if (i < child.stars) Icons.Default.Stars else Icons.Default.StarOutline,
                                null,
                                tint = if (i < child.stars) AmberPrimary else Gray200,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    
                    Spacer(Modifier.height(8.dp))
                }
            }

            // Modern Fluid Tabs
            Surface(
                color = White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Gray100)
                        .padding(4.dp)
                ) {
                    tabs.forEachIndexed { i, tab ->
                        val isSelected = activeTab == i
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) White else Color.Transparent)
                                .clickable { activeTab = i }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                tab,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) BluePrimary else Gray500,
                                fontFamily = CairoFontFamily
                            )
                        }
                    }
                }
            }

            // Scrollable Content area
            Box(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    AnimatedContent(
                        targetState = activeTab,
                        transitionSpec = { fadeIn() with fadeOut() }
                    ) { targetTab ->
                        when (targetTab) {
                            0 -> InfoTab(child)
                            1 -> PerformanceTab()
                            2 -> AttendanceTab(attendanceRate)
                        }
                    }
                    Spacer(Modifier.height(100.dp)) // Extra space for FABs
                }

                // Sticky Action Buttons at bottom with gradient overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, AppBg.copy(0.8f), AppBg),
                                startY = 0f,
                                endY = 100f
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RawdatyButton(
                            text = "إضافة ملاحظة",
                            onClick = onAddNote,
                            icon = Icons.Outlined.NoteAdd,
                            backgroundColor = BluePrimary.copy(0.1f),
                            modifier = Modifier.weight(1f)
                        )
                        RawdatyButton(
                            text = "مراسلة ولي الأمر",
                            onClick = onMessageParent,
                            icon = Icons.AutoMirrored.Filled.Chat,
                            backgroundColor = BluePrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoTab(child: Child) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader("البيانات الأساسية")
        RawdatyCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoRow(Icons.Outlined.School, "الفصل الدراسي الحالي", child.className)
                RawdatyDivider()
                InfoRow(Icons.Outlined.Person, "اسم ولي الأمر", child.parentName ?: "غير محدد")
                RawdatyDivider()
                InfoRow(Icons.Outlined.Phone, "رقم جوال الطوارئ", child.parentPhone ?: "غير محدد")
                RawdatyDivider()
                InfoRow(Icons.Outlined.CalendarMonth, "تاريخ الالتحاق بالروضة", child.enrollmentDate)
            }
        }

        if (child.allergies.isNotEmpty()) {
            SectionHeader("تنبيهات طبية", isCritical = true)
            Surface(
                color = ColorError.copy(0.05f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, ColorError.copy(0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.ReportProblem, null, tint = ColorError, modifier = Modifier.size(24.dp))
                    Column {
                        Text("قائمة الحساسية", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = ColorError, fontFamily = CairoFontFamily)
                        Text(
                            child.allergies.joinToString(" • "), 
                            style = MaterialTheme.typography.bodySmall, 
                            color = ColorError.copy(0.8f),
                            fontFamily = CairoFontFamily
                        )
                    }
                }
            }
        }

        if (child.notes != null) {
            SectionHeader("ملاحظات المعلمة")
            RawdatyCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Outlined.StickyNote2, null, tint = BluePrimary, modifier = Modifier.size(20.dp))
                        Text("آخر تحديث للملف", style = MaterialTheme.typography.labelSmall, color = Gray500, fontFamily = CairoFontFamily)
                    }
                    Text(
                        child.notes, 
                        style = MaterialTheme.typography.bodyMedium, 
                        color = Gray700, 
                        lineHeight = 24.sp,
                        fontFamily = CairoFontFamily
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Gray100), 
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = BluePrimary, modifier = Modifier.size(20.dp))
        }
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Gray500, fontFamily = CairoFontFamily)
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = BlueDark, fontFamily = CairoFontFamily)
        }
    }
}

@Composable
private fun PerformanceTab() {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        SectionHeader("تقييم المهارات")
        RawdatyCard {
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                PerformanceItem("اللغة العربية والقرآن", 88, BluePrimary)
                PerformanceItem("المهارات الاجتماعية والتفاعل", 94, MintPrimary)
                PerformanceItem("النشاط البدني والحركي", 75, AmberPrimary)
                PerformanceItem("الرياضيات والذكاء المنطقي", 82, BlueDark)
            }
        }
    }
}

@Composable
private fun PerformanceItem(label: String, percentage: Int, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
            Text(label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = BlueDark, fontFamily = CairoFontFamily)
            Text("$percentage%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = color, fontFamily = CairoFontFamily)
        }
        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
            color = color,
            trackColor = Gray100
        )
    }
}

@Composable
private fun AttendanceTab(rate: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        SectionHeader("إحصائيات الحضور")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                number = "$rate%",
                label = "نسبة الحضور",
                icon = Icons.Default.CheckCircle,
                gradient = RawdatyGradients.StatMint,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                number = "${100 - rate}%",
                label = "نسبة الغياب",
                icon = Icons.Default.Cancel,
                gradient = RawdatyGradients.StatError,
                modifier = Modifier.weight(1f)
            )
        }
        
        Surface(
            color = MintLight.copy(0.3f),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, MintPrimary.copy(0.1f))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Default.EventAvailable, null, tint = MintPrimary, modifier = Modifier.size(32.dp))
                Text(
                    "الطالب ملتزم جداً بالحضور، مما ينعكس إيجاباً على مستواه التعليمي.", 
                    style = MaterialTheme.typography.bodySmall, 
                    color = MintPrimary, 
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontFamily = CairoFontFamily
                )
            }
        }
    }
}

@Preview
@Composable
fun TeacherStudentDetailPreview() {
    RawdatyTheme {
        TeacherStudentDetailScreen(
            child = Child(1, "أحمد محمد العلي", "2019-05-10", "male", null, 1, "فصل الزهور", 1, "محمد العلي", "0501234567", "2024-09-01", 4, "طالب متميز ويحب الرسم والأنشطة فنية", listOf("الفول السوداني", "الحليب")),
            attendanceRate = 92,
            onBack = {},
            onMessageParent = {},
            onAddNote = {}
        )
    }
}
