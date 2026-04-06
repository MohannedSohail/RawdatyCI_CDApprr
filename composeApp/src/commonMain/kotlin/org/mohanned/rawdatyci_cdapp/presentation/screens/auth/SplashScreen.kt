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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import rawdatyci_cdapp.composeapp.generated.resources.Res
import rawdatyci_cdapp.composeapp.generated.resources.rawdatylogo
import kotlin.random.Random

@Composable
fun SplashScreen(onFinished: () -> Unit = {}) {

    // ── مراحل الأنيميشن ─────────────────────────────────────────
    // 0 = idle  |  1 = rings + logo  |  2 = text  |  3 = loader
    var phase by remember { mutableStateOf(0) }

    // ── الدائرتان ────────────────────────────────────────────────
    // كل دائرة لها scale منفصل يبدأ من 0 ويصل لـ 2.6 / 3.2
    val ring1Scale by animateFloatAsState(
        targetValue   = if (phase >= 1) 2.6f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            easing         = CubicBezierEasing(0.2f, 0f, 0.4f, 1f)
        ),
        label = "ring1Scale"
    )
    val ring1Alpha by animateFloatAsState(
        targetValue   = if (phase >= 1) 0f else 0f, // تبدأ 0.9 ثم تختفي
        animationSpec = tween(1000),
        label = "ring1Alpha"
    )

    val ring2Scale by animateFloatAsState(
        targetValue   = if (phase >= 1) 3.2f else 0f,
        animationSpec = tween(
            durationMillis = 1300,
            delayMillis    = 200,              // تتأخر 200ms عن الأولى
            easing         = CubicBezierEasing(0.2f, 0f, 0.4f, 1f)
        ),
        label = "ring2Scale"
    )

    // ── الشعار ────────────────────────────────────────────────────
    val logoScale by animateFloatAsState(
        targetValue   = if (phase >= 1) 1f else 0.15f,
        animationSpec = spring(
            dampingRatio = 0.75f,               // overshoot خفيف
            stiffness    = Spring.StiffnessMediumLow
        ),
        label = "logoScale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue   = if (phase >= 1) 1f else 0f,
        animationSpec = tween(400),
        label = "logoAlpha"
    )

    // ── النصوص (slide-up منفصل لكل عنصر) ────────────────────────
    val nameOffset by animateFloatAsState(
        targetValue   = if (phase >= 2) 0f else 12f,
        animationSpec = tween(500, easing = EaseOutCubic),
        label = "nameOffset"
    )
    val nameAlpha by animateFloatAsState(
        targetValue   = if (phase >= 2) 1f else 0f,
        animationSpec = tween(500),
        label = "nameAlpha"
    )

    val tagOffset by animateFloatAsState(
        targetValue   = if (phase >= 2) 0f else 10f,
        animationSpec = tween(500, delayMillis = 150, easing = EaseOutCubic),
        label = "tagOffset"
    )
    val tagAlpha by animateFloatAsState(
        targetValue   = if (phase >= 2) 1f else 0f,
        animationSpec = tween(500, delayMillis = 150),
        label = "tagAlpha"
    )

    // ── مؤشر التحميل ─────────────────────────────────────────────
    val loaderAlpha by animateFloatAsState(
        targetValue   = if (phase >= 3) 1f else 0f,
        animationSpec = tween(400),
        label = "loaderAlpha"
    )

    // ── نقاط عائمة هادئة (أقل كثافة من الكود الأصلي) ────────────
    data class Dot(val x: Float, val y: Float, val r: Float, val phase: Float)
    val dots = remember {
        List(12) {
            Dot(
                x     = Random.nextFloat(),
                y     = Random.nextFloat(),
                r     = listOf(1.5f, 2.5f, 4f).random(),
                phase = Random.nextFloat()
            )
        }
    }
    val infinite  = rememberInfiniteTransition(label = "bg")
    val dotTick   by infinite.animateFloat(
        initialValue  = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing)),
        label = "dotTick"
    )

    // ── Timing المشروط ───────────────────────────────────────────
    LaunchedEffect(Unit) {
        phase = 1              // بدء الدوائر + الشعار فوراً
        delay(650)
        phase = 2              // ظهور الاسم والجملة
        delay(350)
        phase = 3              // ظهور مؤشر التحميل
        delay(1800)
        onFinished()
    }

    // ── UI ───────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        BlueDark,   // Blue-900
                        BluePrimary,   // Blue-600
                        MintDark   , // Mint-700
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        // الخلفية — نقاط عائمة هادئة
        Canvas(Modifier.fillMaxSize()) {
            dots.forEach { dot ->
                val p     = (dotTick + dot.phase) % 1f
                val yPos  = size.height * dot.y - size.height * 0.09f * p
                val alpha = ((1f - p) * 0.16f).coerceIn(0f, 1f)
                drawCircle(
                    color  = Color.White.copy(alpha = alpha),
                    radius = dot.r.dp.toPx(),
                    center = Offset(size.width * dot.x, yPos)
                )
            }
        }

        // الدائرتان — ترسمهما Canvas في نفس المركز
        Canvas(Modifier.fillMaxSize()) {
            val cx = size.width  / 2f
            val cy = size.height / 2f - 36.dp.toPx() // نفس إزاحة الشعار

            val baseRadius = 55.dp.toPx()  // نفس نصف قطر الشعار

            // الدائرة الأولى Mint-400
            val r1Alpha = ((1f - ring1Scale / 2.6f) * 0.9f).coerceIn(0f, 1f)
            drawCircle(
                color  = Color(0xFF4BAD73).copy(alpha = r1Alpha),
                radius = baseRadius * ring1Scale,
                center = Offset(cx, cy),
                style  = Stroke(width = 2.dp.toPx())
            )

            // الدائرة الثانية Mint-600
            val r2Alpha = ((1f - ring2Scale / 3.2f) * 0.7f).coerceIn(0f, 1f)
            drawCircle(
                color  = MintPrimary.copy(alpha = r2Alpha),
                radius = baseRadius * ring2Scale,
                center = Offset(cx, cy),
                style  = Stroke(width = 1.5.dp.toPx())
            )
        }

        // ── المحتوى المركزي ──────────────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier.offset(y = (-36).dp) // يرفع المجموعة للأعلى قليلاً
        ) {

            // الشعار — scale + alpha، بدون أي pulse
            Image(
                painter            = painterResource(Res.drawable.rawdatylogo),
                contentDescription = null,
                modifier           = Modifier
                    .size(200.dp)
                    .graphicsLayer {
                        scaleX = logoScale
                        scaleY = logoScale
                        alpha  = logoAlpha
                    }
                    .clip(CircleShape)
            )

            Spacer(Modifier.height(28.dp))

            // اسم التطبيق — بدون تشكيل إطلاقاً
            Text(
                text       = "روضتي",
                style      = MaterialTheme.typography.displaySmall,
                color      = Color(0xFF4BAD73),   // Mint-400
                fontWeight = FontWeight.Bold,
                fontFamily = CairoFontFamily,
                modifier   = Modifier.graphicsLayer {
                    alpha        = nameAlpha
                    translationY = nameOffset.dp.toPx()
                }
            )

            Spacer(Modifier.height(8.dp))

            // الجملة — اختر واحدة:
            Text(
                text = "طفلك بخير — وأنت على علم",
                // بديل 1: "كل خطوة طفلك.. في يدك"
                // بديل 2: "متابعة يومية.. راحة بال دائمة"
                style      = MaterialTheme.typography.bodyLarge,
                color      = BlueLight,   // Blue-200
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Normal,    // Normal — لا Medium
                modifier   = Modifier.graphicsLayer {
                    alpha        = tagAlpha
                    translationY = tagOffset.dp.toPx()
                }
            )
        }

        // مؤشر التحميل — أسفل الشاشة
        CircularProgressIndicator(
            color       = MintPrimary,
            strokeWidth = 3.dp,
            modifier    = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
                .size(30.dp)
                .alpha(loaderAlpha)
        )
    }
}private data class ParticleData(
    val x: Float = (0..100).random() / 100f,
    val yStart: Float = (5..95).random() / 100f,
    val size: Float = (1..4).random().toFloat(),
    val startTime: Float = (0..100).random() / 100f
)

@Preview
@Composable
fun SplashPreview() {
    RawdatyTheme {
        SplashScreen()
    }
}
