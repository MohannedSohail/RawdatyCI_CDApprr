package org.mohanned.rawdatyci_cdapp.presentation.screens.shared

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import org.mohanned.rawdatyci_cdapp.domain.model.News
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

@Composable
fun NewsDetailScreen(
    news: News,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onBookmark: () -> Unit,
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = AppBg,
        topBar = {
            GlassHeader(
                title = "تفاصيل الخبر",
                onBack = onBack,
                actions = {
                    IconButton(onClick = onBookmark) {
                        Icon(Icons.Outlined.BookmarkBorder, null, tint = White)
                    }
                    IconButton(onClick = onShare) {
                        Icon(Icons.Outlined.Share, null, tint = White)
                    }
                },
                gradient = RawdatyGradients.HeroBlue,
                headerHeight = 140.dp
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Premium Hero Visual Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(RawdatyGradients.HeroBlue)
            ) {
                // Background Decorative Circles
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = White.copy(alpha = 0.05f),
                        radius = size.minDimension / 1.2f,
                        center = Offset(size.width * 0.1f, size.height * 0.2f)
                    )
                }

                if (news.isPinned) {
                    Surface(
                        modifier = Modifier
                            .padding(20.dp)
                            .align(Alignment.TopStart),
                        color = AmberPrimary,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.PushPin, null, tint = White, modifier = Modifier.size(14.dp))
                            Text(
                                "إعلان مثبت", 
                                style = MaterialTheme.typography.labelSmall, 
                                color = White, 
                                fontWeight = FontWeight.Bold,
                                fontFamily = CairoFontFamily
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = White.copy(0.15f),
                        border = BorderStroke(1.dp, White.copy(0.2f))
                    ) {
                        Icon(
                            Icons.Outlined.Newspaper,
                            null,
                            tint = White,
                            modifier = Modifier.padding(20.dp).size(40.dp)
                        )
                    }
                }

                // Smooth Bottom Gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Black.copy(0.3f)),
                                startY = 150f
                            )
                        )
                )
            }

            // News Content Card
            Surface(
                modifier = Modifier
                    .offset(y = (-32).dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
                color = White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Category & Meta Data
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = BlueLight.copy(0.4f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "الأخبار الرسمية", 
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall, 
                                color = BluePrimary, 
                                fontWeight = FontWeight.Black,
                                fontFamily = CairoFontFamily
                            )
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Outlined.Schedule, null, tint = Gray400, modifier = Modifier.size(16.dp))
                            Text(
                                news.createdAt, 
                                style = MaterialTheme.typography.labelSmall, 
                                color = Gray500,
                                fontFamily = CairoFontFamily
                            )
                        }
                    }

                    // Headline
                    Text(
                        news.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = BlueDark,
                        lineHeight = 36.sp,
                        fontFamily = CairoFontFamily
                    )

                    // Author Professional Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Gray100,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            RawdatyAvatar(news.authorName, size = 48.dp, gradient = RawdatyGradients.AvatarBlue)
                            Column {
                                Text(
                                    news.authorName, 
                                    style = MaterialTheme.typography.titleSmall, 
                                    fontWeight = FontWeight.Bold, 
                                    color = BlueDark,
                                    fontFamily = CairoFontFamily
                                )
                                Text(
                                    "إدارة روضتي - قسم الإعلانات", 
                                    style = MaterialTheme.typography.labelSmall, 
                                    color = Gray500,
                                    fontFamily = CairoFontFamily
                                )
                            }
                        }
                    }

                    RawdatyDivider()

                    // Detailed Body Text
                    Text(
                        text = news.body,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Gray700,
                        lineHeight = 32.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = CairoFontFamily
                    )

                    Spacer(Modifier.height(40.dp))

                    // Final Affirmation Button
                    RawdatyButton(
                        text = "قرأت الخبر وتفاصيله",
                        onClick = onBack,
                        icon = Icons.Outlined.DoneAll,
                        backgroundColor = BluePrimary,
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    )
                    
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Preview
@Composable
fun NewsDetailPreview() {
    RawdatyTheme {
        NewsDetailScreen(
            news = News(
                id = 1,
                title = "تعديل في جدول الحصص للمرحلة التأسيسية",
                body = "نود إحاطتكم علماً بأنه قد تم إجراء بعض التعديلات الطفيفة على جدول الحصص اليومي.\n\nيرجى من جميع أولياء الأمور الكرام التواجد لاستلام أطفالهم في تمام الساعة ١٢:٣٠ ظهراً بدلاً من ١:٠٠ ظهراً لهذا الأسبوع فقط.\n\nنشكر لكم حسن تعاونكم الدائم معنا.",
                imageUrl = null,
                isPinned = true,
                authorName = "أ. سارة محمد",
                createdAt = "٥ مارس ٢٠٢٤"
            ),
            onBack = {},
            onShare = {},
            onBookmark = {}
        )
    }
}
