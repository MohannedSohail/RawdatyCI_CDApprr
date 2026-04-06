package org.mohanned.rawdatyci_cdapp.presentation.screens.shared

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import org.mohanned.rawdatyci_cdapp.domain.model.User
import org.mohanned.rawdatyci_cdapp.domain.model.UserRole
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

@Composable
fun ProfileScreen(
    user: User?,
    name: String,
    phone: String,
    isLoading: Boolean,
    isSaving: Boolean,
    onBack: () -> Unit,
    onSettingsClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSave: () -> Unit,
    onLogout: () -> Unit,
) {
    Scaffold(
        containerColor = AppBg,
        topBar = { 
            Surface(
                color = White,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(8.dp, 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = BlueDark)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "الملف الشخصي",
                        style = MaterialTheme.typography.titleLarge,
                        color = BlueDark,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CairoFontFamily
                    )
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, null, tint = BlueDark)
                    }
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { LoadingScreen() }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Hero Avatar Section with Premium Styling
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box {
                    Surface(
                        modifier = Modifier.size(126.dp),
                        shape = CircleShape,
                        color = White,
                        shadowElevation = 8.dp
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(4.dp)) {
                            RawdatyAvatar(
                                name = name,
                                size = 118.dp,
                                gradient = when (user?.role) {
                                    UserRole.ADMIN -> RawdatyGradients.HeroBlue
                                    UserRole.TEACHER -> RawdatyGradients.HeroMint
                                    else -> RawdatyGradients.AvatarAmber
                                }
                            )
                        }
                    }
                    
                    Surface(
                        onClick = { /* Camera Action */ },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(40.dp)
                            .offset(x = 4.dp, y = 4.dp),
                        shape = CircleShape,
                        color = White,
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CameraAlt, null, tint = BluePrimary, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        name.ifEmpty { "مستخدم روضتي" },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = Gray900,
                        fontFamily = CairoFontFamily
                    )
                    user?.let { role ->
                        RoleTag(
                            when(role.role) {
                                UserRole.ADMIN -> "المدير العام"
                                UserRole.TEACHER -> "معلمة الفصل"
                                else -> "ولي أمر"
                            },
                            useSmallText = false
                        )
                    }
                }
            }

            // Information Card: Personal Data
            RawdatyCard(elevation = 2.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PersonOutline, null, tint = BluePrimary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "البيانات الشخصية",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary,
                            fontFamily = CairoFontFamily
                        )
                    }
                    
                    RawdatyField(
                        value = name,
                        onValueChange = onNameChange,
                        label = "الاسم الكامل (بالعربي)",
                        leadingIcon = Icons.Default.Badge
                    )

                    RawdatyField(
                        value = user?.email ?: "",
                        onValueChange = {},
                        label = "البريد الإلكتروني",
                        leadingIcon = Icons.Default.AlternateEmail,
                        enabled = false
                    )

                    RawdatyField(
                        value = phone,
                        onValueChange = onPhoneChange,
                        label = "رقم جوال التواصل",
                        leadingIcon = Icons.Default.PhoneIphone
                    )
                }
            }

            // Information Card: Security & Additional
            RawdatyCard(elevation = 2.dp) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                        Icon(Icons.Default.Security, null, tint = BluePrimary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "الأمان واللغة",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary,
                            fontFamily = CairoFontFamily
                        )
                    }
                    
                    SettingsRow(
                        title = "تغيير كلمة المرور",
                        icon = Icons.Default.VpnKey,
                        onClick = { /* Change Pass */ },
                        subtitle = "تأمين حسابك بكلمة سر قوية",
                        iconColor = Gray700
                    )
                    RawdatyDivider()
                    SettingsRow(
                        title = "لغة واجهة التطبيق",
                        icon = Icons.Default.Language,
                        onClick = { /* Language */ },
                        subtitle = "اللغة الحالية: العربية",
                        iconColor = Gray700
                    )
                }
            }

            // Final Actions
            Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
                RawdatyButton(
                    text = "حفظ كافة التعديلات",
                    onClick = onSave,
                    isLoading = isSaving,
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.fillMaxWidth()
                )

                TextButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.textButtonColors(contentColor = ColorError),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "تسجيل الخروج من الحساب", 
                        style = MaterialTheme.typography.titleSmall, 
                        fontWeight = FontWeight.Bold,
                        fontFamily = CairoFontFamily
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ProfilePreview() {
    RawdatyTheme {
        ProfileScreen(
            user = null,
            name = "مهند سهيل",
            phone = "0501234567",
            isLoading = false,
            isSaving = false,
            onBack = {},
            onSettingsClick = {},
            onNameChange = {},
            onPhoneChange = {},
            onSave = {},
            onLogout = {}
        )
    }
}
