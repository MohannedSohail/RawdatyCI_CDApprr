package org.mohanned.rawdatyci_cdapp.presentation.screens.parent

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ComplaintsEffect
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ComplaintsIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ComplaintsViewModel

object ParentAddComplaintScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ComplaintsViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        var type by remember { mutableStateOf("complaint") } // complaint, suggestion
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is ComplaintsEffect.ShowMessage -> {
                        snackbarHostState.showSnackbar(effect.message)
                        if (effect.message.contains("نجاح")) {
                            navigator.pop()
                        }
                    }
                }
            }
        }

        Scaffold(
            containerColor = AppBg,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                GlassHeader(
                    title = "إرسال شكوى أو اقتراح",
                    onBack = { navigator.pop() },
                    gradient = RawdatyGradients.Primary,
                    headerHeight = 120.dp
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    "يسعدنا استماع آراءكم ومقترحاتكم لتطوير خدماتنا.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = CairoFontFamily,
                    color = Gray600
                )

                // اختيار النوع
                RawdatyCard(containerColor = White) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("نوع الطلب", fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily, fontSize = 14.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            SelectableChip(
                                label = "شكوى", 
                                isSelected = type == "complaint", 
                                onClick = { type = "complaint" }
                            )
                            SelectableChip(
                                label = "اقتراح", 
                                isSelected = type == "suggestion", 
                                onClick = { type = "suggestion" }
                            )
                        }
                    }
                }

                RawdatyField(
                    value = title,
                    onValueChange = { title = it },
                    label = "العنوان",
                    placeholder = "اكتب عنواناً ملخصاً...",
                    leadingIcon = Icons.Default.Title
                )

                // حقل النص
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    placeholder = { Text("اكتب تفاصيل طلبك هنا...", fontFamily = CairoFontFamily) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = White,
                        focusedContainerColor = White,
                        unfocusedBorderColor = Gray200,
                        focusedBorderColor = BluePrimary
                    )
                )

                RawdatyButton(
                    text = "إرسال الآن",
                    onClick = { viewModel.onIntent(ComplaintsIntent.Submit(title, content, type)) },
                    icon = Icons.Default.Send,
                    isLoading = state.isActionLoading,
                    enabled = content.isNotBlank() && title.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                )
            }
        }
    }
}

@Composable
private fun SelectableChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (isSelected) BluePrimary else Gray50,
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) null else BorderStroke(1.dp, Gray200)
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
            color = if (isSelected) White else Gray700,
            fontWeight = FontWeight.Bold,
            fontFamily = CairoFontFamily
        )
    }
}
