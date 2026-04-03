package org.mohanned.rawdatyci_cdapp.presentation.screens.parent

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
fun ParentComplaintScreen(
    title: String,
    body: String,
    isLoading: Boolean,
    titleError: String?,
    bodyError: String?,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onSend: () -> Unit,
    onBack: () -> Unit,
) {
    var selectedType by remember { mutableStateOf(0) }
    val types = listOf("شكوى", "اقتراح", "استفسار العام")

    Scaffold(
        containerColor = AppBg,
        topBar = {
            GlassHeader(
                title = "مركز المساعد والشكاوى", 
                onBack = onBack,
                gradient = RawdatyGradients.HeroBlue,
                headerHeight = 140.dp
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Introductory Card with Illustration/Icon
            Surface(
                color = BlueLight.copy(0.3f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, BluePrimary.copy(0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier.size(56.dp).clip(CircleShape).background(White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.SupportAgent, null, tint = BluePrimary, modifier = Modifier.size(32.dp))
                    }
                    Column(Modifier.weight(1f)) {
                        Text(
                            "نحن هنا من أجلك", 
                            style = MaterialTheme.typography.titleSmall, 
                            fontWeight = FontWeight.Black, 
                            color = BluePrimary,
                            fontFamily = CairoFontFamily
                        )
                        Text(
                            "تواصلك معنا يساعدنا في بناء بيئة تعليمية أفضل لأطفالنا.", 
                            style = MaterialTheme.typography.bodySmall, 
                            color = BluePrimary.copy(alpha = 0.8f),
                            fontFamily = CairoFontFamily
                        )
                    }
                }
            }

            // Type Selection with Premium Pills
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "تصنيف التواصل", 
                    style = MaterialTheme.typography.labelLarge, 
                    fontWeight = FontWeight.Bold, 
                    color = BlueDark,
                    fontFamily = CairoFontFamily
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    types.forEachIndexed { i, type ->
                        val isSelected = selectedType == i
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedType = i },
                            color = if (isSelected) BluePrimary else White,
                            border = BorderStroke(1.dp, if (isSelected) BluePrimary else Gray200),
                            shadowElevation = if (isSelected) 4.dp else 0.dp
                        ) {
                            Text(
                                type,
                                modifier = Modifier.padding(vertical = 12.dp),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) White else Gray500,
                                textAlign = TextAlign.Center,
                                fontFamily = CairoFontFamily
                            )
                        }
                    }
                }
            }

            // Main Input Form
            RawdatyCard(elevation = 1.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Text(
                        "تفاصيل الطلب",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = BlueDark,
                        fontFamily = CairoFontFamily
                    )

                    RawdatyField(
                        value = title,
                        onValueChange = onTitleChange,
                        label = "عنوان الرسالة",
                        placeholder = "مثال: ملاحظات حول النقل المدرسي",
                        leadingIcon = Icons.Outlined.Create,
                        isError = titleError != null,
                        errorMessage = titleError
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "محتوى الرسالة بالتفصيل", 
                            style = MaterialTheme.typography.labelMedium, 
                            fontWeight = FontWeight.Bold, 
                            color = Gray700,
                            fontFamily = CairoFontFamily
                        )
                        OutlinedTextField(
                            value = body,
                            onValueChange = onBodyChange,
                            modifier = Modifier.fillMaxWidth().height(180.dp),
                            shape = RoundedCornerShape(16.dp),
                            placeholder = { Text("يرجى كتابة التفاصيل هنا بنوع من الوضوح...", fontFamily = CairoFontFamily, color = Gray400) },
                            isError = bodyError != null,
                            textStyle = LocalTextStyle.current.copy(fontFamily = CairoFontFamily),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BluePrimary,
                                unfocusedBorderColor = Gray200,
                                errorBorderColor = ColorError,
                                focusedContainerColor = Gray50,
                                unfocusedContainerColor = Gray50
                            )
                        )
                        if (bodyError != null) {
                            Text(
                                bodyError, 
                                style = MaterialTheme.typography.labelSmall, 
                                color = ColorError,
                                fontFamily = CairoFontFamily
                            )
                        }
                    }
                }
            }

            // Attachment Section (Stylized)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { /* Attach */ },
                color = Gray50,
                border = BorderStroke(1.dp, Gray200)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.PhotoCamera, null, tint = BluePrimary, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "إرفاق صورة توضيحية (اختياري)", 
                        style = MaterialTheme.typography.labelLarge, 
                        color = BluePrimary, 
                        fontWeight = FontWeight.Bold,
                        fontFamily = CairoFontFamily
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Footer Action Button
            Column(modifier = Modifier.padding(bottom = 32.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                RawdatyButton(
                    text = "إرسال إلى الإدارة",
                    onClick = onSend,
                    isLoading = isLoading,
                    icon = Icons.AutoMirrored.Filled.Send,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text(
                    "سيتم مراجعة طلبك والرد عليه في أسرع وقت ممكن.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Gray400,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontFamily = CairoFontFamily
                )
            }
        }
    }
}

@Preview
@Composable
fun ParentComplaintPreview() {
    RawdatyTheme {
        ParentComplaintScreen(
            title = "",
            body = "",
            isLoading = false,
            titleError = null,
            bodyError = null,
            onTitleChange = {},
            onBodyChange = {},
            onSend = {},
            onBack = {}
        )
    }
}
