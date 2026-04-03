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
fun ForgotPasswordScreen(
    email: String,
    emailError: String?,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
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
                        Icon(Icons.Outlined.LockReset, null, tint = White, modifier = Modifier.padding(16.dp))
                    }
                    
                    Spacer(Modifier.height(20.dp))
                    
                    Text(
                        "استعادة الحساب",
                        style = MaterialTheme.typography.headlineSmall,
                        color = White,
                        fontWeight = FontWeight.Black,
                        fontFamily = CairoFontFamily
                    )
                    Text(
                        "أدخل بريدك الإلكتروني ليصلك رمز استرداد الحساب",
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
                        RawdatyField(
                            value = email,
                            onValueChange = onEmailChange,
                            label = "البريد الإلكتروني المسجل",
                            placeholder = "example@mail.com",
                            leadingIcon = Icons.Outlined.AlternateEmail,
                            isError = emailError != null,
                            errorMessage = emailError
                        )

                        RawdatyButton(
                            text = "إرسال رمز التحقق",
                            onClick = onSubmit,
                            isLoading = isLoading,
                            backgroundColor = BluePrimary,
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        )

                        // Help/Info Card (Refined)
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
                                Icon(Icons.Outlined.Info, null, tint = BluePrimary, modifier = Modifier.size(20.dp))
                                Text(
                                    "تأكد من كتابة البريد الإلكتروني بشكل صحيح. يرجى مراجعة صندوق الوارد (أو الرسائل غير المرغوب فيها) للوصول إلى الرمز.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BlueDark,
                                    fontFamily = CairoFontFamily,
                                    modifier = Modifier.weight(1f),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                        
                        Spacer(Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ForgotPasswordPreview() {
    RawdatyTheme {
        ForgotPasswordScreen(
            email = "",
            emailError = null,
            isLoading = false,
            onEmailChange = {},
            onSubmit = {},
            onBack = {}
        )
    }
}
