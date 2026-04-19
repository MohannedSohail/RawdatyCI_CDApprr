package org.mohanned.rawdatyci_cdapp.presentation.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.presentation.components.GlassHeader
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyButton
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyCard
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyField
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.SettingsIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.SettingsState
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.SettingsViewModel

object AdminSettingsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: SettingsViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.onIntent(SettingsIntent.Load)
        }

        AdminSettingsScreenContent(
            state = state,
            onKindergartenNameChange = { viewModel.onIntent(SettingsIntent.KindergartenNameChanged(it)) },
            onAddressChange = { viewModel.onIntent(SettingsIntent.AddressChanged(it)) },
            onPhoneChange = { viewModel.onIntent(SettingsIntent.PhoneChanged(it)) },
            onSave = { viewModel.onIntent(SettingsIntent.Save) },
            onBack = { navigator.pop() }
        )
    }
}

@Composable
fun AdminSettingsScreenContent(
    state: SettingsState,
    onKindergartenNameChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = AppBg,
        topBar = {
            GlassHeader(
                title = "إعدادات الروضة",
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            RawdatyCard(containerColor = White, elevation = 1.dp) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("المعلومات العامة", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = BlueDark, fontFamily = CairoFontFamily)
                    
                    RawdatyField(
                        value = state.kindergartenName,
                        onValueChange = onKindergartenNameChange,
                        label = "اسم الروضة",
                        leadingIcon = Icons.Default.Business
                    )

                    RawdatyField(
                        value = state.address,
                        onValueChange = onAddressChange,
                        label = "العنوان",
                        leadingIcon = Icons.Default.LocationOn
                    )

                    RawdatyField(
                        value = state.phone,
                        onValueChange = onPhoneChange,
                        label = "رقم التواصل",
                        leadingIcon = Icons.Default.Phone
                    )
                }
            }

            RawdatyButton(
                text = "حفظ الإعدادات",
                onClick = onSave,
                isLoading = state.isSaving,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )

            state.error?.let {
                Text(it, color = ColorError, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontFamily = CairoFontFamily)
            }
        }
    }
}
