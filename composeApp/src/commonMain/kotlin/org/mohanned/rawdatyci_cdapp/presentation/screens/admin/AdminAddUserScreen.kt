package org.mohanned.rawdatyci_cdapp.presentation.screens.admin

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun AdminAddUserScreen(
    name: String,
    email: String,
    phone: String,
    password: String,
    role: String,
    isLoading: Boolean,
    nameError: String?,
    emailError: String?,
    passwordError: String?,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRoleChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
) {
    var roleExpanded by remember { mutableStateOf(false) }
    val roles = listOf("معلمة", "ولي أمر", "مدير")

    Scaffold(
        containerColor = AppBg,
        topBar = {
            GlassHeader(
                title = "إضافة مستخدم",
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Card
            RawdatyCard(backgroundColor = BlueLight.copy(0.4f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(
                        modifier = Modifier.size(52.dp).clip(CircleShape).background(White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PersonAdd, null, tint = BluePrimary, modifier = Modifier.size(28.dp))
                    }
                    Column(Modifier.weight(1f)) {
                        Text("إنشاء حساب جديد", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black, color = BluePrimary)
                        Text("تأكد من إدخال البيانات الصحيحة لضمان وصول المستخدم للنظام.", style = MaterialTheme.typography.bodySmall, color = Gray600)
                    }
                }
            }

            // Form Fields
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                RawdatyField(
                    value = name,
                    onValueChange = onNameChange,
                    label = "الاسم الكامل",
                    placeholder = "مثال: سارة محمد العبود",
                    leadingIcon = Icons.Default.Badge,
                    isError = nameError != null,
                    errorMessage = nameError
                )

                // Role Selector
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("صلاحية المستخدم", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Gray700)
                    ExposedDropdownMenuBox(
                        expanded = roleExpanded,
                        onExpandedChange = { roleExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = role.ifEmpty { "اختر الدور الوظيفي" },
                            onValueChange = {},
                            readOnly = true,
                            leadingIcon = { Icon(Icons.Default.AdminPanelSettings, null, tint = Gray400) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(roleExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BluePrimary,
                                unfocusedBorderColor = Gray200,
                                focusedContainerColor = White,
                                unfocusedContainerColor = White
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = roleExpanded,
                            onDismissRequest = { roleExpanded = false }
                        ) {
                            roles.forEach { r ->
                                DropdownMenuItem(
                                    text = { Text(r, style = MaterialTheme.typography.bodyMedium) },
                                    onClick = { onRoleChange(r); roleExpanded = false }
                                )
                            }
                        }
                    }
                }

                RawdatyField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = "البريد الإلكتروني",
                    placeholder = "example@mail.com",
                    leadingIcon = Icons.Default.Email,
                    isError = emailError != null,
                    errorMessage = emailError
                )

                RawdatyField(
                    value = phone,
                    onValueChange = onPhoneChange,
                    label = "رقم التواصل",
                    placeholder = "05XXXXXXXX",
                    leadingIcon = Icons.Default.Phone
                )

                RawdatyField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = "كلمة المرور المؤقتة",
                    placeholder = "********",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    isError = passwordError != null,
                    errorMessage = passwordError
                )
            }

            // Password Security Hint
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AmberLight.copy(0.5f))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Info, null, tint = AmberPrimary, modifier = Modifier.size(16.dp))
                Text(
                    "سيُطلب من المستخدم تغيير كلمة المرور عند أول تسجيل دخول.",
                    style = MaterialTheme.typography.labelSmall,
                    color = AmberPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(24.dp))

            // Actions
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                RawdatyButton(
                    text = "تأكيد وإضافة المستخدم",
                    onClick = onSave,
                    isLoading = isLoading,
                    icon = Icons.Default.Save,
                    modifier = Modifier.fillMaxWidth()
                )
                
                TextButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("إلغاء العملية", color = Gray400, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────
@Preview
@Composable
fun AdminAddUserPreview() {
    RawdatyTheme {
        AdminAddUserScreen(
            name = "",
            email = "",
            phone = "",
            password = "",
            role = "",
            isLoading = false,
            nameError = null,
            emailError = null,
            passwordError = null,
            onNameChange = {},
            onEmailChange = {},
            onPhoneChange = {},
            onPasswordChange = {},
            onRoleChange = {},
            onSave = {},
            onBack = {}
        )
    }
}
