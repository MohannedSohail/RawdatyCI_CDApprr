package org.mohanned.rawdatyci_cdapp.presentation.screens.admin

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.NotificationsIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.NotificationsViewModel

object AdminSendNotificationScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: NotificationsViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        AdminSendNotificationScreenContent(
            title = state.title,
            body = state.body,
            target = state.target,
            isLoading = state.isActionLoading,
            onTitleChange = { viewModel.onIntent(NotificationsIntent.TitleChanged(it)) },
            onBodyChange = { viewModel.onIntent(NotificationsIntent.BodyChanged(it)) },
            onTargetChange = { viewModel.onIntent(NotificationsIntent.TargetChanged(it)) },
            onSend = { viewModel.onIntent(NotificationsIntent.Send) },
            onBack = { navigator.pop() }
        )
    }
}

@Composable
fun AdminSendNotificationScreenContent(
    title: String,
    body: String,
    target: String,
    isLoading: Boolean,
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
            RawdatyCard(containerColor = AmberLight.copy(0.4f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(16.dp)) {
                    Box(
                        modifier = Modifier.size(52.dp).clip(CircleShape).background(White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Campaign, null, tint = AmberPrimary, modifier = Modifier.size(28.dp))
                    }
                    Column(Modifier.weight(1f)) {
                        Text("تواصل فوري وشامل", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black, color = BluePrimary, fontFamily = CairoFontFamily)
                        Text("سيتم إرسال هذا التنبيه كإشعار دفع (Push) للفئة المستهدفة فوراً.", style = MaterialTheme.typography.bodySmall, color = Gray600, fontFamily = CairoFontFamily)
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("الفئة المستهدفة بالتنبيه", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Gray700, fontFamily = CairoFontFamily)
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
                                    color = if (isSelected) White else Gray500,
                                    fontFamily = CairoFontFamily
                                )
                            }
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                RawdatyField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = "عنوان التنبيه",
                    placeholder = "مثال: إعلان هام بخصوص الرحلة",
                    leadingIcon = Icons.Default.Title
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("محتوى الإشعار", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Gray700, fontFamily = CairoFontFamily)
                    OutlinedTextField(
                        value = body,
                        onValueChange = onBodyChange,
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                        shape = RoundedCornerShape(16.dp),
                        placeholder = { Text("اكتب تفاصيل التنبيه هنا باختصار ووضوح...", color = Gray400, fontFamily = CairoFontFamily) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BluePrimary,
                            unfocusedBorderColor = Gray200,
                            focusedContainerColor = Gray50,
                            unfocusedContainerColor = Gray50
                        )
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            RawdatyButton(
                text = "إرسال التنبيه الآن",
                onClick = onSend,
                isLoading = isLoading,
                icon = Icons.AutoMirrored.Filled.Send,
                backgroundColor = BluePrimary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
