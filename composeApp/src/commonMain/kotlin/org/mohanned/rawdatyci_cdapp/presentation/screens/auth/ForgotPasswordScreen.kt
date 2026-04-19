package org.mohanned.rawdatyci_cdapp.presentation.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ForgotPasswordEffect
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ForgotPasswordIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ForgotPasswordState
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ForgotPasswordViewModel

object ForgotPasswordScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ForgotPasswordViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is ForgotPasswordEffect.NavigateToOtp -> {
                        navigator.push(OtpScreen(effect.email))
                    }
                    is ForgotPasswordEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                    is ForgotPasswordEffect.ShowSuccess -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        ForgotPasswordUI(
            state = state,
            snackbarHostState = snackbarHostState,
            onIntent = viewModel::onIntent,
            onBack = { navigator.pop() }
        )
    }
}

@Composable
fun ForgotPasswordUI(
    state: ForgotPasswordState,
    snackbarHostState: SnackbarHostState,
    onIntent: (ForgotPasswordIntent) -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        containerColor = AppBg,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            WaveHeader(
                title = "استعادة الحساب",
                subtitle = "أدخل بريدك الإلكتروني ليصلك رمز التفعيل",
                onBack = onBack,
                gradient = RawdatyGradients.HeroBlue,
                headerHeight = 240.dp
            ) {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = CircleShape,
                    color = White.copy(0.15f),
                    border = BorderStroke(1.dp, White.copy(0.2f))
                ) {
                    Icon(
                        Icons.Outlined.LockReset,
                        null,
                        tint = White,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            AnimateEntrance(delay = 300) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .offset(y = (-30).dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    RawdatyCard(elevation = 8.dp, containerColor = White) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            RawdatyField(
                                value = state.email,
                                onValueChange = { onIntent(ForgotPasswordIntent.EmailChanged(it)) },
                                label = "البريد الإلكتروني المسجل",
                                placeholder = "example@mail.com",
                                leadingIcon = Icons.Outlined.AlternateEmail
                            )

                            RawdatyButton(
                                text = "إرسال رمز التحقق",
                                onClick = { onIntent(ForgotPasswordIntent.Submit) },
                                isLoading = state.isLoading,
                                backgroundColor = BluePrimary,
                                modifier = Modifier.fillMaxWidth().height(56.dp)
                            )

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = BlueLight.copy(0.15f),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, BluePrimary.copy(0.1f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        Icons.Outlined.Info,
                                        null,
                                        tint = BluePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        "تأكد من كتابة البريد الإلكتروني بشكل صحيح. يرجى مراجعة صندوق الوارد (أو الرسائل غير المرغوب فيها) للوصول إلى الرمز.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BlueDark,
                                        fontFamily = CairoFontFamily,
                                        modifier = Modifier.weight(1f),
                                        lineHeight =18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
