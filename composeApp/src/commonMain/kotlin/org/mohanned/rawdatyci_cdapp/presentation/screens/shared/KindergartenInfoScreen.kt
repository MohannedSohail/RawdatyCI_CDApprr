package org.mohanned.rawdatyci_cdapp.presentation.screens.shared

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import org.mohanned.rawdatyci_cdapp.domain.model.KindergartenSettings
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

@Composable
fun KindergartenInfoScreen(
    settings: KindergartenSettings?,
    isLoading: Boolean,
    onBack: () -> Unit,
    onCallClick: () -> Unit,
    onEmailClick: () -> Unit,
    onMapClick: () -> Unit,
) {
    Scaffold(
        containerColor = AppBg,
        topBar = {
            GlassHeader(
                title = "عن الروضة", 
                onBack = onBack,
                gradient = RawdatyGradients.HeroBlue,
                headerHeight = 140.dp
            )
        }
    ) { padding ->
        if (isLoading || settings == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BluePrimary)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Premium Brand Header
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = BlueDark
                ) {
                    Box {
                        // Background Decoration
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(
                                color = BluePrimary.copy(alpha = 0.2f),
                                radius = size.minDimension / 1.5f,
                                center = Offset(size.width, 0f)
                            )
                        }
                        
                        Column(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                modifier = Modifier.size(80.dp),
                                shape = CircleShape,
                                color = White.copy(0.1f),
                                border = BorderStroke(1.dp, White.copy(0.2f))
                            ) {
                                Icon(
                                    Icons.Default.School, 
                                    null, 
                                    tint = White, 
                                    modifier = Modifier.padding(20.dp).size(40.dp)
                                )
                            }
                            Spacer(Modifier.height(16.dp))
                            Text(
                                settings.name,
                                style = MaterialTheme.typography.headlineSmall,
                                color = White,
                                fontWeight = FontWeight.Black,
                                fontFamily = CairoFontFamily,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "بيئة تعليمية آمنة ومحفزة",
                                style = MaterialTheme.typography.bodySmall,
                                color = White.copy(0.7f),
                                fontFamily = CairoFontFamily
                            )
                        }
                    }
                }
            }

            // About Section
            if (settings.about != null) {
                item {
                    SectionHeader("رؤيتنا ورسالتنا")
                    RawdatyCard(elevation = 1.dp) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Outlined.Lightbulb, null, tint = AmberPrimary, modifier = Modifier.size(20.dp))
                                Text("نبذة عن الروضة", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = BlueDark, fontFamily = CairoFontFamily)
                            }
                            Text(
                                settings.about,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Gray700,
                                lineHeight = 26.sp,
                                fontFamily = CairoFontFamily
                            )
                        }
                    }
                }
            }

            // Contact Information
            item {
                SectionHeader("التواصل المباشر")
                RawdatyCard(elevation = 1.dp) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Phone
                        ContactRow(
                            icon = Icons.Outlined.Phone,
                            label = "رقم الهاتف والواتساب",
                            value = settings.phone ?: "غير متوفر",
                            iconColor = ColorSuccess,
                            onClick = onCallClick
                        )
                        RawdatyDivider()
                        // Email
                        ContactRow(
                            icon = Icons.Outlined.Mail,
                            label = "البريد الإلكتروني الرسمي",
                            value = settings.email ?: "غير متوفر",
                            iconColor = BluePrimary,
                            onClick = onEmailClick
                        )
                        RawdatyDivider()
                        // Address
                        ContactRow(
                            icon = Icons.Outlined.LocationOn,
                            label = "العنوان والموقع الجغرافي",
                            value = settings.address ?: "غير متوفر",
                            iconColor = ColorError,
                            onClick = onMapClick
                        )
                    }
                }
            }

            // Interactive Map Card
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .clickable(onClick = onMapClick),
                    color = Gray100,
                    border = BorderStroke(1.dp, Gray200)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        // Simulated map background (could be an image)
                        Icon(Icons.Default.Map, null, tint = BluePrimary.copy(0.1f), modifier = Modifier.size(200.dp))
                        
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Surface(
                                shape = CircleShape,
                                color = White,
                                shadowElevation = 4.dp
                            ) {
                                Icon(Icons.Default.Place, null, tint = ColorError, modifier = Modifier.padding(12.dp).size(24.dp))
                            }
                            Text(
                                "عرض الموقع على الخريطة",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = BluePrimary,
                                fontFamily = CairoFontFamily
                            )
                        }
                    }
                }
            }

            // Academic Year Info Footer
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MintLight.copy(0.3f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MintPrimary.copy(0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Stars, null, tint = MintPrimary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "العام الدراسي الحالي: ${settings.primaryColor}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MintPrimary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = CairoFontFamily
                        )
                    }
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ContactRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    iconColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = RoundedCornerShape(12.dp),
            color = iconColor.copy(0.1f)
        ) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.padding(12.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Gray500, fontFamily = CairoFontFamily)
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = BlueDark, fontFamily = CairoFontFamily)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = Gray300, modifier = Modifier.size(20.dp))
    }
}
