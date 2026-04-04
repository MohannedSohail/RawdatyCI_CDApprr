package org.mohanned.rawdatyci_cdapp.presentation.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import org.jetbrains.compose.resources.painterResource
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import rawdatyci_cdapp.composeapp.generated.resources.Res
import rawdatyci_cdapp.composeapp.generated.resources.rawdatylogo

@Composable
fun LoginScreen(
    identifier: String = "",
    password: String = "",
    identifierError: String? = null,
    passwordError: String? = null,
    generalError: String? = null,
    isLoading: Boolean = false,
    onIdentifierChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onSubmit: () -> Unit = {},
    onForgotPassword: () -> Unit = {},
) {
    Scaffold(containerColor = White) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Premium Artistic Header (40% height)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF0F2A3E), Color(0xFF1E4C6F))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Background Decoration
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = White.copy(0.05f),
                        radius = size.minDimension / 1.5f,
                        center = Offset(size.width * 0.9f, size.height * 0.1f)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Replaced RawdatyLogo with the new PNG logo
                    Image(
                        painter = painterResource(Res.drawable.rawdatylogo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "مرحباً بك مجدداً", 
                            style = MaterialTheme.typography.headlineMedium, 
                            color = White, 
                            fontWeight = FontWeight.Black,
                            fontFamily = CairoFontFamily
                        )
                        Text(
                            "يُرجى تسجيل الدخول للمتابعة", 
                            style = MaterialTheme.typography.bodyLarge, 
                            color = White.copy(0.7f),
                            fontFamily = CairoFontFamily
                        )
                    }
                }
            }

            // High-Fidelity Login Form
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = (-32).dp),
                color = White,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 40.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (generalError != null) {
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                            color = ColorError.copy(0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                generalError, 
                                color = ColorError, 
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                fontFamily = CairoFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        RawdatyField(
                            value = identifier, 
                            onValueChange = onIdentifierChange, 
                            label = "مُعرف الحساب (رقم الهوية أو البريد)", 
                            placeholder = "ادخل رقم الهوية أو البريد الإلكتروني",
                            leadingIcon = Icons.Outlined.Badge,
                            isError = identifierError != null,
                            errorMessage = identifierError
                        )
                        
                        Column {
                            RawdatyField(
                                value = password, 
                                onValueChange = onPasswordChange, 
                                label = "كلمة المرور الخاصة بك", 
                                placeholder = "ادخل كلمة المرور",
                                leadingIcon = Icons.Outlined.Lock, 
                                isPassword = true,
                                isError = passwordError != null,
                                errorMessage = passwordError
                            )
                            
                            Text(
                                "نسيت كلمة المرور؟",
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(top = 10.dp)
                                    .clickable { onForgotPassword() },
                                color = BluePrimary,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                fontFamily = CairoFontFamily
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        RawdatyButton(
                            text = "تسجيل الدخول للنظام", 
                            onClick = onSubmit, 
                            modifier = Modifier.fillMaxWidth().height(60.dp), 
                            isLoading = isLoading,
                            backgroundColor = BluePrimary
                        )
                    }

                    Spacer(Modifier.height(48.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "ليس لديك حساب مسجل؟ ", 
                            color = Gray500, 
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = CairoFontFamily
                        )
                        Text(
                            "تواصل مع الإدارة", 
                            color = BluePrimary, 
                            style = MaterialTheme.typography.bodyMedium, 
                            fontWeight = FontWeight.Black, 
                            fontFamily = CairoFontFamily,
                            modifier = Modifier.clickable { /* Contact Admin */ }
                        )
                    }
                    
                    Spacer(Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
@Preview
fun LoginPreview() {
    RawdatyTheme {
        LoginScreen(
            identifier = "1234567890",
            password = "password123"
        )
    }
}
