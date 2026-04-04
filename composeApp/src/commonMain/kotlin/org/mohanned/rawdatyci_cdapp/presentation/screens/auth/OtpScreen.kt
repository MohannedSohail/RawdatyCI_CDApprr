package org.mohanned.rawdatyci_cdapp.presentation.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

@Composable
@Preview
fun OtpScreen(
    email: String,
    otp: String,
    countdown: Int,
    canResend: Boolean,
    isLoading: Boolean,
    error: String?,
    onOtpChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onResend: () -> Unit,
    onBack: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    
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
                        Icon(Icons.Outlined.MarkEmailRead, null, tint = White, modifier = Modifier.padding(16.dp))
                    }
                    
                    Spacer(Modifier.height(20.dp))
                    
                    Text(
                        "تأكيد الرمز",
                        style = MaterialTheme.typography.headlineSmall,
                        color = White,
                        fontWeight = FontWeight.Black,
                        fontFamily = CairoFontFamily
                    )
                    Text(
                        "أرسلنا رمز التفعيل إلى بريدك الإلكتروني",
                        style = MaterialTheme.typography.bodySmall,
                        color = White.copy(0.7f),
                        fontFamily = CairoFontFamily,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // OTP Input Container
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
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
                        verticalArrangement = Arrangement.spacedBy(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = BlueLight.copy(0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                email,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = BluePrimary,
                                fontWeight = FontWeight.Bold,
                                fontFamily = CairoFontFamily
                            )
                        }

                        // Premium OTP Box Layout
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            repeat(4) { i ->
                                val char = otp.getOrNull(i)?.toString() ?: ""
                                val isFocused = otp.length == i
                                
                                Surface(
                                    modifier = Modifier.size(64.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (char.isNotEmpty()) BlueLight.copy(0.1f) else Gray50,
                                    border = BorderStroke(
                                        width = if (isFocused || char.isNotEmpty()) 2.dp else 1.dp,
                                        color = when {
                                            error != null -> ColorError
                                            isFocused -> BluePrimary
                                            char.isNotEmpty() -> BluePrimary.copy(0.6f)
                                            else -> Gray200
                                        }
                                    )
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            char,
                                            style = MaterialTheme.typography.headlineMedium,
                                            color = BlueDark,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = CairoFontFamily
                                        )
                                    }
                                }
                            }
                        }

                        // Invisible Input capturing the OTp
                        BasicTextField(
                            value = otp,
                            onValueChange = { 
                                if (it.length <= 4 && it.all { c -> c.isDigit() }) {
                                    onOtpChange(it)
                                    if (it.length == 4) focusManager.clearFocus()
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.size(1.dp).alpha(0f)
                        )

                        if (error != null) {
                            Text(
                                error,
                                color = ColorError,
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = CairoFontFamily,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        RawdatyButton(
                            text = "تأكيد الدخول",
                            onClick = onSubmit,
                            isLoading = isLoading,
                            enabled = otp.length == 4,
                            backgroundColor = BluePrimary,
                            modifier = Modifier.fillMaxWidth().height(60.dp)
                        )

                        // Resend Logic UI
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (!canResend) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Outlined.Timer, null, tint = Gray400, modifier = Modifier.size(16.dp))
                                    Text(
                                        "إعادة الإرسال متاحة خلال ${countdown} ثانية",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Gray500,
                                        fontFamily = CairoFontFamily
                                    )
                                }
                            } else {
                                TextButton(onClick = onResend) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Icons.Outlined.Refresh, null, tint = BluePrimary, modifier = Modifier.size(20.dp))
                                        Text(
                                            "إعادة إرسال الرمز الآن",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = BluePrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = CairoFontFamily
                                        )
                                    }
                                }
                            }
                        }
                        
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun OtpPreview() {
    RawdatyTheme {
        OtpScreen(
            email = "user@mail.com",
            otp = "12",
            countdown = 59,
            canResend = false,
            isLoading = false,
            error = null,
            onOtpChange = {},
            onSubmit = {},
            onResend = {},
            onBack = {}
        )
    }
}
