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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

@Composable
fun AdminSendNotificationScreen(
    title: String,
    body: String,
    target: String,
    isLoading: Boolean,
    titleError: String?,
    bodyError: String?,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onTargetChange: (String) -> Unit,
    onSend: () -> Unit,
    onBack: () -> Unit,
) {
    val targets = listOf(
        Triple("all", "الكل", Icons.Default.Groups),
        Triple("teachers", "المعلمون", Icons.Default.School),
        Triple("parents", "أولياء الأمور", Icons.Default.FamilyRestroom),
    )

    Scaffold(
        containerColor = AppBg,
        topBar = {
            GlassHeader(
                title = "إرسال تنبيه عاجل",
                onBack = onBack,
                backgroundColor = BluePrimary
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
            RawdatyCard(backgroundColor = AmberLight.copy(0.4f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(
                        modifier = Modifier.size(52.dp).clip(CircleShape).background(White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Campaign, null, tint = AmberPrimary, modifier = Modifier.size(28.dp))
                    }
                    Column(Modifier.weight(1f)) {
                        Text("تواصل فوري وشامل", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black, color = BluePrimary)
                        Text("سيتم إرسال هذا التنبيه كإشعار دفع (Push) للفئة المستهدفة فوراً.", style = MaterialTheme.typography.bodySmall, color = Gray600)
                    }
                }
            }

            // Target Selection Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("الفئة المستهدفة بالتنبيه", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Gray700)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    targets.forEach { (value, label, icon) ->
                        val isSelected = target == value
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) BluePrimary else White)
                                .border(1.dp, if (isSelected) BluePrimary else Gray200, RoundedCornerShape(12.dp))
                                .clickable { onTargetChange(value) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(icon, null, tint = if (isSelected) White else Gray400, modifier = Modifier.size(20.dp))
                                Text(
                                    label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) White else Gray500
                                )
                            }
                        }
                    }
                }
            }

            // Input Fields
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                RawdatyField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = "عنوان التنبيه",
                    placeholder = "مثال: إعلان هام بخصوص الرحلة",
                    leadingIcon = Icons.Default.Title,
                    isError = titleError != null,
                    errorMessage = titleError
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("محتوى الإشعار", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Gray700)
                    OutlinedTextField(
                        value = body,
                        onValueChange = onBodyChange,
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        shape = RoundedCornerShape(16.dp),
                        placeholder = { Text("اكتب تفاصيل التنبيه هنا باختصار ووضوح...", color = Gray400) },
                        isError = bodyError != null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Gray200,
                            errorBorderColor = ColorError,
                            focusedContainerColor = White,
                            unfocusedContainerColor = White
                        )
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        if (bodyError != null) {
                            Text(bodyError, style = MaterialTheme.typography.labelSmall, color = ColorError)
                        } else {
                            Spacer(Modifier.weight(1f))
                        }
                        Text("${body.length} / 250", style = MaterialTheme.typography.labelSmall, color = if (body.length > 250) ColorError else Gray300)
                    }
                }
            }

            // Preview Section (Simulated)
            RawdatyCard(backgroundColor = Gray50) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("معاينة شكل الإشعار على الهاتف", style = MaterialTheme.typography.labelSmall, color = Gray400, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(White.copy(0.7f)).padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(AmberPrimary), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.NotificationsActive, null, tint = White, modifier = Modifier.size(20.dp))
                        }
                        Column(Modifier.weight(1f)) {
                            Text(title.ifEmpty { "عنوان التنبيه سيظهر هنا" }, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Black, color = Gray900, maxLines = 1)
                            Text(body.ifEmpty { "محتوى التنبيه سيظهر هنا بشكل مختصر..." }, style = MaterialTheme.typography.labelSmall, color = Gray600, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Actions
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                RawdatyButton(
                    text = "إرسال التنبيه الآن",
                    onClick = onSend,
                    isLoading = isLoading,
                    icon = Icons.AutoMirrored.Filled.Send,
                    backgroundColor = BluePrimary,
                    modifier = Modifier.fillMaxWidth()
                )
                
                TextButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("إلغاء والعودة للوحة التحكم", color = Gray400, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────
@Preview
@Composable
fun AdminSendNotificationPreview() {
    RawdatyTheme {
        AdminSendNotificationScreen(
            title = "",
            body = "",
            target = "all",
            isLoading = false,
            titleError = null,
            bodyError = null,
            onTitleChange = {},
            onBodyChange = {},
            onTargetChange = {},
            onSend = {},
            onBack = {}
        )
    }
}
