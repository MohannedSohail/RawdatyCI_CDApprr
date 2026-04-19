package org.mohanned.rawdatyci_cdapp.presentation.screens.admin

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

@Composable
fun AdminAddEditUserScreenContent(
    state: AdminAddEditUserState,
    isEdit: Boolean,
    snackbarHostState: SnackbarHostState,
    onIntent: (AdminAddEditUserIntent) -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = AppBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            GlassHeader(
                title = if (isEdit) "تعديل مستخدم" else "إضافة مستخدم جديد",
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            RawdatyCard(elevation = 1.dp, containerColor = White) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(16.dp)) {
                    Text("المعلومات الأساسية", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = BlueDark, fontFamily = CairoFontFamily)
                    
                    RawdatyField(
                        value = state.name,
                        onValueChange = { onIntent(AdminAddEditUserIntent.NameChanged(it)) },
                        label = "الاسم الكامل",
                        leadingIcon = Icons.Default.Person
                    )

                    RawdatyField(
                        value = state.email,
                        onValueChange = { onIntent(AdminAddEditUserIntent.EmailChanged(it)) },
                        label = "البريد الإلكتروني",
                        leadingIcon = Icons.Default.Email,
                        enabled = !isEdit
                    )

                    RawdatyField(
                        value = state.phone,
                        onValueChange = { onIntent(AdminAddEditUserIntent.PhoneChanged(it)) },
                        label = "رقم الهاتف",
                        leadingIcon = Icons.Default.Phone
                    )
                    
                    if (!isEdit) {
                        RawdatyField(
                            value = state.password,
                            onValueChange = { onIntent(AdminAddEditUserIntent.PasswordChanged(it)) },
                            label = "كلمة المرور",
                            leadingIcon = Icons.Default.Lock,
                            isPassword = true
                        )
                    }
                }
            }

            RawdatyCard(elevation = 1.dp, containerColor = White) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(16.dp)) {
                    Text("الدور والصلاحيات", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = BlueDark, fontFamily = CairoFontFamily)
                    
                    Text("نوع المستخدم", style = MaterialTheme.typography.labelMedium, color = Gray700, fontFamily = CairoFontFamily)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("parent" to "ولي أمر", "teacher" to "معلمة").forEach { (id, label) ->
                            val selected = state.role == id
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (selected) BluePrimary else Gray100)
                                    .clickable { onIntent(AdminAddEditUserIntent.RoleChanged(id)) }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(label, color = if (selected) White else Gray700, fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            RawdatyButton(
                text = if (isEdit) "حفظ التعديلات" else "إضافة المستخدم",
                onClick = { onIntent(AdminAddEditUserIntent.Save) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                isLoading = state.isSaving
            )
            
            if (state.error != null) {
                Text(state.error, color = ColorError, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontFamily = CairoFontFamily)
            }
        }
    }
}
