package org.mohanned.rawdatyci_cdapp.presentation.screens.admin

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AddEditNewsEffect
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AddEditNewsIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AddEditNewsState
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AdminAddEditNewsViewModel

data class AdminAddNewsScreen(val newsId: String? = null) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: AdminAddEditNewsViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(newsId) {
            viewModel.onIntent(AddEditNewsIntent.Load(newsId))
        }

        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is AddEditNewsEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                    AddEditNewsEffect.SaveSuccess -> navigator.pop()
                }
            }
        }

        AdminAddNewsScreenContent(
            state = state,
            snackbarHostState = snackbarHostState,
            onIntent = viewModel::onIntent,
            onBack = { navigator.pop() }
        )
    }
}

@Composable
fun AdminAddNewsScreenContent(
    state: AddEditNewsState,
    snackbarHostState: SnackbarHostState,
    onIntent: (AddEditNewsIntent) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            GlassHeader(
                title = if (state.isEditMode) "تعديل الخبر" else "إضافة خبر جديد",
                onBack = onBack,
                gradient = RawdatyGradients.AdminHeader,
                headerHeight = 140.dp
            )
        },
        containerColor = AppBg
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Gray100)
                    .border(1.dp, Gray200, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.AddPhotoAlternate, 
                        contentDescription = null, 
                        tint = Gray400, 
                        modifier = Modifier.size(48.dp)
                    )
                    Text("إضافة صورة الخبر", color = Gray500, fontFamily = CairoFontFamily)
                }
            }

            RawdatyCard(elevation = 1.dp, containerColor = White) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(16.dp)) {
                    RawdatyField(
                        value = state.title,
                        onValueChange = { onIntent(AddEditNewsIntent.TitleChanged(it)) },
                        label = "عنوان الخبر",
                        placeholder = "اكتب عنواناً جذاباً...",
                        leadingIcon = Icons.Default.Title
                    )

                    OutlinedTextField(
                        value = state.body,
                        onValueChange = { onIntent(AddEditNewsIntent.BodyChanged(it)) },
                        modifier = Modifier.fillMaxWidth().height(150.dp),
                        label = { Text("محتوى الخبر", fontFamily = CairoFontFamily) },
                        placeholder = { Text("اكتب تفاصيل الخبر هنا...", fontFamily = CairoFontFamily) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BluePrimary, 
                            unfocusedBorderColor = Gray300,
                            focusedContainerColor = Gray50,
                            unfocusedContainerColor = Gray50
                        )
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = state.isVisible, 
                            onCheckedChange = { onIntent(AddEditNewsIntent.VisibilityChanged(it)) }, 
                            colors = CheckboxDefaults.colors(checkedColor = BluePrimary)
                        )
                        Text(
                            "ظهور الخبر للعامة", 
                            style = MaterialTheme.typography.bodyMedium, 
                            fontFamily = CairoFontFamily,
                            color = Gray800
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            RawdatyButton(
                text = if (state.isEditMode) "حفظ التعديلات" else "نشر الخبر الآن",
                onClick = { onIntent(AddEditNewsIntent.Save) },
                isLoading = state.isSaving,
                modifier = Modifier.fillMaxWidth()
            )
            
            state.error?.let {
                Text(it, color = ColorError, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontFamily = CairoFontFamily)
            }
        }
    }
}
