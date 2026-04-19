package org.mohanned.rawdatyci_cdapp.presentation.screens.admin

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.mohanned.rawdatyci_cdapp.domain.model.User
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AddEditClassroomEffect
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AddEditClassroomIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AddEditClassroomState
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AdminAddEditClassroomViewModel
import org.koin.compose.viewmodel.koinViewModel

data class AdminAddClassroomScreen(val classId: String? = null) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: AdminAddEditClassroomViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(classId) {
            viewModel.onIntent(AddEditClassroomIntent.Load(classId))
        }

        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is AddEditClassroomEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                    AddEditClassroomEffect.SaveSuccess -> navigator.pop()
                }
            }
        }

        AdminAddEditClassroomScreenContent(
            state = state,
            snackbarHostState = snackbarHostState,
            onIntent = viewModel::onIntent,
            onBack = { navigator.pop() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAddEditClassroomScreenContent(
    state: AddEditClassroomState,
    snackbarHostState: SnackbarHostState,
    onIntent: (AddEditClassroomIntent) -> Unit,
    onBack: () -> Unit,
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = AppBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            GlassHeader(
                title = if (state.isEditMode) "تعديل بيانات الفصل" else "إضافة فصل جديد",
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
            state.error?.let {
                Surface(color = ColorError.copy(0.1f), shape = RoundedCornerShape(16.dp)) {
                    Text(it, color = ColorError, modifier = Modifier.padding(16.dp), fontFamily = CairoFontFamily)
                }
            }

            RawdatyCard(elevation = 1.dp, containerColor = White) {
                Column(verticalArrangement = Arrangement.spacedBy(20.dp), modifier = Modifier.padding(16.dp)) {
                    Text("البيانات الأساسية", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = BlueDark, fontFamily = CairoFontFamily)

                    RawdatyField(
                        value = state.name,
                        onValueChange = { onIntent(AddEditClassroomIntent.NameChanged(it)) },
                        label = "اسم الفصل (المسمى الرسمي)",
                        placeholder = "مثال: فصل براعم المستقبل",
                        leadingIcon = Icons.Default.Class
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("المعلمة المسؤولة", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Gray700, fontFamily = CairoFontFamily)
                        ExposedDropdownMenuBox(expanded = dropdownExpanded, onExpandedChange = { dropdownExpanded = it }) {
                            OutlinedTextField(
                                value = state.teacherName.ifEmpty { "اختر المعلمة من القائمة" },
                                onValueChange = {},
                                readOnly = true,
                                leadingIcon = { Icon(Icons.Default.PersonOutline, null, tint = BluePrimary) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(dropdownExpanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(16.dp),
                                textStyle = LocalTextStyle.current.copy(fontFamily = CairoFontFamily, color = Color.Black),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BluePrimary, 
                                    unfocusedBorderColor = Gray200, 
                                    focusedContainerColor = Gray50, 
                                    unfocusedContainerColor = Gray50,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                )
                            )
                            ExposedDropdownMenu(expanded = dropdownExpanded, onDismissRequest = { dropdownExpanded = false }, modifier = Modifier.background(White)) {
                                state.teachers.forEach { teacher ->
                                    DropdownMenuItem(
                                        text = { Text(teacher.name, fontFamily = CairoFontFamily, fontWeight = FontWeight.Medium, color = Color.Black) },
                                        onClick = { onIntent(AddEditClassroomIntent.TeacherChanged(teacher.id, teacher.name)); dropdownExpanded = false }
                                    )
                                }
                            }
                        }
                    }

                    RawdatyField(
                        value = state.capacity,
                        onValueChange = { onIntent(AddEditClassroomIntent.CapacityChanged(it)) },
                        label = "السعة القصوى (عدد المقاعد)",
                        placeholder = "مثال: 25",
                        leadingIcon = Icons.Default.Groups
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(bottom = 32.dp)) {
                RawdatyButton(
                    text = if (state.isEditMode) "تحديث بيانات الفصل" else "تأكيد إضافة الفصل",
                    onClick = { onIntent(AddEditClassroomIntent.Save) },
                    isLoading = state.isSaving,
                    icon = if (state.isEditMode) Icons.Default.Save else Icons.Default.AddCircle,
                    modifier = Modifier.fillMaxWidth()
                )
                TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp)) {
                    Text("إلغاء والعودة", color = Gray500, style = MaterialTheme.typography.labelLarge, fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
