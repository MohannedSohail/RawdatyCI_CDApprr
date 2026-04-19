package org.mohanned.rawdatyci_cdapp.presentation.screens.shared

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.domain.model.User
import org.mohanned.rawdatyci_cdapp.domain.model.UserRole
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.screens.auth.LoginScreen
import org.mohanned.rawdatyci_cdapp.presentation.screens.auth.ResetPasswordScreen
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ProfileEffect
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ProfileIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ProfileViewModel

object ProfileScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ProfileViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            viewModel.onIntent(ProfileIntent.Load)
            viewModel.effect.collect { effect ->
                when (effect) {
                    ProfileEffect.NavigateToLogin -> navigator.replaceAll(LoginScreen())
                    is ProfileEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        ProfileScreenContent(
            user = state.user,
            name = state.name,
            phone = state.phone,
            isLoading = state.isLoading,
            isSaving = state.isSaving,
            onNameChange = { viewModel.onIntent(ProfileIntent.NameChanged(it)) },
            onPhoneChange = { viewModel.onIntent(ProfileIntent.PhoneChanged(it)) },
            onSave = { viewModel.onIntent(ProfileIntent.Save) },
            onChangePassword = { current, new, confirm ->
                // Typically a dialog or separate intent, here we'll assume a direct call for simplicity
            },
            onLogout = { viewModel.onIntent(ProfileIntent.Logout) },
            onAvatarClick = { viewModel.onIntent(ProfileIntent.Save) },
            onChangePasswordClick = { navigator.push(ResetPasswordScreen("logged_in")) },
            onBack = { navigator.pop() },
            snackbarHostState = snackbarHostState
        )
    }
}

@Composable
fun ProfileScreenContent(
    user: User?,
    name: String,
    phone: String,
    isLoading: Boolean,
    isSaving: Boolean,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSave: () -> Unit,
    onChangePassword: (String, String, String) -> Unit,
    onLogout: () -> Unit,
    onAvatarClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    Scaffold(
        containerColor = AppBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            GlassHeader(
                title = "الملف الشخصي",
                onBack = onBack,
                gradient = RawdatyGradients.Primary,
                headerHeight = 140.dp
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BluePrimary)
            }
        } else if (user != null) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    RawdatyAvatar(user.name, size = 100.dp, gradient = RawdatyGradients.AvatarBlue)
                    IconButton(
                        onClick = onAvatarClick,
                        modifier = Modifier.size(32.dp).background(BluePrimary, CircleShape).border(2.dp, White, CircleShape)
                    ) {
                        Icon(Icons.Default.CameraAlt, null, tint = White, modifier = Modifier.size(16.dp))
                    }
                }

                Text(user.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
                RoleTag(role = when(user.role) {
                    UserRole.ADMIN, UserRole.SUPER_ADMIN -> "مدير النظام"
                    UserRole.TEACHER -> "معلمة"
                    UserRole.PARENT -> "ولي أمر"
                })

                RawdatyCard(containerColor = White) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("المعلومات الشخصية", fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
                        RawdatyField(value = name, onValueChange = onNameChange, label = "الاسم الكامل", leadingIcon = Icons.Default.Person)
                        RawdatyField(value = phone, onValueChange = onPhoneChange, label = "رقم الهاتف", leadingIcon = Icons.Default.Phone)
                        RawdatyField(value = user.email, onValueChange = {}, label = "البريد الإلكتروني", leadingIcon = Icons.Default.Email, enabled = false)
                    }
                }

                RawdatyButton(
                    text = "حفظ التعديلات",
                    onClick = onSave,
                    isLoading = isSaving,
                    modifier = Modifier.fillMaxWidth()
                )

                RawdatyCard(containerColor = White) {
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onChangePasswordClick() }.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(Icons.Default.LockReset, null, tint = AmberPrimary)
                        Text("تغيير كلمة المرور", modifier = Modifier.weight(1f), fontFamily = CairoFontFamily)
                        Icon(Icons.Default.ChevronLeft, null, tint = Gray300)
                    }
                }

                RawdatyButton(
                    text = "تسجيل الخروج",
                    onClick = onLogout,
                    backgroundColor = ColorError.copy(0.1f),
                    textColor = ColorError,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}
