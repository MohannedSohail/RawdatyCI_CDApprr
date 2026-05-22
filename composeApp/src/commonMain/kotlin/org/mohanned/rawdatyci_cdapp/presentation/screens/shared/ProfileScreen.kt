package org.mohanned.rawdatyci_cdapp.presentation.screens.shared

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.domain.model.User
import org.mohanned.rawdatyci_cdapp.domain.model.UserRole
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.screens.auth.UserTypeSelectScreen
import org.mohanned.rawdatyci_cdapp.presentation.screens.auth.ResetPasswordScreen
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.*

object ProfileScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        ProfileScreenContent(onBack = null)
    }
}

@Composable
fun ProfileScreenContent(onBack: (() -> Unit)? = null) {
    val navigator = LocalNavigator.currentOrThrow
    val viewModel: ProfileViewModel = koinViewModel()
    val settingsViewModel: SettingsViewModel = koinViewModel()
    
    val state by viewModel.state.collectAsState()
    val settingsState by settingsViewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.onIntent(ProfileIntent.Load)
        settingsViewModel.onIntent(SettingsIntent.Load)
        
        viewModel.effect.collect { effect ->
            when (effect) {
                ProfileEffect.NavigateToLogin -> navigator.replaceAll(UserTypeSelectScreen)
                is ProfileEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        containerColor = AppBg,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = padding.calculateBottomPadding())
        ) {
            ProfileHeader(
                user = state.user,
                isLoading = state.isLoading,
                onBack = onBack
            )

            AnimateEntrance {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 40.dp)
                        .offset(y = (-30).dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    ProfileInfoExpandableSection(
                        originalUser = state.user,
                        currentName = state.name,
                        currentPhone = state.phone,
                        email = state.user?.email ?: "",
                        isLoading = state.isLoading,
                        onNameChange = { viewModel.onIntent(ProfileIntent.NameChanged(it)) },
                        onPhoneChange = { viewModel.onIntent(ProfileIntent.PhoneChanged(it)) },
                        onSave = { viewModel.onIntent(ProfileIntent.Save) },
                        isSaving = state.isSaving
                    )

                    // 2. قسم بيانات الروضة (عرض فقط لجميع المستخدمين)
                    KindergartenInfoSection(state = settingsState)

                    // 3. قسم الإعدادات والأمان
                    ProfileSettingsSection(
                        onChangePassword = { navigator.push(ResetPasswordScreen("logged_in")) },
                        onLogout = { viewModel.onIntent(ProfileIntent.Logout) }
                    )
                    
                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(user: User?, isLoading: Boolean, onBack: (() -> Unit)?) {
    Box(
        modifier = Modifier.fillMaxWidth().height(320.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = RawdatyGradients.HeroBlue,
                    shape = RoundedCornerShape(bottomStart = 45.dp, bottomEnd = 45.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBack != null) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowForward, null, tint = White)
                    }
                }
                Text(
                    "الملف الشخصي",
                    color = White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = CairoFontFamily,
                    modifier = Modifier.padding(start = if (onBack == null) 16.dp else 0.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 140.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                ShimmerBox(Modifier.size(120.dp).clip(CircleShape).border(4.dp, White, CircleShape))
            } else {
                Surface(
                    modifier = Modifier
                        .size(120.dp)
                        .shadow(20.dp, CircleShape),
                    shape = CircleShape,
                    color = White,
                    border = BorderStroke(4.dp, White)
                ) {
                    RawdatyAvatar(
                        name = user?.name ?: "User",
                        size = 112.dp,
                        gradient = RawdatyGradients.AvatarBlue
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            if (!isLoading && user != null) {
                Text(
                    user.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = BlueDark,
                    fontFamily = CairoFontFamily
                )
                Spacer(Modifier.height(4.dp))
                RoleTag(role = when(user.role) {
                    UserRole.ADMIN, UserRole.SUPER_ADMIN -> "مدير النظام"
                    UserRole.TEACHER -> "معلم"
                    UserRole.PARENT -> "ولي أمر"
                })
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun ProfileInfoExpandableSection(
    originalUser: User?,
    currentName: String,
    currentPhone: String,
    email: String,
    isLoading: Boolean,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSave: () -> Unit,
    isSaving: Boolean
) {
    var isExpanded by remember { mutableStateOf(false) }
    val isChanged = originalUser != null && (currentName != originalUser.name || currentPhone != (originalUser.phone ?: ""))

    RawdatyCard(containerColor = White, elevation = 4.dp) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Badge, null, tint = BluePrimary, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(12.dp))
                Text(
                    "معلومات الحساب",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BlueDark,
                    fontFamily = CairoFontFamily
                )
                Icon(
                    Icons.Default.ExpandMore,
                    null,
                    tint = Gray300,
                    modifier = Modifier.rotate(if (isExpanded) 180f else 0f)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp).padding(bottom = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (isLoading) {
                        repeat(3) { ShimmerBox(Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(12.dp))) }
                    } else {
                        InfoField(label = "الاسم الكامل", value = currentName, onValueChange = onNameChange, icon = Icons.Outlined.Person)
                        InfoField(label = "رقم الهاتف", value = currentPhone, onValueChange = onPhoneChange, icon = Icons.Outlined.Phone)
                        InfoField(label = "البريد الإلكتروني", value = email, onValueChange = {}, icon = Icons.Outlined.Email, enabled = false)

                        RawdatyButton(
                            text = "حفظ التعديلات",
                            onClick = onSave,
                            isLoading = isSaving,
                            enabled = isChanged,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KindergartenInfoSection(state: SettingsState) {
    var isExpanded by remember { mutableStateOf(false) }
    val aboutText = "روضة رَوْضَتِي هي منصة تعليمية رائدة تهدف إلى توفير بيئة تعليمية ذكية ومحفزة للأطفال. نحن نركز على الربط الفعال بين المدرسة وأولياء الأمور لضمان أفضل متابعة لتطور الطفل الأكاديمي والاجتماعي، مع توفير كافة سبل الراحة والأمان داخل أسوار الروضة."

    RawdatyCard(containerColor = White, elevation = 4.dp) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Business, null, tint = BluePrimary, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(12.dp))
                Text(
                    "بيانات الروضة",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BlueDark,
                    fontFamily = CairoFontFamily
                )
                Icon(
                    Icons.Default.ExpandMore,
                    null,
                    tint = Gray300,
                    modifier = Modifier.rotate(if (isExpanded) 180f else 0f)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp).padding(bottom = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (state.isLoading) {
                        ShimmerBox(Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(12.dp)))
                    } else {
                        // نبذة عن الروضة
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("عن الروضة", style = MaterialTheme.typography.labelMedium, color = Gray500, fontFamily = CairoFontFamily)
                            Text(
                                text = aboutText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Gray900, // لون أسود
                                fontFamily = CairoFontFamily,
                                lineHeight = 22.sp
                            )
                        }

                        HorizontalDivider(color = Gray50, thickness = 1.dp)

                        // معلومات التواصل (عرض فقط)
                        ReadOnlyInfoItem(label = "اسم الروضة", value = state.kindergartenName, icon = Icons.Outlined.Business)
                        ReadOnlyInfoItem(label = "العنوان", value = state.address, icon = Icons.Outlined.LocationOn)
                        ReadOnlyInfoItem(label = "رقم التواصل", value = state.phone, icon = Icons.Outlined.Phone)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadOnlyInfoItem(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, null, tint = BluePrimary, modifier = Modifier.size(20.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Gray500, fontFamily = CairoFontFamily)
            Text(value, style = MaterialTheme.typography.bodyMedium, color = Gray900, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
        }
    }
}

@Composable
private fun InfoField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    enabled: Boolean = true
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = Gray500, fontFamily = CairoFontFamily, modifier = Modifier.padding(horizontal = 4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(icon, null, tint = if (enabled) BluePrimary else Gray400) },
            textStyle = LocalTextStyle.current.copy(fontFamily = CairoFontFamily, fontSize = 14.sp, color = Gray900),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BluePrimary,
                unfocusedBorderColor = Gray200,
                disabledBorderColor = Gray100,
                disabledTextColor = Gray500,
                focusedTextColor = Gray900,
                unfocusedTextColor = Gray900
            )
        )
    }
}

@Composable
private fun ProfileSettingsSection(
    onChangePassword: () -> Unit,
    onLogout: () -> Unit
) {
    RawdatyCard(containerColor = White, elevation = 4.dp) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Settings, null, tint = BluePrimary, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(12.dp))
                Text("الإعدادات والأمان", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BlueDark, fontFamily = CairoFontFamily)
            }
            Spacer(Modifier.height(4.dp))
            SettingsItem(icon = Icons.Default.LockReset, title = "تغيير كلمة المرور", color = AmberPrimary, onClick = onChangePassword)
            HorizontalDivider(color = Gray50, thickness = 1.dp)
            SettingsItem(icon = Icons.Default.Logout, title = "تسجيل الخروج", color = ColorError, onClick = onLogout)
        }
    }
}

@Composable
private fun SettingsItem(icon: ImageVector, title: String, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { onClick() }.padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(modifier = Modifier.size(40.dp).background(color.copy(0.1f), CircleShape), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
        }
        Text(title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = Gray800, fontFamily = CairoFontFamily)
        Icon(Icons.Default.ChevronLeft, null, tint = Gray300)
    }
}
