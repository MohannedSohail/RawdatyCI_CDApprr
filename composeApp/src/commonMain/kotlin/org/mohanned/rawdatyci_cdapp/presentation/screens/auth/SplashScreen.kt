package org.mohanned.rawdatyci_cdapp.presentation.screens.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyLogo
import org.mohanned.rawdatyci_cdapp.presentation.navigation.roleToHome
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AuthEffect
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AuthViewModel

object SplashScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: AuthViewModel = koinViewModel()
        
        SplashScreenContent(
            onFinished = {
                viewModel.checkSession()
            }
        )

        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is AuthEffect.NavigateToDashboard -> {
                        navigator.replaceAll(roleToHome(effect.role))
                    }
                    AuthEffect.NavigateToOnboarding -> {
                        navigator.replaceAll(OnboardingScreen)
                    }
                    else -> { }
                }
            }
        }
    }
}

@Composable
fun SplashScreenContent(onFinished: () -> Unit = {}) {
    var startAnimations by remember { mutableStateOf(false) }

    // ✅ تحويل الأنيميشن ليعمل لمرة واحدة فقط عند تغيير startAnimations
    val pulseScale by animateFloatAsState(
        targetValue = if (startAnimations) 1.6f else 1f,
        animationSpec = tween(2500, easing = FastOutSlowInEasing),
        label = "pulseScale"
    )

    val pulseAlpha by animateFloatAsState(
        targetValue = if (startAnimations) 0f else 0.5f,
        animationSpec = tween(2500, easing = FastOutSlowInEasing),
        label = "pulseAlpha"
    )
    
    val logoScale by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow),
        label = "logoScale"
    )
    
    val contentAlpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(1200),
        label = "contentAlpha"
    )

    LaunchedEffect(Unit) {
        delay(200)
        startAnimations = true
        delay(2800)
        onFinished()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(RawdatyGradients.Splash),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2 - 50.dp.toPx())
            drawCircle(
                color = White.copy(alpha = pulseAlpha * 0.4f),
                radius = 110.dp.toPx() * pulseScale,
                center = center,
                style = Stroke(width = 3.dp.toPx())
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp).offset(y = (-50).dp)
        ) {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .scale(logoScale)
                    .clip(CircleShape)
                    .background(White.copy(alpha = 0.2f))
                    .border(3.dp, White.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                RawdatyLogo(
                    modifier = Modifier.size(150.dp).clip(CircleShape),
                    color = White
                )
            }

            Spacer(Modifier.height(40.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(contentAlpha)
            ) {
                Text(
                    text = "رَوْضَتِي",
                    style = MaterialTheme.typography.displayMedium,
                    color = White,
                    fontWeight = FontWeight.Black,
                    fontFamily = CairoFontFamily,
                    letterSpacing = 3.sp
                )
                Text(
                    text = "رعاية متميزة لجيل واعد",
                    style = MaterialTheme.typography.titleMedium,
                    color = White.copy(alpha = 0.8f),
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp)
                .alpha(contentAlpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = White,
                strokeWidth = 3.dp,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "جاري تحضير عالمك الممتع...",
                color = White.copy(alpha = 0.7f),
                fontSize = 11.sp,
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
