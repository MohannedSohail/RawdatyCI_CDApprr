package org.mohanned.rawdatyci_cdapp.presentation.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

@Composable
fun ResetPasswordScreen(
    newPassword: String,
    confirmPassword: String,
    isLoading: Boolean,
    error: String?,
    onNewPasswordChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        containerColor = White
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Premium Gradient Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.38f)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF0F2A3E), Color(0xFF1E4C6F))
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .statusBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .clip(CircleShape)
                                .background(White.copy(0.15f))
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = White)
                        }
                    }
                    
                    Spacer(Modifier.height(32.dp))
                    
                    Surface(
                        modifier = Modifier.size(72.dp),
                        shape = CircleShape,
                        color = White.copy(0.1f),
                        border = BorderStroke(1.dp, White.copy(0.2f))
                    ) {
                        Icon(Icons.Outlined.VerifiedUser, null, tint = White, modifier = Modifier.padding(16.dp))
                    }
                    
                    Spacer(Modifier.height(20.dp))
                    
                    Text(
                        "تغيير كلمة المرور",
                        style = MaterialTheme.typography.headlineSmall,
                        color = White,
                        fontWeight = FontWeight.Black,
                        fontFamily = CairoFontFamily
                    )
                    Text(
                        "يُرجى تعيين كلمة مرور قوية وجديدة لحسابك",
                        style = MaterialTheme.typography.bodySmall,
                        color = White.copy(0.7f),
                        fontFamily = CairoFontFamily,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Interactive Form Container
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.fillMaxHeight(0.32f))
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = White,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 40.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        // Password Strength Indicators
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            PasswordRuleChip(
                                label = "٨ أحرف فأكثر",
                                met = newPassword.length >= 8,
                                modifier = Modifier.weight(1f)
                            )
                            PasswordRuleChip(
                                label = "يحتوي أرقام",
                                met = newPassword.any { it.isDigit() },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        RawdatyField(
                            value = newPassword,
                            onValueChange = onNewPasswordChange,
                            label = "كلمة المرور الجديدة",
                            placeholder = "••••••••",
                            leadingIcon = Icons.Outlined.Lock,
                            isPassword = true
                        )

                        RawdatyField(
                            value = confirmPassword,
                            onValueChange = onConfirmChange,
                            label = "تأكيد كلمة المرور",
                            placeholder = "••••••••",
                            leadingIcon = Icons.Outlined.LockOpen,
                            isPassword = true,
                            isError = error != null,
                            errorMessage = error
                        )

                        RawdatyButton(
                            text = "حفظ وإعادة تعيين الحساب",
                            onClick = onSubmit,
                            isLoading = isLoading,
                            backgroundColor = BluePrimary,
                            modifier = Modifier.fillMaxWidth().height(60.dp)
                        )
                        
                        Spacer(Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun PasswordRuleChip(label: String, met: Boolean, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = if (met) ColorSuccess.copy(0.1f) else Gray100,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (met) ColorSuccess.copy(0.3f) else Gray200)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                if (met) Icons.Default.CheckCircle else Icons.Outlined.Circle,
                null,
                tint = if (met) ColorSuccess else Gray400,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = if (met) ColorSuccess else Gray500,
                fontWeight = if (met) FontWeight.Bold else FontWeight.Medium,
                fontFamily = CairoFontFamily
            )
        }
    }
}

@Preview
@Composable
fun ResetPasswordPreview() {
    RawdatyTheme {
        ResetPasswordScreen(
            newPassword = "",
            confirmPassword = "",
            isLoading = false,
            error = null,
            onNewPasswordChange = {},
            onConfirmChange = {},
            onSubmit = {},
            onBack = {}
        )
    }
}
