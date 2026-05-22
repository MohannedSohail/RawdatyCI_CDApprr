package org.mohanned.rawdatyci_cdapp.presentation.screens.admin

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AdminAddEditUserEffect
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AdminAddEditUserIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AdminAddEditUserViewModel
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AdminAddEditUserState
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class AdminAddEditUserScreen(val userId: String? = null) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: AdminAddEditUserViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(userId) {
            if (userId != null) {
                viewModel.onIntent(AdminAddEditUserIntent.LoadUser(userId))
            }
            viewModel.effect.collect { effect ->
                when (effect) {
                    is AdminAddEditUserEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                    AdminAddEditUserEffect.NavigateBack -> navigator.pop()
                }
            }
        }

        AdminAddEditUserScreenContent(
            state = state,
            isEdit = userId != null,
            snackbarHostState = snackbarHostState,
            onIntent = viewModel::onIntent,
            onBack = { navigator.pop() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddEditUserScreenContent(
    state: AdminAddEditUserState,
    isEdit: Boolean,
    snackbarHostState: SnackbarHostState,
    onIntent: (AdminAddEditUserIntent) -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    
    var showClassPicker by remember { mutableStateOf(false) }

    // هندلة اختيار التاريخ
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.fromEpochMilliseconds(millis)
                            .toLocalDateTime(TimeZone.UTC).date.toString()
                        onIntent(AdminAddEditUserIntent.ChildBirthDateChanged(date))
                    }
                    showDatePicker = false
                }) { Text("تأكيد", fontFamily = CairoFontFamily) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("إلغاء", fontFamily = CairoFontFamily) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        containerColor = AppBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            GlassHeader(
                title = if (isEdit) "تعديل بيانات" else "إضافة حساب جديد",
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
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            
            // ✅ 1. قسم اختيار نوع المستخدم (تغيير مسمى معلم)
            AnimateEntrance(delay = 100) {
                RawdatyCard(elevation = 2.dp, containerColor = White, shape = RoundedCornerShape(24.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AdminPanelSettings, null, tint = BluePrimary, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(10.dp))
                            Text("نوع الحساب", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Gray900, fontFamily = CairoFontFamily)
                        }
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            listOf("parent" to "ولي أمر", "teacher" to "معلم").forEach { (id, label) ->
                                val selected = state.role == id
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (selected) BluePrimary else Gray50)
                                        .border(1.5.dp, if (selected) BluePrimary else Gray200, RoundedCornerShape(16.dp))
                                        .clickable { onIntent(AdminAddEditUserIntent.RoleChanged(id)) }
                                        .padding(vertical = 14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(label, color = if (selected) White else Gray700, fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // ✅ 2. قسم المعلومات الأساسية
            AnimateEntrance(delay = 200) {
                RawdatyCard(elevation = 2.dp, containerColor = White, shape = RoundedCornerShape(24.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Badge, null, tint = BluePrimary, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(10.dp))
                            Text("بيانات الحساب", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Gray900, fontFamily = CairoFontFamily)
                        }
                        
                        RawdatyField(
                            value = state.name,
                            onValueChange = { onIntent(AdminAddEditUserIntent.NameChanged(it)) },
                            label = "الاسم الكامل (بالعربي)",
                            leadingIcon = Icons.Default.Person
                        )

                        if (!isEdit) {
                            RawdatyField(
                                value = state.usernameEn,
                                onValueChange = { onIntent(AdminAddEditUserIntent.UsernameEnChanged(it)) },
                                label = "اسم الدخول (بالإنجليزي فقط)",
                                placeholder = "مثال: mohammed.ali",
                                leadingIcon = Icons.Default.Language
                            )

                            Column {
                                RawdatyField(
                                    value = state.email,
                                    onValueChange = { },
                                    label = "البريد الإلكتروني المولد",
                                    leadingIcon = Icons.Default.AlternateEmail,
                                    enabled = false
                                )
                                Text("سيتم استخدام هذا البريد لتسجيل الدخول", fontSize = 10.sp, color = Gray400, modifier = Modifier.padding(start = 12.dp, top = 4.dp), fontFamily = CairoFontFamily)
                            }

                            RawdatyField(
                                value = state.password,
                                onValueChange = { onIntent(AdminAddEditUserIntent.PasswordChanged(it)) },
                                label = "كلمة المرور المؤقتة",
                                leadingIcon = Icons.Default.Lock,
                                isPassword = true
                            )
                        }

                        RawdatyField(
                            value = state.phone,
                            onValueChange = { onIntent(AdminAddEditUserIntent.PhoneChanged(it)) },
                            label = "رقم التواصل",
                            leadingIcon = Icons.Default.Phone
                        )
                    }
                }
            }

            // ✅ 3. قسم بيانات الطفل
            AnimatedVisibility(
                visible = state.role == "parent",
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                AnimateEntrance(delay = 300) {
                    RawdatyCard(elevation = 2.dp, containerColor = White, shape = RoundedCornerShape(24.dp), accentBorder = AmberPrimary.copy(0.2f)) {
                        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ChildCare, null, tint = AmberPrimary, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(10.dp))
                                Text("بيانات الطفل", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Gray900, fontFamily = CairoFontFamily)
                            }

                            RawdatyField(
                                value = state.childName,
                                onValueChange = { onIntent(AdminAddEditUserIntent.ChildNameChanged(it)) },
                                label = "اسم الطفل الرباعي",
                                leadingIcon = Icons.Default.Face
                            )

                            // اختيار الصف ✅
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = state.classes.find { it.id == state.childClassId }?.name ?: "لم يتم اختيار فصل",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("فصل الطفل", fontFamily = CairoFontFamily) },
                                    leadingIcon = { Icon(Icons.Default.School, null, tint = BluePrimary) },
                                    trailingIcon = { 
                                        IconButton(onClick = { showClassPicker = true }) {
                                            Icon(Icons.Default.ArrowDropDown, null)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().clickable { showClassPicker = true },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = BluePrimary,
                                        unfocusedBorderColor = Gray200,
                                        unfocusedTextColor = Gray400,
                                        disabledTextColor = Gray400
                                    )
                                )

                                DropdownMenu(
                                    expanded = showClassPicker,
                                    onDismissRequest = { showClassPicker = false },
                                    modifier = Modifier.fillMaxWidth(0.8f).background(White)
                                ) {
                                    state.classes.forEach { classroom ->
                                        DropdownMenuItem(
                                            text = { Text(classroom.name, fontFamily = CairoFontFamily, color = Gray900) },
                                            onClick = {
                                                onIntent(AdminAddEditUserIntent.ChildClassChanged(classroom.id))
                                                showClassPicker = false
                                            }
                                        )
                                    }
                                    if (state.classes.isEmpty()) {
                                        DropdownMenuItem(
                                            text = { Text("لا توجد فصول متاحة", fontFamily = CairoFontFamily, color = Gray900) },
                                            onClick = { showClassPicker = false }
                                        )
                                    }
                                }
                            }

                            // حقل تاريخ الميلاد مع DatePicker ✅
                            Box(modifier = Modifier.clickable { showDatePicker = true }) {
                                RawdatyField(
                                    value = state.childBirthDate,
                                    onValueChange = { },
                                    label = "تاريخ الميلاد",
                                    leadingIcon = Icons.Default.Cake,
                                    enabled = false,
                                    placeholder = "اضغط لاختيار التاريخ"
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("جنس الطفل:", style = MaterialTheme.typography.labelLarge, color = Gray700, fontFamily = CairoFontFamily, modifier = Modifier.padding(start = 8.dp))
                                listOf("male" to "ذكر", "female" to "أنثى").forEach { (id, label) ->
                                    val selected = state.childGender == id
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (selected) BluePrimary else Gray50)
                                            .border(1.dp, if (selected) BluePrimary else Gray200, RoundedCornerShape(12.dp))
                                            .clickable { onIntent(AdminAddEditUserIntent.ChildGenderChanged(id)) }
                                            .padding(horizontal = 20.dp, vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(label, color = if (selected) White else Gray600, fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            AnimateEntrance(delay = 400) {
                RawdatyButton(
                    text = if (isEdit) "حفظ التعديلات" else "إتمام إنشاء الحساب",
                    onClick = { onIntent(AdminAddEditUserIntent.Save) },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    isLoading = state.isSaving
                )
            }
            
            Spacer(Modifier.height(40.dp))
        }
    }
}
