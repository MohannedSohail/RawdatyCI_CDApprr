package org.mohanned.rawdatyci_cdapp.presentation.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.FamilyRestroom
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.mohanned.rawdatyci_cdapp.presentation.components.AnimateEntrance
import org.mohanned.rawdatyci_cdapp.presentation.components.OnBoardingHeader
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyCard
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyLogo
import org.mohanned.rawdatyci_cdapp.presentation.components.WaveHeader
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

object UserTypeSelectScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        UserTypeSelectScreenContent(
            onTypeSelected = { role ->
                navigator.push(LoginScreen(role = role))
            }
        )
    }
}

@Composable
fun UserTypeSelectScreenContent(
    onTypeSelected: (String) -> Unit = {},
) {
    Scaffold(containerColor = White) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .verticalScroll(rememberScrollState()),
        ) {
            OnBoardingHeader(
                title = "مرحباً بك في رَوْضَتِي",
                subtitle = "يُرجى تحديد نوع الحساب للمتابعة",
                gradient = RawdatyGradients.Splash,
                headerHeight = 260.dp
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .background(White.copy(0.15f))
                        .border(1.dp, White.copy(0.2f), RoundedCornerShape(25.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    RawdatyLogo(
                        modifier = Modifier.size(85.dp).clip(RoundedCornerShape(25.dp)),
                        color = White
                    )
                }
            }


            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .offset(y = (-30).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        AnimateEntrance(delay = 200){
                        RoleCard(
                            "ولي أمر طالب",
                            "متابعة الأبناء، والنشاطات، والرسوم، والتواصل",
                            Icons.Outlined.FamilyRestroom,
                            BluePrimary
                        ) { onTypeSelected("Parent") }}
                        AnimateEntrance(delay = 300){
                        RoleCard(
                            "معلم / مشرفة",
                            "إدارة الحضور، النشاطات اليومية، والدردشة",
                            Icons.Outlined.School,
                            MintPrimary
                        ) { onTypeSelected("Teacher") }}

                        AnimateEntrance(delay = 400){
                        RoleCard(
                            "إدارة المؤسسة",
                            "تحكم كامل، تقارير، إدارة الحسابات المالية",
                            Icons.Outlined.AdminPanelSettings,
                            AmberPrimary
                        ) { onTypeSelected("Admin") }}
                    }


                Spacer(Modifier.height(48.dp))
//
//                AnimateEntrance(delay = 600) {
//                    TextButton(onClick = { /* Help */ }) {
//                        Text(
//                            "تواجه مشكلة؟ تواصل مع الدعم الفني",
//                            style = MaterialTheme.typography.labelLarge,
//                            color = BluePrimary,
//                            fontWeight = FontWeight.Bold,
//                            fontFamily = CairoFontFamily
//                        )
//                    }
//                }
//
//                Spacer(Modifier.height(40.dp))
            }
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
                        modifier = Modifier.size(36.dp)
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

            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                null,
                tint = Gray300,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
