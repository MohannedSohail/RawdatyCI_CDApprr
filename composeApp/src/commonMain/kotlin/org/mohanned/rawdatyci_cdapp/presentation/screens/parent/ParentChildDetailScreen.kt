package org.mohanned.rawdatyci_cdapp.presentation.screens.parent

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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ParentChildDetailScreen(
    child: Child,
    attendanceRate: Int,
    onBack: () -> Unit,
    onAttendanceClick: () -> Unit,
    onMessageTeacher: () -> Unit,
) {
    var activeTab by remember { mutableStateOf(0) }
    val tabs = listOf("النمو والتعلم", "سجل الحضور", "طاقم الفصل")

    Scaffold(
        containerColor = AppBg,
        topBar = {
            GlassHeader(
                title = "ملف طفلي",
                onBack = onBack,
                gradient = RawdatyGradients.HeroBlue,
                headerHeight = 140.dp
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Profile Header Section with Premium feel
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
                        RawdatyAvatar(child.fullName, size = 100.dp, gradient = RawdatyGradients.AvatarBlue)
                        Surface(
                            modifier = Modifier
                                .size(32.dp)
                                .align(Alignment.BottomEnd),
                            color = MintPrimary,
                            shape = CircleShape,
                            border = BorderStroke(3.dp, White)
                        ) {
                            Icon(Icons.Default.Verified, null, tint = White, modifier = Modifier.padding(6.dp))
                        }
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            child.fullName, 
                            style = MaterialTheme.typography.titleLarge, 
                            fontWeight = FontWeight.Black, 
                            color = BlueDark,
                            fontFamily = CairoFontFamily
                        )
                        Surface(
                            color = BlueLight.copy(0.4f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.School, null, tint = BluePrimary, modifier = Modifier.size(16.dp))
                                Text(
                                    child.className, 
                                    style = MaterialTheme.typography.labelMedium, 
                                    color = BluePrimary, 
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = CairoFontFamily
                                )
                            }
                        }
                    }

                    // Modern Tabs (Segmented Control style)
                    Surface(
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        color = Gray100,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(modifier = Modifier.padding(4.dp)) {
                            tabs.forEachIndexed { i, tab ->
                                val isSelected = activeTab == i
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) White else Color.Transparent)
                                        .clickable { activeTab = i }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        tab,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) BluePrimary else Gray500,
                                        fontFamily = CairoFontFamily
                                    )
                                }
                            }
                        }
                    }
                }
            }

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
                        // Growth & Learning
                        0 -> {
                            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                                SectionHeader("المهارات المكتسبة")
                                RawdatyCard(elevation = 1.dp) {
                                    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                                        ProgressItem("اللغة العربية والقرآن", 0.92f, BluePrimary)
                                        ProgressItem("الذكاء المنطقي والرياضي", 0.78f, BlueDark)
                                        ProgressItem("المهارات الاجتماعية", 0.95f, MintPrimary)
                                        ProgressItem("السلوك والمشاركة", 1.00f, AmberPrimary)
                                    }
                                }

                                // Stars Summary
                                SectionHeader("لوحة التميز")
                                RawdatyCard(elevation = 1.dp) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text("عدد النجوم", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = BlueDark, fontFamily = CairoFontFamily)
                                            Text("تقييم المعلمة لهذا الأسبوع", style = MaterialTheme.typography.labelSmall, color = Gray500, fontFamily = CairoFontFamily)
                                        }
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            repeat(5) { i ->
                                                Icon(
                                                    if (i < child.stars) Icons.Default.Stars else Icons.Outlined.StarOutline,
                                                    null,
                                                    tint = if (i < child.stars) AmberPrimary else Gray200,
                                                    modifier = Modifier.size(32.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                                
                                if (child.notes != null) {
                                    SectionHeader("كلمة المعلمة")
                                    RawdatyCard {
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

                        // Attendance
                        1 -> {
                            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                                SectionHeader("ملخص الحضور")
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    StatCard(
                                        number = "$attendanceRate%",
                                        label = "نسبة الحضور",
                                        icon = Icons.Default.CheckCircle,
                                        gradient = RawdatyGradients.StatMint,
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatCard(
                                        number = "${100 - attendanceRate}%",
                                        label = "نسبة الغياب",
                                        icon = Icons.Default.Cancel,
                                        gradient = RawdatyGradients.StatError,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                RawdatyButton(
                                    text = "عرض التفاصيل اليومية",
                                    onClick = onAttendanceClick,
                                    icon = Icons.Outlined.CalendarMonth,
                                    backgroundColor = MintPrimary,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // Teacher Info
                        2 -> {
                            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                                SectionHeader("معلمة الفصل")
                                RawdatyCard(elevation = 1.dp) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                        RawdatyAvatar("سارة أحمد", size = 64.dp, gradient = RawdatyGradients.AvatarMint)
                                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text("أ. سارة أحمد", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BlueDark, fontFamily = CairoFontFamily)
                                            Text("المعلمة المسؤولة عن فصل النجوم", style = MaterialTheme.typography.bodySmall, color = Gray500, fontFamily = CairoFontFamily)
                                            Surface(
                                                color = ColorSuccess.copy(0.1f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Box(Modifier.size(6.dp).clip(CircleShape).background(ColorSuccess))
                                                    Text("متاحة الآن للاستفسارات", style = MaterialTheme.typography.labelSmall, color = ColorSuccess, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
                                                }
                                            }
                                        }
                                    }
                                }

                                RawdatyButton(
                                    text = "بدء محادثة فورية مع المعلمة",
                                    onClick = onMessageTeacher,
                                    icon = Icons.AutoMirrored.Filled.Chat,
                                    modifier = Modifier.fillMaxWidth(),
                                    backgroundColor = BluePrimary
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun ProgressItem(label: String, progress: Float, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
            Text(label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = BlueDark, fontFamily = CairoFontFamily)
            Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = color, fontFamily = CairoFontFamily)
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
            color = color,
            trackColor = Gray100
        )
    }
}

@Preview
@Composable
fun ParentChildDetailPreview() {
    RawdatyTheme {
        ParentChildDetailScreen(
            child = Child(
                id = 1, fullName = "أحمد محمد العلي",
                dateOfBirth = "2019-05-10", gender = "male", photoUrl = null,
                classId = 1, className = "فصل النجوم",
                parentId = 1, parentName = "محمد العلي",
                parentPhone = "0501234567", enrollmentDate = "2024-09-01",
                stars = 4, notes = "أحمد طالب مجتهد جداً ويظهر حماساً كبيراً في تعلم القراءة والمهارات الحسابية البسيطة."
            ),
            attendanceRate = 92,
            onBack = {},
            onAttendanceClick = {},
            onMessageTeacher = {}
        )
    }
}
