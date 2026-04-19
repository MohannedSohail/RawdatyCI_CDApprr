package org.mohanned.rawdatyci_cdapp.presentation.screens.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyLogo
import org.mohanned.rawdatyci_cdapp.presentation.navigation.roleToHome
import org.mohanned.rawdatyci_cdapp.presentation.theme.BlueDark
import org.mohanned.rawdatyci_cdapp.presentation.theme.BlueLight
import org.mohanned.rawdatyci_cdapp.presentation.theme.CairoFontFamily
import org.mohanned.rawdatyci_cdapp.presentation.theme.MintPrimary
import org.mohanned.rawdatyci_cdapp.presentation.theme.RawdatyGradients
import org.mohanned.rawdatyci_cdapp.presentation.theme.White
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AuthEffect
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AuthViewModel
import rawdatyci_cdapp.composeapp.generated.resources.Res
import rawdatyci_cdapp.composeapp.generated.resources.rawdatylogo
import kotlin.random.Random

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
    var phase by remember { mutableStateOf(0) }

    val ring1Scale by animateFloatAsState(
        targetValue = if (phase >= 1) 2.6f else 0f,
        animationSpec = tween(1000, easing = CubicBezierEasing(0.2f, 0f, 0.4f, 1f))
    )
    val ring2Scale by animateFloatAsState(
        targetValue = if (phase >= 1) 3.2f else 0f,
        animationSpec = tween(1300, delayMillis = 200, easing = CubicBezierEasing(0.2f, 0f, 0.4f, 1f))
    )
    val logoScale by animateFloatAsState(
        targetValue = if (phase >= 1) 1f else 0.15f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessMediumLow)
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (phase >= 1) 1f else 0f,
        animationSpec = tween(400)
    )
    val nameAlpha by animateFloatAsState(
        targetValue = if (phase >= 2) 1f else 0f,
        animationSpec = tween(500)
    )
    val tagAlpha by animateFloatAsState(
        targetValue = if (phase >= 2) 1f else 0f,
        animationSpec = tween(500, delayMillis = 150)
    )
    val loaderAlpha by animateFloatAsState(
        targetValue = if (phase >= 3) 1f else 0f,
        animationSpec = tween(400)
    )

    LaunchedEffect(Unit) {
        phase = 1
        delay(650)
        phase = 2
        delay(350)
        phase = 3
        delay(1800)
        onFinished()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(RawdatyGradients.Splash),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f - 36.dp.toPx()
            val baseRadius = 55.dp.toPx()

            val r1Alpha = ((1f - ring1Scale / 2.6f) * 0.9f).coerceIn(0f, 1f)
            drawCircle(
                color = Color(0xFF4BAD73).copy(alpha = r1Alpha),
                radius = baseRadius * ring1Scale,
                center = Offset(cx, cy),
                style = Stroke(width = 2.dp.toPx())
            )

            val r2Alpha = ((1f - ring2Scale / 3.2f) * 0.7f).coerceIn(0f, 1f)
            drawCircle(
                color = MintPrimary.copy(alpha = r2Alpha),
                radius = baseRadius * ring2Scale,
                center = Offset(cx, cy),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = (-36).dp)
        ) {

            Box(
                modifier = Modifier
                    .size(180.dp).graphicsLayer {
                        scaleX = logoScale
                        scaleY = logoScale
                        alpha = logoAlpha
                    }.clip(CircleShape)
                    .background(White.copy(0.15f))
                    .border(1.dp, White.copy(0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                RawdatyLogo(
                    modifier = Modifier.size(160.dp).clip(CircleShape),
                    color = White
                )
            }

//            Image(
//                painter = painterResource(Res.drawable.rawdatylogo),
//                contentDescription = null,
//                modifier = Modifier.size(160.dp).graphicsLayer {
//                    scaleX = logoScale
//                    scaleY = logoScale
//                    alpha = logoAlpha
//                }.clip(CircleShape)
//            )
            Spacer(Modifier.height(28.dp))
            Text(
                text = "رَوْضَتِي",
                style = MaterialTheme.typography.displaySmall,
                color = White,
                fontWeight = FontWeight.Bold,
                fontFamily = CairoFontFamily,
                modifier = Modifier.alpha(nameAlpha)
            )
            Spacer(Modifier.height(8.dp))
//            Text(
//                text = "طفلك بخير — وأنت على علم",
//                style = MaterialTheme.typography.bodyLarge,
//                color = BlueLight,
//                fontFamily = CairoFontFamily,
//                modifier = Modifier.alpha(tagAlpha)
//            )
        }

        CircularProgressIndicator(
            color = BlueDark,
            trackColor = White,
            strokeWidth = 3.dp,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp).size(40.dp).alpha(loaderAlpha)
        )
    }
}
