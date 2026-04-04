package org.mohanned.rawdatyci_cdapp.presentation.screens.admin

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import org.mohanned.rawdatyci_cdapp.domain.model.News
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

@Composable
fun AdminDashboardScreen(
    totalParents: Int,
    totalTeachers: Int,
    totalClasses: Int,
    recentNews: List<News>,
    isLoading: Boolean,
    navIndex: Int = 0,
    onNavSelect: (Int) -> Unit = {},
    onQuickAction: (String) -> Unit = {}
) {
    val navItems = listOf(
        BottomNavItem("الرئيسية", Icons.Outlined.GridView, Icons.Filled.GridView),
        BottomNavItem("الفصول", Icons.Outlined.Groups, Icons.Filled.Groups),
        BottomNavItem("المستخدمين", Icons.Outlined.PersonAdd, Icons.Filled.PersonAdd),
        BottomNavItem("الإعدادات", Icons.Outlined.Settings, Icons.Filled.Settings),
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
            // ── Premium Admin Header ────────────────
            GlassHeader(
                title = "لوحة التحكم",
                subtitle = "نظام إدارة روضتي المتكامل",
                gradient = RawdatyGradients.AdminHeader,
                headerHeight = 180.dp
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(White.copy(0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AdminPanelSettings,
                        null,
                        tint = White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {

                // ── KPI Summary Cards (Floating Effect) ────────────────────────
                Column(modifier = Modifier.offset(y = (-40).dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            label = "أولياء الأمور",
                            value = totalParents.toString(),
                            icon = Icons.Default.Groups,
                            color = BluePrimary,
                            modifier = Modifier.weight(1f)
                        ) { onQuickAction("المستخدمون") }
                        StatCard(
                            label = "المعلمات",
                            value = totalTeachers.toString(),
                            icon = Icons.Default.School,
                            color = MintPrimary,
                            modifier = Modifier.weight(1f)
                        ) { onQuickAction("المستخدمون") }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            label = "الفصول الدراسية",
                            value = totalClasses.toString(),
                            icon = Icons.Default.Class,
                            color = AmberPrimary,
                            modifier = Modifier.weight(1f)
                        ) { onQuickAction("الفصول") }
                        StatCard(
                            label = "حالة النظام",
                            value = "١٠٠٪",
                            icon = Icons.Default.VerifiedUser,
                            color = BluePrimary,
                            modifier = Modifier.weight(1f)
                        ) {}
                    }
                }

                // ── Recent News ─────────────────────────────
                if (recentNews.isNotEmpty()) {
                    SectionHeader(
                        "آخر الأخبار والتعميمات",
                        actionText = "عرض الكل",
                        onSeeAll = { onQuickAction("التعميمات") })
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        recentNews.take(3).forEach { item ->
                            RecentNewsItem(item) { onQuickAction("التعميمات") }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ── Quick Actions ───────────────────────────
                SectionHeader("الوصول السريع")
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionItem(
                        "إضافة فصل",
                        Icons.Default.AddHomeWork,
                        BluePrimary
                    ) { onQuickAction("إضافة فصل") }
                    QuickActionItem(
                        "إرسال تنبيه",
                        Icons.Default.Campaign,
                        AmberPrimary
                    ) { onQuickAction("التعميمات") }
                    QuickActionItem(
                        "إضافة مستخدم",
                        Icons.Default.PersonAdd,
                        MintPrimary
                    ) { onQuickAction("المستخدمون") }
                    QuickActionItem(
                        "الشكاوى",
                        Icons.Default.Feedback,
                        ColorError
                    ) { onQuickAction("الشكاوى") }
                }

                Spacer(Modifier.height(24.dp))

                // ── System Health Widget ────────────────────
                SectionHeader("التقارير والإحصائيات")
                RawdatyCard(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = BlueLight.copy(alpha = 0.5f),
                    elevation = 0.dp
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Analytics,
                                null,
                                tint = BluePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                "عرض التقارير التفصيلية",
                                style = MaterialTheme.typography.titleSmall,
                                color = BluePrimary,
                                fontWeight = FontWeight.Bold,
                                fontFamily = CairoFontFamily
                            )
                            Text(
                                "تحليل أداء المعلمات وحضور الطلاب",
                                style = MaterialTheme.typography.bodySmall,
                                color = BluePrimary.copy(0.7f),
                                fontFamily = CairoFontFamily
                            )
                        }
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            null,
                            tint = BluePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun RecentNewsItem(news: News, onClick: () -> Unit) {
    RawdatyCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = White,
        onClick = onClick,
        elevation = 2.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (news.isPinned) AmberLight else BlueLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (news.isPinned) Icons.Default.PushPin else Icons.Default.Description,
                    null,
                    tint = if (news.isPinned) AmberPrimary else BluePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(Modifier.weight(1f)) {
                Text(
                    news.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = CairoFontFamily
                )
                Text(
                    news.createdAt,
                    style = MaterialTheme.typography.labelSmall,
                    color = Gray400,
                    fontFamily = CairoFontFamily
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                null,
                tint = Gray300,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun QuickActionItem(label: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .width(90.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(color.copy(0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
        }
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = Gray700,
            fontWeight = FontWeight.Bold,
            fontFamily = CairoFontFamily,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Preview
@Composable
fun AdminDashboardPreview() {
    RawdatyTheme {
        AdminDashboardScreen(
            totalParents = 120,
            totalTeachers = 15,
            totalClasses = 8,
            recentNews = listOf(
                News(
                    1,
                    "بدء التسجيل للترم الثاني",
                    "يسرنا إبلاغكم بفتح باب التسجيل المبكر للفصل الدراسي القادم.",
                    "الإدارة",
                    true,
                    "Mohanned",
                    "24 مارس"
                ),
                News(
                    2,
                    "تحديثات نظام الروضة",
                    "تم إضافة مميزات جديدة لنظام إدارة الحضور والغياب.",
                    "الدعم الفني",
                    true,
                    "Mohanned",
                    "24 مارس"
                )
            ),
            isLoading = false
        )
    }
}
