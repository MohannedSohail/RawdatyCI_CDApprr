package org.mohanned.rawdatyci_cdapp.presentation.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.mohanned.rawdatyci_cdapp.presentation.components.AnimateEntrance
import org.mohanned.rawdatyci_cdapp.presentation.components.ModernHeader
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

data class ParentDetailContentScreen(
    val title: String,
    val body: String,
    val author: String?,
    val date: String,
    val type: String,
    val icon: ImageVector
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            containerColor = AppBg,
            topBar = {
                ModernHeader(
                    title = if (type == "complaint") "تفاصيل الطلب" else "تفاصيل الخبر",
                    onBack = { navigator.pop() },
                    gradient = if (type == "announcement") RawdatyGradients.AvatarAmber else RawdatyGradients.HeroBlue
                )
            }
        ) { padding ->
            // ✅ أنيميشن الدخول في كل شاشة
            AnimateEntrance {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // شارة النوع
                    Surface(
                        color = (if (type == "announcement") AmberPrimary else BluePrimary).copy(0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = type.uppercase(),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            color = if (type == "announcement") AmberPrimary else BluePrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = CairoFontFamily
                        )
                    }

                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = BlueDark,
                        fontFamily = CairoFontFamily,
                        lineHeight = 34.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        InfoChip(Icons.Default.Person, author ?: "الإدارة")
                        InfoChip(Icons.Default.CalendarMonth, date)
                    }

                    Divider(color = Gray100)

                    Text(
                        text = body,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Gray700,
                        fontFamily = CairoFontFamily,
                        lineHeight = 28.sp
                    )
                    
                    Spacer(Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
private fun InfoChip(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            modifier = Modifier.size(24.dp).clip(CircleShape).background(Gray50),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = Gray400, modifier = Modifier.size(14.dp))
        }
        Text(text, color = Gray500, fontSize = 12.sp, fontFamily = CairoFontFamily)
    }
}
