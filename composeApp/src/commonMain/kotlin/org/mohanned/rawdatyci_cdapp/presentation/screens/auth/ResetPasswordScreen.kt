package org.mohanned.rawdatyci_cdapp.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyButton
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyField
import org.mohanned.rawdatyci_cdapp.presentation.components.WaveHeader
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ForgotPasswordEffect
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ForgotPasswordIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ForgotPasswordViewModel

data class ResetPasswordScreen(val resetToken: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ForgotPasswordViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                if (effect is ForgotPasswordEffect.ShowSuccess) {
                    navigator.replaceAll(LoginScreen())
                }
            }
        }

        Scaffold(containerColor = White) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(White)
                    .verticalScroll(rememberScrollState())
            ) {
                WaveHeader(
                    title = "تعيين كلمة المرور",
                    subtitle = "يُرجى إدخال كلمة المرور الجديدة",
                    gradient = RawdatyGradients.Splash,
                    headerHeight = 220.dp
                )

                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    RawdatyField(
                        value = state.newPassword,
                        onValueChange = { viewModel.onIntent(ForgotPasswordIntent.NewPasswordChanged(it)) },
                        label = "كلمة المرور الجديدة",
                        leadingIcon = Icons.Outlined.Lock,
                        isPassword = true
                    )
                    RawdatyField(
                        value = state.confirmPassword,
                        onValueChange = { viewModel.onIntent(ForgotPasswordIntent.ConfirmPasswordChanged(it)) },
                        label = "تأكيد كلمة المرور",
                        leadingIcon = Icons.Outlined.Lock,
                        isPassword = true
                    )
                    
                    state.error?.let {
                        Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(Modifier.height(12.dp))
                    
                    RawdatyButton(
                        text = "حفظ وتغيير",
                        onClick = { viewModel.onIntent(ForgotPasswordIntent.ResetPassword(resetToken)) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        isLoading = state.isLoading
                    )
                }
            }
        }
    }
}
