package org.mohanned.rawdatyci_cdapp.presentation.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyLogo
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    var startAnim by remember { mutableStateOf(false) }
    
    // Smooth entry animations
    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0.6f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessLow)
    )
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(1500, easing = EaseOutQuart)
    )
    
    // Continuous premium pulse
    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Animated Particles for depth
    val particles = remember { List(20) { ParticleData() } }
    val particleAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing)
        )
    )

    LaunchedEffect(Unit) {
        startAnim = true
        delay(3200) // Optimal duration for splash
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F2A3E), // Deep Blue start
                        Color(0xFF1E4C6F), // Blue-600 middle
                        Color(0xFF256640)  // Dark Green end
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Floating premium particles (Bokeh effect)
        Canvas(modifier = Modifier.fillMaxSize()) {
            particles.forEach { p ->
                val progress = (particleAnim + p.startTime) % 1f
                val y = size.height * p.yStart - (size.height * 0.15f * progress)
                val alpha = (1f - progress) * 0.25f
                
                drawCircle(
                    color = White.copy(alpha = alpha),
                    radius = p.size.dp.toPx(),
                    center = Offset(size.width * p.x, y)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alphaAnim)
                .scale(scaleAnim * pulseScale)
        ) {
            // High-fidelity Logo with White tint for contrast
            Box(
                modifier = Modifier
                    .size(170.dp)
                    .clip(CircleShape)
                    .background(White.copy(0.12f))
                    .border(2.dp, White.copy(0.15f), CircleShape)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                RawdatyLogo(modifier = Modifier.fillMaxSize(), isWhite = true)
            }
            
            Spacer(Modifier.height(48.dp))
            
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "رَوْضَتِي", // Added Tashkeel for core branding as requested
                    style = MaterialTheme.typography.displayMedium,
                    color = MintPrimary, // Mint Green primary accent
                    fontWeight = FontWeight.Black,
                    fontFamily = CairoFontFamily,
                    letterSpacing = 1.sp
                )
                
                Text(
                    "التعليم بذكاء.. والتربية بحب",
                    style = MaterialTheme.typography.titleMedium,
                    color = White.copy(alpha = 0.85f),
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        // Polished Loading Indicator at bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = MintPrimary,
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )
            
            Text(
                "جاري تهيئة النظام...",
                style = MaterialTheme.typography.labelSmall,
                color = White.copy(0.6f),
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Light
            )
        }
    }
}

private data class ParticleData(
    val x: Float = (0..100).random() / 100f,
    val yStart: Float = (5..95).random() / 100f,
    val size: Float = (1..4).random().toFloat(),
    val startTime: Float = (0..100).random() / 100f
)

@Preview
@Composable
fun SplashPreview() {
    RawdatyTheme {
        SplashScreen({})
    }
}
