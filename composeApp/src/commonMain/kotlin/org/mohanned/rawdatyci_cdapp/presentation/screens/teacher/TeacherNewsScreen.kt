package org.mohanned.rawdatyci_cdapp.presentation.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mohanned.rawdatyci_cdapp.domain.model.News
import org.mohanned.rawdatyci_cdapp.presentation.components.EmptyState
import org.mohanned.rawdatyci_cdapp.presentation.components.GlassHeader
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyCard
import org.mohanned.rawdatyci_cdapp.presentation.components.SectionHeader
import org.mohanned.rawdatyci_cdapp.presentation.theme.AmberPrimary
import org.mohanned.rawdatyci_cdapp.presentation.theme.AppBg
import org.mohanned.rawdatyci_cdapp.presentation.theme.BlueDark
import org.mohanned.rawdatyci_cdapp.presentation.theme.BlueLight
import org.mohanned.rawdatyci_cdapp.presentation.theme.BluePrimary
import org.mohanned.rawdatyci_cdapp.presentation.theme.CairoFontFamily
import org.mohanned.rawdatyci_cdapp.presentation.theme.Gray200
import org.mohanned.rawdatyci_cdapp.presentation.theme.Gray300
import org.mohanned.rawdatyci_cdapp.presentation.theme.Gray400
import org.mohanned.rawdatyci_cdapp.presentation.theme.Gray500
import org.mohanned.rawdatyci_cdapp.presentation.theme.RawdatyGradients
import org.mohanned.rawdatyci_cdapp.presentation.theme.RawdatyTheme
import org.mohanned.rawdatyci_cdapp.presentation.theme.White

@Composable
fun TeacherNewsScreen(
    news: List<News>,
    isLoading: Boolean,
    onNewsClick: (News) -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        containerColor = AppBg,
        topBar = {
            GlassHeader(
                title = "مركز الأخبار والإعلانات",
                onBack = onBack,
                gradient = RawdatyGradients.HeroBlue,
                headerHeight = 140.dp
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BluePrimary)
            }
            return@Scaffold
        }

        if (news.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.Newspaper,
                title = "لا توجد أخبار جديدة",
                subtitle = "سيتم عرض الإعلانات والأخبار الرسمية هنا فور نشرها."
            )
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Pinned News Section
            val pinned = news.filter { it.isPinned }
            if (pinned.isNotEmpty()) {
                item {
                    SectionHeader("إعلانات هامة")
                }
                items(pinned, key = { "pinned_${it.id}" }) { item ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .clickable { onNewsClick(item) },
                        color = BlueDark,
                    ) {
                        Box {
                            // Subtle background pattern or gradient overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(BlueDark, BluePrimary.copy(0.7f)),
                                            start = Offset.Zero,
                                            end = Offset.Infinite
                                        )
                                    )
                            )

                            Column(
                                modifier = Modifier.padding(24.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Icon(
                                        Icons.Default.PushPin,
                                        null,
                                        tint = AmberPrimary,
                                        modifier = Modifier.size(16.dp),
                                    )
                                    Text(
                                        "إعلان مثبت",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AmberPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = CairoFontFamily
                                    )
                                }
                                Text(
                                    item.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = White,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = CairoFontFamily
                                )
                                Text(
                                    item.body,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = White.copy(0.8f),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    fontFamily = CairoFontFamily,
                                    lineHeight = 22.sp
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Schedule,
                                        null,
                                        tint = White.copy(0.6f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        item.createdAt,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = White.copy(0.6f),
                                        fontFamily = CairoFontFamily
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // News Feed Section
            val regular = news.filter { !it.isPinned }
            if (regular.isNotEmpty()) {
                item {
                    SectionHeader("آخر المستجدات")
                }
                items(regular, key = { "regular_${it.id}" }) { item ->
                    RawdatyCard(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier.clickable { onNewsClick(item) },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(BlueLight.copy(0.3f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    Icons.Outlined.Description,
                                    null,
                                    tint = BluePrimary,
                                    modifier = Modifier.size(32.dp),
                                )
                            }
                            Column(
                                Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    item.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = BlueDark,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = CairoFontFamily,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    item.body,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Gray500,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontFamily = CairoFontFamily
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.PersonOutline,
                                        null,
                                        tint = Gray400,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        item.authorName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Gray400,
                                        fontFamily = CairoFontFamily
                                    )
                                    Text("•", color = Gray200)
                                    Text(
                                        item.createdAt,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Gray400,
                                        fontFamily = CairoFontFamily
                                    )
                                }
                            }
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                null,
                                tint = Gray300,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun TeacherNewsPreview() {
    RawdatyTheme {
        val dummyNews = listOf(
            News(
                1,
                "بدء التسجيل للرحلة السنوية",
                "يسرنا إبلاغكم بفتح باب التسجيل للرحلة الميدانية السنوية إلى حديقة الحيوان لجميع الطلاب.",
                "سارة أحمد",
                false,
                "Moahnned Sohail",
                createdAt = "2024-03-24"
            ),
            News(
                2,
                "تغيير في مواعيد الحصص",
                "سيتم تأجيل حصة الرسم يوم غد لمدة ساعة واحدة نظراً لإقامة نشاط رياضي خارجي.",
                "إدارة الروضة",
                false,
                "Moahnned Sohail",
                createdAt = "2024-03-24"
            ),
            News(
                3,
                "توزيع شهادات التميز",
                "سيتم توزيع شهادات التميز على الطلاب المتفوقين في فصل النجوم يوم الخميس القادم.",
                "أ. نورة علي",
                false,
                "Moahnned Sohail",
                createdAt = "2024-03-24"
            )
        )
        TeacherNewsScreen(
            news = dummyNews,
            isLoading = false,
            onNewsClick = {},
            onBack = {},
        )
    }
}
