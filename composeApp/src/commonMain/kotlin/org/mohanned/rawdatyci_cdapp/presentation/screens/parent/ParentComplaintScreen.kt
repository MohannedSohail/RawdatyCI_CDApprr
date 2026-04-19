package org.mohanned.rawdatyci_cdapp.presentation.screens.parent

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ComplaintsIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ComplaintsViewModel

object ParentComplaintScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ComplaintsViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        var type by remember { mutableStateOf("complaint") }

        Scaffold(
            containerColor = AppBg,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                GlassHeader(
                    title = "إرسال شكوى أو مقترح",
                    onBack = { navigator.pop() },
                    gradient = RawdatyGradients.Splash,
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
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                RawdatyCard(containerColor = White, elevation = 1.dp) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("نوع الطلب", fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            listOf("complaint" to "شكوى", "suggestion" to "مقترح").forEach { (id, label) ->
                                val selected = type == id
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(if (selected) BluePrimary else Gray100, RoundedCornerShape(12.dp))
                                        .clickable { type = id }
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(label, color = if (selected) White else Gray700, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
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

                        OutlinedTextField(
                            value = content,
                            onValueChange = { content = it },
                            modifier = Modifier.fillMaxWidth().height(180.dp),
                            label = { Text("التفاصيل", fontFamily = CairoFontFamily) },
                            placeholder = { Text("اشرح لنا بالتفصيل لكي نتمكن من مساعدتك...", fontFamily = CairoFontFamily) },
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BluePrimary, unfocusedBorderColor = Gray200, focusedContainerColor = Gray50, unfocusedContainerColor = Gray50)
                        )
                    }
                }

                RawdatyButton(
                    text = "إرسال الآن",
                    onClick = { 
                        viewModel.onIntent(ComplaintsIntent.Submit(title, content, type))
                        navigator.pop()
                    },
                    isLoading = state.isActionLoading,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                )
            }
        }
    }
}
