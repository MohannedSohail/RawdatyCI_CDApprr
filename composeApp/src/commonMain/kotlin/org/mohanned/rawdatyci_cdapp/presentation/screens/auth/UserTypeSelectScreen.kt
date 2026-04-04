package org.mohanned.rawdatyci_cdapp.presentation.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

@Composable
fun UserTypeSelectScreen(
    onTypeSelected: (String) -> Unit = {},
) {
    Box(modifier = Modifier.fillMaxSize().background(White)) {
        // Premium background decoration
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = BlueLight.copy(0.15f), 
                radius = 350.dp.toPx(), 
                center = Offset(size.width * 1.1f, size.height * -0.2f)
            )
            drawCircle(
                color = MintLight.copy(0.12f), 
                radius = 300.dp.toPx(), 
                center = Offset(size.width * -0.1f, size.height * 0.9f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(80.dp))
            
            AnimateEntrance(delay = 100) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(Gray50)
                            .border(1.dp, Gray200, RoundedCornerShape(32.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        RawdatyLogo(
                            modifier = Modifier.fillMaxSize().padding(8.dp)
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "مرحباً بك في رَوْضَتِي", 
                            style = MaterialTheme.typography.displaySmall, 
                            color = BlueDark, 
                            fontWeight = FontWeight.Black,
                            fontFamily = CairoFontFamily
                        )
                        Text(
                            "يُرجى تحديد نوع الحساب المسجل به في النظام", 
                            style = MaterialTheme.typography.bodyLarge, 
                            color = Gray500,
                            fontFamily = CairoFontFamily,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(Modifier.height(56.dp))

            // Roles Container
            AnimateEntrance(delay = 300) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    RoleCard(
                        "ولي أمر طالب", 
                        "متابعة الأبناء، والنشاطات، والرسوم، والتواصل", 
                        Icons.Outlined.FamilyRestroom, 
                        BluePrimary
                    ) { onTypeSelected("Parent") }
                    
                    RoleCard(
                        "معلم / مشرفة", 
                        "إدارة الحضور، النشاطات اليومية، والدردشة", 
                        Icons.Outlined.School, 
                        MintPrimary
                    ) { onTypeSelected("Teacher") }
                    
                    RoleCard(
                        "إدارة المؤسسة", 
                        "تحكم كامل، تقارير، إدارة الحسابات المالية", 
                        Icons.Outlined.AdminPanelSettings, 
                        AmberPrimary
                    ) { onTypeSelected("Admin") }
                }
            }

            Spacer(Modifier.height(48.dp))
            
            Text(
                "تواجه مشكلة؟ تواصل مع الدعم الفني",
                style = MaterialTheme.typography.labelMedium,
                color = BluePrimary,
                fontWeight = FontWeight.Bold,
                fontFamily = CairoFontFamily,
                modifier = Modifier.padding(bottom = 24.dp).clickable { /* Help */ }
            )
        }
    }
}

@Composable
private fun RoleCard(
    title: String, 
    desc: String, 
    icon: ImageVector, 
    accentColor: Color, 
    onClick: () -> Unit
) {
    RawdatyCard(
        modifier = Modifier.fillMaxWidth(), 
        onClick = onClick, 
        containerColor = White,
        elevation = 2.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Role Icon with premium stylized background
            Surface(
                modifier = Modifier.size(72.dp),
                shape = RoundedCornerShape(20.dp),
                color = accentColor.copy(0.12f),
                border = BorderStroke(1.dp, accentColor.copy(0.08f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon, 
                        null, 
                        tint = accentColor, 
                        modifier = Modifier.size(36.dp) // Optimized icon size
                    )
                }
            }

            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    title, 
                    style = MaterialTheme.typography.titleMedium, 
                    color = BlueDark, 
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = CairoFontFamily
                )
                Text(
                    desc, 
                    style = MaterialTheme.typography.labelSmall, 
                    color = Gray500, 
                    lineHeight = 18.sp,
                    fontFamily = CairoFontFamily,
                    maxLines = 2
                )
            }

            // High-fidelity arrow indicator
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft, 
                null, 
                tint = Gray300, 
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
@Preview
fun UserTypeSelectPreview() {
    RawdatyTheme {
        UserTypeSelectScreen()
    }
}
