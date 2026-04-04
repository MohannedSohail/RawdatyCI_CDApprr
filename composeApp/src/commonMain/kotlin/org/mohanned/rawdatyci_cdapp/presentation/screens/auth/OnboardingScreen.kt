package org.mohanned.rawdatyci_cdapp.presentation.screens.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.mohanned.rawdatyci_cdapp.presentation.theme.AmberPrimary
import org.mohanned.rawdatyci_cdapp.presentation.theme.BlueDark
import org.mohanned.rawdatyci_cdapp.presentation.theme.BluePrimary
import org.mohanned.rawdatyci_cdapp.presentation.theme.CairoFontFamily
import org.mohanned.rawdatyci_cdapp.presentation.theme.Gray200
import org.mohanned.rawdatyci_cdapp.presentation.theme.Gray400
import org.mohanned.rawdatyci_cdapp.presentation.theme.Gray500
import org.mohanned.rawdatyci_cdapp.presentation.theme.MintPrimary
import org.mohanned.rawdatyci_cdapp.presentation.theme.RawdatyTheme
import org.mohanned.rawdatyci_cdapp.presentation.theme.White
import rawdatyci_cdapp.composeapp.generated.resources.Res
import rawdatyci_cdapp.composeapp.generated.resources.onboarding
import rawdatyci_cdapp.composeapp.generated.resources.onboarding1
import rawdatyci_cdapp.composeapp.generated.resources.onboarding2

@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit = {}
) {
    val pages = listOf(
        OnboardingPage(
            "تابعي طفلك لحظة بلحظة",
            "احصلي على تحديثات مباشرة عن نشاطات طفلك وصوره ووجباته اليومية بكل سهولة.",
            Res.drawable.onboarding,
            MintPrimary
        ),
        OnboardingPage(
            "تواصل آمن ومباشر",
            "دردشة مباشرة مع المعلمات وإدارة الروضة لضمان أفضل رعاية لطفلك في أي وقت.",
            Res.drawable.onboarding1,
            BluePrimary
        ),
        OnboardingPage(
            "فعاليات ومناسبات",
            "كوني دائماً على علم بالرحلات والحفلات والمناسبات القادمة في جدول الروضة الذكي.",
            Res.drawable.onboarding2,
            AmberPrimary
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Scaffold(containerColor = White) { _ ->
        Column(modifier = Modifier.fillMaxSize()) {
            // ── Interactive Illustration Area (60%) ──────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f)
                    .background(pages[pagerState.currentPage].color.copy(0.05f))
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { index ->
                    Box(
                        modifier = Modifier.fillMaxSize().padding(start = 10.dp, end = 10.dp, bottom = 10.dp, top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(pages[index].image),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .aspectRatio(0.85f)
                                .clip(RoundedCornerShape(24.dp)),
                            contentScale = ContentScale.FillBounds
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f),
                color = White,
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                shadowElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp, vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Premium Centered Progress Indicators
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 32.dp)
                    ) {
                        repeat(pages.size) { i ->
                            val active = pagerState.currentPage == i
                            val width by animateDpAsState(if (active) 32.dp else 10.dp)
                            val color by animateColorAsState(if (active) pages[i].color else Gray200)
                            Box(
                                modifier = Modifier
                                    .height(10.dp)
                                    .width(width)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                        }
                    }

                    // Dynamic Text Section
                    AnimatedContent(
                        targetState = pages[pagerState.currentPage],
                        transitionSpec = { fadeIn() togetherWith fadeOut() }
                    ) { page ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                page.title,
                                style = MaterialTheme.typography.headlineLarge,
                                color = BlueDark,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center,
                                fontFamily = CairoFontFamily
                            )
                            Text(
                                page.description,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Gray500,
                                textAlign = TextAlign.Center,
                                lineHeight = 28.sp,
                                fontFamily = CairoFontFamily
                            )
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    // ── Premium Footer Navigation ────────
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = onFinished) {
                            Text(
                                "تخطي الجولة",
                                color = Gray400,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                fontFamily = CairoFontFamily
                            )
                        }

                        Button(
                            onClick = {
                                if (pagerState.currentPage < pages.size - 1) {
                                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                                } else {
                                    onFinished()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = pages[pagerState.currentPage].color),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.height(56.dp).width(if (pagerState.currentPage == pages.size - 1) 160.dp else 120.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    if (pagerState.currentPage == pages.size - 1) "ابدأ الآن" else "التالي",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = CairoFontFamily,
                                    color = White
                                )
                                if (pagerState.currentPage < pages.size - 1) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = White, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class OnboardingPage(
    val title: String,
    val description: String,
    val image: DrawableResource,
    val color: Color
)

@Preview
@Composable
fun OnboardingPreview() {
    RawdatyTheme {
        OnboardingScreen()
    }
}
