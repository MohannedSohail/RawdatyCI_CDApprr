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
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

@Composable
fun ParentHomeScreen(
    parentName: String = "عبدالعزيز العلي",
    childName: String = "ليان",
    unreadMessages: Int = 2,
    navIndex: Int = 0,
    onNavSelect: (Int) -> Unit = {},
    onChildClick: () -> Unit = {},
    onMessagesClick: () -> Unit = {},
    onEventsClick: () -> Unit = {}
) {
    val navItems = listOf(
        BottomNavItem("الرئيسية", Icons.Outlined.Home, Icons.Filled.Home),
        BottomNavItem("الرسائل",  Icons.Outlined.Chat, Icons.Filled.Chat),
        BottomNavItem("الفعاليات", Icons.Outlined.CalendarMonth, Icons.Filled.CalendarMonth),
        BottomNavItem("حسابي",    Icons.Outlined.Person, Icons.Filled.Person),
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
            // ── Premium Parent Header ──────────────────────
            GlassHeader(
                title = "مرحباً، $parentName",
                subtitle = "تابعي يوم $childName الدراسي بكل طمأنينة",
                gradient = RawdatyGradients.HeroBlue,
                headerHeight = 180.dp
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(White.copy(0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Celebration, null, tint = White, modifier = Modifier.size(24.dp))
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                
                // ── Student Status Card (Floating) ───────────────────────────
                Column(modifier = Modifier.offset(y = (-40).dp)) {
                    RawdatyCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onChildClick,
                        backgroundColor = White,
                        elevation = 4.dp
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RawdatyAvatar(childName, size = 60.dp, gradient = RawdatyGradients.AvatarBlue)
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    "حالة $childName اليوم", 
                                    style = MaterialTheme.typography.labelSmall, 
                                    color = Gray500,
                                    fontFamily = CairoFontFamily
                                )
                                Text(
                                    "فصل النجوم", 
                                    style = MaterialTheme.typography.titleMedium, 
                                    color = Gray900, 
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = CairoFontFamily
                                )
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(Modifier.size(8.dp).clip(CircleShape).background(MintPrimary))
                                    Text(
                                        "متواجد منذ ٠٨:١٠ ص", 
                                        style = MaterialTheme.typography.bodySmall, 
                                        color = MintPrimary, 
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = CairoFontFamily
                                    )
                                }
                            }
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = Gray300)
                        }
                    }
                }

                // ── Progress & Skills Grid ───────────────────────────
                SectionHeader("التقدم والمهارات", actionText = "التفاصيل", onSeeAll = onChildClick)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), 
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProgressMiniCard("الذكاء اللغوي", "٩٠٪", MintLight, MintPrimary)
                    ProgressMiniCard("المهارات الحركية", "٨٥٪", BlueLight, BluePrimary)
                    ProgressMiniCard("التفاعل الاجتماعي", "٩٥٪", AmberLight, AmberPrimary)
                }

                Spacer(Modifier.height(24.dp))

                // ── Recent Activity Timeline ─────────────────
                SectionHeader("النشاطات الأخيرة")
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ActivityRow("تم الانتهاء من حصة الرسم", "منذ ساعة واحدة", "🎨", BlueLight)
                    ActivityRow("مشاركة فعالة في حصة القرآن", "منذ ٣ ساعات", "📖", MintLight)
                    ActivityRow("تناول وجبة الإفطار الصحية", "منذ ٤ ساعات", "🍏", AmberLight)
                }

                Spacer(Modifier.height(24.dp))

                // ── Upcoming Events ──────────────────────────
                SectionHeader("فعاليات قادمة", actionText = "الكل", onSeeAll = onEventsClick)
                RawdatyCard(
                    modifier = Modifier.fillMaxWidth(), 
                    containerColor = Gray50,
                    elevation = 0.dp
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)).background(White),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("٢٢", style = MaterialTheme.typography.titleLarge, color = ColorError, fontWeight = FontWeight.Black, fontFamily = CairoFontFamily)
                                Text("مارس", style = MaterialTheme.typography.labelSmall, color = Gray400, fontFamily = CairoFontFamily)
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                "رحلة حديقة الحيوان", 
                                style = MaterialTheme.typography.bodyLarge, 
                                color = Gray900, 
                                fontWeight = FontWeight.Bold,
                                fontFamily = CairoFontFamily
                            )
                            Text(
                                "الخميس القادم - ٠٨:٠٠ ص", 
                                style = MaterialTheme.typography.bodySmall, 
                                color = Gray500,
                                fontFamily = CairoFontFamily
                            )
                        }
                        RawdatyButton("تأكيد", onClick = {}, modifier = Modifier.height(36.dp).width(90.dp), useSmallText = true)
                    }
                }

                Spacer(Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun ProgressMiniCard(label: String, value: String, bgColor: Color, textColor: Color) {
    RawdatyCard(
        modifier = Modifier.width(140.dp), 
        containerColor = bgColor,
        elevation = 1.dp
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                value, 
                style = MaterialTheme.typography.headlineSmall, 
                color = textColor, 
                fontWeight = FontWeight.Black,
                fontFamily = CairoFontFamily
            )
            Text(
                label, 
                style = MaterialTheme.typography.labelSmall, 
                color = textColor.copy(0.8f),
                fontWeight = FontWeight.SemiBold,
                fontFamily = CairoFontFamily
            )
        }
    }
}

@Composable
private fun ActivityRow(title: String, timeAgo: String, emoji: String, color: Color) {
    RawdatyCard(
        modifier = Modifier.fillMaxWidth(), 
        containerColor = White,
        elevation = 1.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(color.copy(0.4f)), 
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
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = CairoFontFamily
                )
                Text(
                    timeAgo, 
                    style = MaterialTheme.typography.labelSmall, 
                    color = Gray400,
                    fontFamily = CairoFontFamily
                )
            }
        }
    }
}

@Preview
@Composable
fun ParentHomePreview() {
    RawdatyTheme {
        ParentHomeScreen()
    }
}
