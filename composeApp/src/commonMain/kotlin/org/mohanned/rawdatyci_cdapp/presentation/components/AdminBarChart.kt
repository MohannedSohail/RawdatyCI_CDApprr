package org.mohanned.rawdatyci_cdapp.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

@Composable
fun AdminBarChart(
    data: List<Pair<String, Int>>,
    modifier: Modifier = Modifier,
    height: Dp = 200.dp,
    barColor: Color = BluePrimary
) {
    val maxValue = data.maxOfOrNull { it.second }?.toFloat() ?: 1f
    
    // Animation state
    var animationPlayed by remember { mutableStateOf(false) }
    val animateProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    RawdatyCard(
        modifier = modifier.fillMaxWidth(),
        containerColor = White,
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "نظرة إحصائية شاملة",
                style = MaterialTheme.typography.titleSmall,
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                color = Gray800
            )
            Spacer(Modifier.height(24.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.Bottom
            ) {
                data.forEach { (label, value) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Canvas(modifier = Modifier.fillMaxWidth(0.4f).fillMaxHeight((value / maxValue) * animateProgress)) {
                                drawRoundRect(
                                    color = barColor,
                                    size = size,
                                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            value.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = Gray900,
                            fontFamily = CairoFontFamily
                        )
                        Text(
                            label,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = Gray400,
                            fontFamily = CairoFontFamily
                        )
                    }
                }
            }
        }
    }
}
