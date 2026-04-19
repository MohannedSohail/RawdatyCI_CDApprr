package org.mohanned.rawdatyci_cdapp.presentation.screens.auth

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LockClock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.OtpEffect
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.OtpIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.OtpViewModel

data class OtpScreen(val email: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: OtpViewModel = koinViewModel { parametersOf(email) }
        val state by viewModel.state.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    OtpEffect.NavigateToReset -> {
                        state.resetToken?.let { token ->
                            navigator.push(ResetPasswordScreen(token))
                        }
                    }
                    is OtpEffect.ShowError -> { /* UI error display */ }
                }
            }
        }

        // Timer effect
        LaunchedEffect(Unit) {
            while (true) {
                kotlinx.coroutines.delay(1000)
                viewModel.onIntent(OtpIntent.Tick)
            }
        }

        Scaffold(containerColor = White) { padding ->
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            ) {
                WaveHeader(
                    title = "تحقق من البريد",
                    subtitle = "أدخل الرمز المرسل إلى $email",
                    onBack = { navigator.pop() },
                    gradient = RawdatyGradients.HeroBlue,
                    headerHeight = 240.dp
                ) {
                    Icon(Icons.Outlined.LockClock, null, tint = White, modifier = Modifier.size(64.dp))
                }

                Column(
                    modifier = Modifier.padding(horizontal = 24.dp).offset(y = (-30).dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    RawdatyCard(elevation = 8.dp, containerColor = White) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            RawdatyField(
                                value = state.otp,
                                onValueChange = { if (it.length <= 6) viewModel.onIntent(OtpIntent.OtpChanged(it)) },
                                label = "رمز التحقق (6 أرقام)",
                                placeholder = "000000",
                                isError = state.error != null
                            )

                            RawdatyButton(
                                text = "تحقق الآن",
                                onClick = { viewModel.onIntent(OtpIntent.Submit) },
                                isLoading = state.isLoading,
                                modifier = Modifier.fillMaxWidth().height(56.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("لم يصلك الرمز؟ ", color = Gray500, fontFamily = CairoFontFamily)
                                if (state.canResend) {
                                    Text(
                                        "إعادة إرسال",
                                        color = BluePrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = CairoFontFamily,
                                        modifier = Modifier.clickable { viewModel.onIntent(OtpIntent.Resend) }
                                    )
                                } else {
                                    Text(
                                        "إعادة إرسال خلال ${state.countdown} ثانية",
                                        color = Gray400,
                                        fontFamily = CairoFontFamily
                                    )
                                }
                            }
                        }
                    }
                    
                    state.error?.let {
                        Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
