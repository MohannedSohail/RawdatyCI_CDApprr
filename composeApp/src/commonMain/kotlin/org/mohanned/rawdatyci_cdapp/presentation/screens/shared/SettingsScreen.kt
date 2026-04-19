package org.mohanned.rawdatyci_cdapp.presentation.screens.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyCard
import org.mohanned.rawdatyci_cdapp.presentation.components.SettingsRow
import org.mohanned.rawdatyci_cdapp.presentation.screens.auth.LoginScreen
import org.mohanned.rawdatyci_cdapp.presentation.screens.auth.ResetPasswordScreen
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ProfileEffect
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ProfileViewModel
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.SettingsIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.SettingsViewModel

object SettingsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val settingsViewModel: SettingsViewModel = koinViewModel()
        val profileViewModel: ProfileViewModel = koinViewModel()
        
        val settingsState by settingsViewModel.state.collectAsState()
        val profileState by profileViewModel.state.collectAsState()

        LaunchedEffect(Unit) {
            settingsViewModel.onIntent(SettingsIntent.Load)
            profileViewModel.onIntent(org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ProfileIntent.Load)
            profileViewModel.effect.collect { effect ->
                if (effect == ProfileEffect.NavigateToLogin) {
                    navigator.replaceAll(LoginScreen())
                }
            }
        }

        SettingsScreenContent(
            userName = profileState.name.ifEmpty { "المستخدم" },
            userRole = profileState.user?.role?.name?.lowercase() ?: "parent",
            isDarkMode = settingsState.isDarkMode,
            notificationsOn = settingsState.notificationsOn,
            language = settingsState.language,
            appVersion = settingsState.appVersion,
            onBack = { navigator.pop() },
            onEditProfile = { navigator.push(ProfileScreen) },
            onResetPassword = { navigator.push(ResetPasswordScreen("logged_in_user")) },
            onDarkModeToggle = { settingsViewModel.onIntent(SettingsIntent.ToggleDarkMode(it)) },
            onNotifToggle = { settingsViewModel.onIntent(SettingsIntent.ToggleNotifications(it)) },
            onLanguageClick = { },
            onPrivacyClick = { },
            onHelpClick = { },
            onLogout = { profileViewModel.onIntent(org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ProfileIntent.Logout) }
        )
    }
}

@Composable
fun SettingsScreenContent(
    userName: String,
    userRole: String,
    isDarkMode: Boolean,
    notificationsOn: Boolean,
    language: String,
    appVersion: String,
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onResetPassword: () -> Unit,
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
            Surface(color = White, shadowElevation = 4.dp) {
                Row(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(8.dp, 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = BlueDark) }
                    Spacer(Modifier.width(8.dp))
                    Text("الإعدادات والتفضيلات", style = MaterialTheme.typography.titleLarge, color = BlueDark, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
                }
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                RawdatyCard(onClick = onEditProfile, elevation = 4.dp, containerColor = White) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(Modifier.size(64.dp).clip(CircleShape).background(BlueLight.copy(0.5f)), contentAlignment = Alignment.Center) {
                            Text(userName.firstOrNull()?.toString() ?: "U", style = MaterialTheme.typography.headlineSmall, color = BluePrimary, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
                        }
                        Column(Modifier.weight(1f)) {
                            Text(userName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Gray800, fontFamily = CairoFontFamily)
                            Spacer(Modifier.height(4.dp))
                            Surface(color = BluePrimary.copy(0.1f), shape = RoundedCornerShape(100)) {
                                Text(if (userRole == "admin") "مدير الروضة" else if (userRole == "teacher") "معلم / معلمة" else "ولي أمر", modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = BluePrimary, fontFamily = CairoFontFamily)
                            }
                        }
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Gray300, modifier = Modifier.size(20.dp))
                    }
                }
            }

            item { SettingCategory("الحساب والأمان") }
            item {
                RawdatyCard(elevation = 2.dp, containerColor = White) {
                    Column {
                        SettingsRow(title = "تعديل الملف الشخصي", icon = Icons.Default.PersonOutline, iconColor = BluePrimary, onClick = onEditProfile)
                        SettingsRow(title = "تغيير كلمة المرور", icon = Icons.Default.Password, iconColor = BluePrimary, onClick = onResetPassword)
                        SettingsRow(title = "الخصوصية والأمان", icon = Icons.Default.Shield, iconColor = MintPrimary, onClick = onPrivacyClick)
                    }
                }
            }

            item { SettingCategory("تفضيلات التطبيق") }
            item {
                RawdatyCard(elevation = 2.dp, containerColor = White) {
                    Column {
                        SettingsRow(title = "تنبيهات الإشعارات", icon = Icons.Default.NotificationsNone, iconColor = AmberPrimary, onClick = {}, content = {
                            Switch(checked = notificationsOn, onCheckedChange = onNotifToggle, colors = SwitchDefaults.colors(checkedTrackColor = BluePrimary))
                        }, showArrow = false)
                        SettingsRow(title = "لغة التطبيق", icon = Icons.Default.Language, iconColor = BluePrimary, onClick = onLanguageClick, content = {
                            Text(language, style = MaterialTheme.typography.bodySmall, color = BluePrimary, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
                        })
                        SettingsRow(title = "الوضع الليلي", icon = Icons.Default.DarkMode, iconColor = Gray700, onClick = {}, content = {
                            Switch(checked = isDarkMode, onCheckedChange = onDarkModeToggle, colors = SwitchDefaults.colors(checkedTrackColor = BluePrimary))
                        }, showArrow = false)
                    }
                }
            }

            item {
                Button(onClick = onLogout, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color(0xFFD32F2F)), modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp)) {
                    Icon(Icons.AutoMirrored.Filled.Logout, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("تسجيل الخروج", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
                }
            }
        }
    }
}

@Composable
private fun SettingCategory(label: String) {
    Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black, color = BlueDark.copy(0.6f), modifier = Modifier.padding(start = 8.dp, bottom = 4.dp, top = 8.dp), fontFamily = CairoFontFamily)
}
