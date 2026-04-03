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
import org.mohanned.rawdatyci_cdapp.domain.model.User
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddClassroomScreen(
    name: String,
    teacherName: String,
    capacity: String,
    isEdit: Boolean,
    isLoading: Boolean,
    teachers: List<User>,
    onNameChange: (String) -> Unit,
    onTeacherChange: (String) -> Unit,
    onCapacityChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = AppBg,
        topBar = {
            GlassHeader(
                title = if (isEdit) "تعديل بيانات الفصل" else "إضافة فصل جديد",
                onBack = onBack,
                gradient = RawdatyGradients.AdminHeader,
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
            // Introductory Card
            Surface(
                color = AmberLight.copy(0.3f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, AmberPrimary.copy(0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier.size(52.dp).clip(CircleShape).background(White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.School, null, tint = AmberPrimary, modifier = Modifier.size(28.dp))
                    }
                    Column(Modifier.weight(1f)) {
                        Text(
                            "هوية الفصل الدراسي", 
                            style = MaterialTheme.typography.titleSmall, 
                            fontWeight = FontWeight.Black, 
                            color = BluePrimary,
                            fontFamily = CairoFontFamily
                        )
                        Text(
                            "تحديد المسمى وسعة الفصل يساعد في تنظيم توزيع الطلاب بشكل آلي.", 
                            style = MaterialTheme.typography.bodySmall, 
                            color = Gray600,
                            fontFamily = CairoFontFamily
                        )
                    }
                }
            }

            // Main Form Card
            RawdatyCard(elevation = 1.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Text(
                        "البيانات الأساسية",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = BlueDark,
                        fontFamily = CairoFontFamily
                    )
                    
                    RawdatyField(
                        value = name,
                        onValueChange = onNameChange,
                        label = "اسم الفصل (المسمى الرسمي)",
                        placeholder = "مثال: فصل براعم المستقبل",
                        leadingIcon = Icons.Default.Class
                    )

                    // Teacher Selection with Modern Dropdown
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "المعلمة المسؤولة", 
                            style = MaterialTheme.typography.labelMedium, 
                            fontWeight = FontWeight.Bold, 
                            color = Gray700,
                            fontFamily = CairoFontFamily
                        )
                        ExposedDropdownMenuBox(
                            expanded = dropdownExpanded,
                            onExpandedChange = { dropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = teacherName.ifEmpty { "اختر المعلمة من القائمة" },
                                onValueChange = {},
                                readOnly = true,
                                leadingIcon = { Icon(Icons.Default.PersonOutline, null, tint = BluePrimary) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(dropdownExpanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(16.dp),
                                textStyle = LocalTextStyle.current.copy(fontFamily = CairoFontFamily),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BluePrimary,
                                    unfocusedBorderColor = Gray200,
                                    focusedContainerColor = Gray50,
                                    unfocusedContainerColor = Gray50
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false },
                                modifier = Modifier.background(White)
                            ) {
                                DropdownMenuItem(
                                    text = { Text("قيد التعيين لاحقاً", fontFamily = CairoFontFamily, color = Gray400) },
                                    onClick = { onTeacherChange(""); dropdownExpanded = false }
                                )
                                teachers.forEach { teacher ->
                                    DropdownMenuItem(
                                        text = { Text(teacher.name, fontFamily = CairoFontFamily, fontWeight = FontWeight.Medium) },
                                        onClick = { onTeacherChange(teacher.name); dropdownExpanded = false }
                                    )
                                }
                            }
                        }
                    }

                    RawdatyDivider()

                    RawdatyField(
                        value = capacity,
                        onValueChange = onCapacityChange,
                        label = "السعة القصوى (عدد المقاعد)",
                        placeholder = "مثال: 25",
                        leadingIcon = Icons.Default.Groups
                    )
                }
            }

            // Real-time Insight
            if (capacity.isNotEmpty()) {
                Surface(
                    color = White,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Gray100)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Analytics, null, tint = MintPrimary, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "معاينة السعة الاستيعابية", 
                                style = MaterialTheme.typography.labelSmall, 
                                color = Gray500, 
                                fontWeight = FontWeight.Bold,
                                fontFamily = CairoFontFamily
                            )
                        }
                        LinearProgressIndicator(
                            progress = { 0.0f }, // New class is 0/capacity
                            modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
                            color = MintPrimary,
                            trackColor = Gray100
                        )
                        Text(
                            "متاح $capacity مقاعد شاغرة حالياً في هذا الفصل.", 
                            style = MaterialTheme.typography.labelSmall, 
                            color = MintPrimary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = CairoFontFamily
                        )
                    }
                }
            }

            // Action Buttons Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(bottom = 32.dp)) {
                RawdatyButton(
                    text = if (isEdit) "تحديث بيانات الفصل" else "تأكيد إضافة الفصل",
                    onClick = onSave,
                    isLoading = isLoading,
                    icon = if (isEdit) Icons.Default.Save else Icons.Default.AddCircle,
                    modifier = Modifier.fillMaxWidth()
                )
                
                TextButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "إلغاء والعودة", 
                        color = Gray400, 
                        style = MaterialTheme.typography.labelLarge,
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun AdminAddClassroomPreview() {
    RawdatyTheme {
        AdminAddClassroomScreen(
            name = "",
            teacherName = "",
            capacity = "25",
            isEdit = false,
            isLoading = false,
            teachers = emptyList(),
            onNameChange = {},
            onTeacherChange = {},
            onCapacityChange = {},
            onSave = {},
            onBack = {}
        )
    }
}
