package org.mohanned.rawdatyci_cdapp.presentation.screens.shared

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

@Composable
fun SettingsScreen(
    userName: String,
    userRole: String,
    isDarkMode: Boolean,
    notificationsOn: Boolean,
    language: String,
    appVersion: String,
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onDarkModeToggle: (Boolean) -> Unit,
    onNotifToggle: (Boolean) -> Unit,
    onLanguageClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onHelpClick: () -> Unit,
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
                        "الإعدادات والتفضيلات",
                        style = MaterialTheme.typography.titleLarge,
                        color = BlueDark,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CairoFontFamily
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Overview Card
            item {
                RawdatyCard(onClick = onEditProfile, elevation = 4.dp) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(Modifier.size(64.dp).clip(CircleShape).background(BlueLight.copy(0.5f)), contentAlignment = Alignment.Center) {
                            Text(
                                userName.firstOrNull()?.toString() ?: "U",
                                style = MaterialTheme.typography.headlineSmall,
                                color = BluePrimary,
                                fontWeight = FontWeight.Bold,
                                fontFamily = CairoFontFamily
                            )
                        }
                        Column(Modifier.weight(1f)) {
                            Text(
                                userName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Gray800,
                                fontFamily = CairoFontFamily
                            )
                            Spacer(Modifier.height(4.dp))
                            Surface(
                                color = BluePrimary.copy(0.1f),
                                shape = RoundedCornerShape(100)
                            ) {
                                Text(
                                    when(userRole) {
                                        "admin" -> "مدير الروضة"
                                        "teacher" -> "معلم / معلمة"
                                        else -> "ولي أمر"
                                    },
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BluePrimary,
                                    fontFamily = CairoFontFamily
                                )
                            }
                        }
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Gray300, modifier = Modifier.size(20.dp))
                    }
                }
            }

            // Category: Account & Security
            item { SettingCategory("الحساب والأمان") }
            item {
                RawdatyCard(elevation = 2.dp) {
                    Column {
                        SettingsRow(
                            title = "تعديل الملف الشخصي",
                            icon = Icons.Default.PersonOutline,
                            iconColor = BluePrimary,
                            onClick = onEditProfile
                        )
                        HorizontalDivider(color = Gray50)
                        SettingsRow(
                            title = "تغيير كلمة المرور",
                            icon = Icons.Default.Password,
                            iconColor = BluePrimary,
                            onClick = { /* Open Change Pass */ }
                        )
                        HorizontalDivider(color = Gray50)
                        SettingsRow(
                            title = "الخصوصية والأمان",
                            icon = Icons.Default.Shield,
                            iconColor = MintPrimary,
                            onClick = onPrivacyClick
                        )
                    }
                }
            }

            // Category: App Preferences
            item { SettingCategory("تفضيلات التطبيق") }
            item {
                RawdatyCard(elevation = 2.dp) {
                    Column {
                        SettingsRow(
                            title = "تنبيهات الإشعارات",
                            icon = Icons.Default.NotificationsNone,
                            iconColor = AmberPrimary,
                            onClick = {},
                            content = {
                                Switch(
                                    checked = notificationsOn,
                                    onCheckedChange = onNotifToggle,
                                    colors = SwitchDefaults.colors(
                                        checkedTrackColor = BluePrimary,
                                        checkedThumbColor = White
                                    )
                                )
                            },
                            showArrow = false
                        )
                        HorizontalDivider(color = Gray50)
                        SettingsRow(
                            title = "لغة التطبيق",
                            icon = Icons.Default.Language,
                            iconColor = BluePrimary,
                            onClick = onLanguageClick,
                            content = {
                                Text(
                                    language,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BluePrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = CairoFontFamily
                                )
                            }
                        )
                        HorizontalDivider(color = Gray50)
                        SettingsRow(
                            title = "الوضع الليلي (Dark Mode)",
                            icon = Icons.Default.DarkMode,
                            iconColor = Gray700,
                            onClick = {},
                            content = {
                                Switch(
                                    checked = isDarkMode,
                                    onCheckedChange = onDarkModeToggle,
                                    colors = SwitchDefaults.colors(
                                        checkedTrackColor = BluePrimary,
                                        checkedThumbColor = White
                                    )
                                )
                            },
                            showArrow = false
                        )
                    }
                }
            }

            // Category: About & Support
            item { SettingCategory("حول التطبيق") }
            item {
                RawdatyCard(elevation = 2.dp) {
                    Column {
                        SettingsRow(
                            title = "مركز المساعدة والدعم",
                            icon = Icons.Default.SupportAgent,
                            iconColor = BluePrimary.copy(alpha = 0.7f),
                            onClick = onHelpClick
                        )
                        HorizontalDivider(color = Gray50)
                        SettingsRow(
                            title = "سياسة الخصوصية",
                            icon = Icons.Default.Policy,
                            iconColor = Gray500,
                            onClick = { /* Policy */ }
                        )
                        HorizontalDivider(color = Gray50)
                        SettingsRow(
                            title = "إصدار التطبيق",
                            icon = Icons.Default.Info,
                            iconColor = Gray400,
                            onClick = {},
                            content = {
                                Text(
                                    appVersion,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Gray400,
                                    fontFamily = CairoFontFamily
                                )
                            },
                            showArrow = false
                        )
                    }
                }
            }

            // Logout Button
            item {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFEBEE),
                        contentColor = Color(0xFFD32F2F)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "تسجيل الخروج",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CairoFontFamily
                    )
                }
            }

            item {
                Text(
                    "صنع بكل حب في روضتي © 2024",
                    style = MaterialTheme.typography.labelSmall,
                    color = Gray400,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    textAlign = TextAlign.Center,
                    fontFamily = CairoFontFamily
                )
            }
        }
    }
}

@Composable
private fun SettingCategory(label: String) {
    Text(
        label,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Black,
        color = BlueDark.copy(0.6f),
        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp, top = 8.dp),
        fontFamily = CairoFontFamily
    )
}

@Preview
@Composable
fun SettingsPreview() {
    RawdatyTheme {
        SettingsScreen(
            userName = "محمد العلي",
            userRole = "parent",
            isDarkMode = false,
            notificationsOn = true,
            language = "العربية",
            appVersion = "2.4.0",
            onBack = {},
            onEditProfile = {},
            onDarkModeToggle = {},
            onNotifToggle = {},
            onLanguageClick = {},
            onPrivacyClick = {},
            onHelpClick = {},
            onLogout = {}
        )
    }
}
