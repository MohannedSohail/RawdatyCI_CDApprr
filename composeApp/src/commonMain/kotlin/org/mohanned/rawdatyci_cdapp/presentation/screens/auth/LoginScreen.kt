package org.mohanned.rawdatyci_cdapp.presentation.screens.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.mohanned.rawdatyci_cdapp.domain.model.UserRole
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyButton
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyField
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AuthEffect
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AuthIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AuthViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.presentation.navigation.roleToHome
import rawdatyci_cdapp.composeapp.generated.resources.Res
import rawdatyci_cdapp.composeapp.generated.resources.rawdatylogo

data class LoginScreen(val role: String? = null) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: AuthViewModel = koinViewModel()
        
        AuthScreenRoot(
            viewModel = viewModel,
            onNavigateToDashboard = { userRole ->
                navigator.replaceAll(roleToHome(userRole))
            },
            onForgotPassword = {
                navigator.push(ForgotPasswordScreen)
            }
        )
    }
}

@Composable
fun AuthScreenRoot(
    viewModel: AuthViewModel,
    onNavigateToDashboard: (UserRole) -> Unit,
    onForgotPassword: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AuthEffect.NavigateToDashboard -> onNavigateToDashboard(effect.role)
                else -> {}
            }
        }
    }

    LoginScreenUI(
        identifier = state.email,
        password = state.password,
        generalError = state.error,
        isLoading = state.isLoading,
        onIdentifierChange = { viewModel.onIntent(AuthIntent.EmailChanged(it)) },
        onPasswordChange = { viewModel.onIntent(AuthIntent.PasswordChanged(it)) },
        onSubmit = { viewModel.onIntent(AuthIntent.Login) },
        onForgotPassword = onForgotPassword
    )
}

@Composable
fun LoginScreenUI(
    identifier: String = "",
    password: String = "",
    generalError: String? = null,
    isLoading: Boolean = false,
    onIdentifierChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onSubmit: () -> Unit = {},
    onForgotPassword: () -> Unit = {},
) {
    var headerVisible by remember { mutableStateOf(false) }
    var formVisible   by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        headerVisible = true
        kotlinx.coroutines.delay(650)
        formVisible = true
    }

    val logoScale by animateFloatAsState(targetValue = if (headerVisible) 1f else 0.5f, animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium))
    val logoAlpha by animateFloatAsState(targetValue = if (headerVisible) 1f else 0f, animationSpec = tween(400))
    val logoOffsetY by animateFloatAsState(targetValue = if (headerVisible) 0f else 10f, animationSpec = tween(500, easing = EaseOutCubic))
    val textAlpha by animateFloatAsState(targetValue = if (headerVisible) 1f else 0f, animationSpec = tween(480, delayMillis = 280))
    val textOffsetY by animateFloatAsState(targetValue = if (headerVisible) 0f else 10f, animationSpec = tween(480, delayMillis = 280, easing = EaseOutCubic))
    val subAlpha by animateFloatAsState(targetValue = if (headerVisible) 1f else 0f, animationSpec = tween(400, delayMillis = 450))
    val formAlpha by animateFloatAsState(targetValue = if (formVisible) 1f else 0f, animationSpec = tween(520, easing = EaseOutQuart))
    val formOffsetY by animateFloatAsState(targetValue = if (formVisible) 0f else 18f, animationSpec = tween(520, easing = EaseOutQuart))

    Scaffold(containerColor = White) { padding ->
        Column(modifier = Modifier.fillMaxSize().background(White).verticalScroll(rememberScrollState())) {
            Box(
                modifier = Modifier.fillMaxWidth().height(360.dp).clip(WaveShape()).background(RawdatyGradients.Splash),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(color = Color.White.copy(alpha = .08f), radius = size.minDimension * 1.1f, center = Offset(size.width * .95f, size.height * -.1f))
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp), modifier = Modifier.statusBarsPadding()) {
                    Image(
                        painter = painterResource(Res.drawable.rawdatylogo),
                        contentDescription = null,
                        modifier = Modifier.size(130.dp).clip(CircleShape).scale(logoScale).alpha(logoAlpha).border(2.dp, White.copy(0.2f), CircleShape).offset(y = logoOffsetY.dp)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.alpha(textAlpha).offset(y = textOffsetY.dp)) {
                        Text("مرحباً بك مجدداً", style = MaterialTheme.typography.displaySmall, color = White, fontWeight = FontWeight.Black, fontFamily = CairoFontFamily)
                        Text("نظام رَوْضَتِي - الجيل الخامس", style = MaterialTheme.typography.titleMedium, color = White.copy(alpha = .8f), fontFamily = CairoFontFamily, modifier = Modifier.alpha(subAlpha))
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize().offset(y = (-40).dp).alpha(formAlpha).offset(y = formOffsetY.dp)) {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 40.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (generalError != null) {
                        Surface(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), color = ColorError.copy(.1f), shape = RoundedCornerShape(12.dp)) {
                            Text(generalError, color = ColorError, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold)
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        RawdatyField(
                            value = identifier, 
                            onValueChange = onIdentifierChange, 
                            label = "مُعرف الحساب", 
                            placeholder = "رقم الهوية أو البريد", 
                            leadingIcon = Icons.Outlined.Badge,
                            isError = generalError != null
                        )
                        Column {
                            RawdatyField(
                                value = password, 
                                onValueChange = onPasswordChange, 
                                label = "كلمة المرور", 
                                placeholder = "ادخل كلمة المرور", 
                                leadingIcon = Icons.Outlined.Lock, 
                                isPassword = true,
                                isError = generalError != null
                            )
                            Text("نسيت كلمة المرور؟", modifier = Modifier.align(Alignment.End).padding(top = 10.dp).clickable { onForgotPassword() }, color = BluePrimary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
                        }
                        Spacer(Modifier.height(16.dp))
                        RawdatyButton(text = "تسجيل الدخول", onClick = onSubmit, modifier = Modifier.fillMaxWidth().height(60.dp), isLoading = isLoading, backgroundColor = BluePrimary)
                    }
                }
            }
        }
    }
}
