package org.mohanned.rawdatyci_cdapp.presentation.screens.teacher

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
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
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

@Composable
fun TeacherHomeScreen(
    teacherName: String = "سارة أحمد",
    attendanceCount: String = "18/22",
    attendancePercent: Int = 82,
    taskProgress: Float = 0.75f,
    navIndex: Int = 0,
    onNavSelect: (Int) -> Unit = {},
    onAttendanceClick: () -> Unit = {},
    onMessagesClick: () -> Unit = {},
    onClassClick: (Int) -> Unit = {}
) {
    val navItems = listOf(
        BottomNavItem("الرئيسية", Icons.Outlined.Home, Icons.Filled.Home),
        BottomNavItem("الفصول",   Icons.Outlined.Groups, Icons.Filled.Groups),
        BottomNavItem("الرسائل",  Icons.Outlined.Chat, Icons.Filled.Chat),
        BottomNavItem("الملف الشخصي", Icons.Outlined.Person, Icons.Filled.Person),
    )

    Scaffold(
        containerColor = AppBg,
        bottomBar = { RawdatyBottomNav(navItems, navIndex, onNavSelect) },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // ── Premium Teacher Header ──────────────────────
            GlassHeader(
                title = "مرحباً، أ. $teacherName",
                subtitle = "نظام إدارة روضتي - قسم المعلمات",
                gradient = RawdatyGradients.ParentHeader, // Using Greenish gradient for Teachers
                headerHeight = 180.dp
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(White.copy(0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.School, null, tint = White, modifier = Modifier.size(24.dp))
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                
                // ── Dashboard Overview (Floating Cards) ──────
                Column(modifier = Modifier.offset(y = (-40).dp)) {
                    SectionHeader("فصولي الدراسية", actionText = "الكل")
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ClassCard("فصل النجوم", "٢٢ طالباً", "🌟", BlueLight) { onClassClick(1) }
                        ClassCard("فصل الأمل", "١٨ طالباً", "✨", MintLight) { onClassClick(2) }
                        ClassCard("فصل العصافير", "٢٠ طالباً", "🐤", AmberLight) { onClassClick(3) }
                    }
                }

                // ── Attendance Quick Insight ──────────────────────
                SectionHeader("إحصائيات اليوم", actionText = "تحضير", onSeeAll = onAttendanceClick)
                StatCard(
                    label = "نسبة حضور الطلاب",
                    value = "$attendancePercent%",
                    icon = Icons.Default.CheckCircle,
                    color = MintPrimary,
                    gradient = RawdatyGradients.AvatarMint,
                    onClick = onAttendanceClick
                )

                Spacer(Modifier.height(24.dp))

                // ── Daily Agenda ────────────────────────────
                SectionHeader("جدول الأنشطة اليومي")
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    AgendaItem("حصة القراءة", "الفصل: النجوم", "٠٩:٠٠ ص", "📚", BlueLight)
                    AgendaItem("وقت اللعب الحر", "فصل الأمل", "١٠:٣٠ ص", "🎨", AmberLight)
                    AgendaItem("وقت الراحة", "الساحة الرئيسية", "١٢:٠٠ م", "🍎", MintLight)
                }

                Spacer(Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun ClassCard(title: String, subtitle: String, emoji: String, color: Color, onClick: () -> Unit) {
    RawdatyCard(
        modifier = Modifier.width(140.dp), 
        containerColor = color, 
        onClick = onClick,
        elevation = 2.dp
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(White.copy(0.7f)), 
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 22.sp)
            }
            Column {
                Text(
                    title, 
                    style = MaterialTheme.typography.titleMedium, 
                    color = Gray900, 
                    fontWeight = FontWeight.Bold,
                    fontFamily = CairoFontFamily
                )
                Text(
                    subtitle, 
                    style = MaterialTheme.typography.labelSmall, 
                    color = Gray600,
                    fontFamily = CairoFontFamily
                )
            }
        }
    }
}

@Composable
private fun AgendaItem(title: String, desc: String, time: String, emoji: String, color: Color) {
    RawdatyCard(
        modifier = Modifier.fillMaxWidth(), 
        containerColor = White,
        elevation = 1.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(color.copy(0.5f)), 
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 20.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    title, 
                    style = MaterialTheme.typography.bodyLarge, 
                    color = Gray900, 
                    fontWeight = FontWeight.Bold,
                    fontFamily = CairoFontFamily
                )
                Text(
                    desc, 
                    style = MaterialTheme.typography.bodySmall, 
                    color = Gray500,
                    fontFamily = CairoFontFamily
                )
            }
            Text(
                time, 
                style = MaterialTheme.typography.labelMedium, 
                color = Gray400,
                fontWeight = FontWeight.SemiBold,
                fontFamily = CairoFontFamily
            )
        }
    }
}

@Preview
@Composable
fun TeacherHomePreview() {
    RawdatyTheme {
        TeacherHomeScreen()
    }
}
