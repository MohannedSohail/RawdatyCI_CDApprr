package org.mohanned.rawdatyci_cdapp.presentation.screens.admin

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.domain.model.User
import org.mohanned.rawdatyci_cdapp.domain.model.UserRole
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.UsersIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.UsersViewModel

data class AdminUserDetailsScreen(val userId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: UsersViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(userId) {
            viewModel.onIntent(UsersIntent.LoadUserForEdit(userId))
        }

        AdminUserDetailsScreenContent(
            user = state.currentUser,
            isLoading = state.isLoading,
            onBack = { navigator.pop() },
            onEdit = { navigator.push(AdminAddEditUserScreen(userId)) },
            onDelete = {
                viewModel.onIntent(UsersIntent.DeleteUser(userId))
                navigator.pop()
            }
        )
    }
}

@Composable
fun AdminUserDetailsScreenContent(
    user: User?,
    isLoading: Boolean,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteConfirmDialog(
            title = "تأكيد الحذف",
            message = "هل أنت متأكد من حذف هذا المستخدم؟ لا يمكن التراجع عن هذا الإجراء.",
            onConfirm = {
                showDeleteDialog = false
                onDelete()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Scaffold(
        containerColor = AppBg,
        topBar = {
            GlassHeader(
                title = "تفاصيل المستخدم",
                onBack = onBack,
                gradient = RawdatyGradients.AdminHeader,
                headerHeight = 140.dp
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BluePrimary)
            }
        } else if (user == null) {
            EmptyState(
                title = "المستخدم غير موجود",
                subtitle = "لم يتم العثور على بيانات المستخدم",
                icon = Icons.Default.PersonOff,
                actionText = "رجوع",
                onAction = onBack
            )
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RawdatyAvatar(
                    name = user.name,
                    size = 100.dp,
                    gradient = when (user.role) {
                        UserRole.TEACHER -> RawdatyGradients.AvatarMint
                        UserRole.ADMIN, UserRole.SUPER_ADMIN -> RawdatyGradients.AvatarBlue
                        else -> RawdatyGradients.AvatarAmber
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    user.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    fontFamily = CairoFontFamily
                )

                RoleTag(
                    role = when (user.role) {
                        UserRole.TEACHER -> "معلمة"
                        UserRole.ADMIN -> "مشرف"
                        UserRole.SUPER_ADMIN -> "مدير"
                        else -> "ولي أمر"
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                InfoCard(icon = Icons.Default.Email, label = "البريد الإلكتروني", value = user.email)
                InfoCard(icon = Icons.Default.Phone, label = "رقم الهاتف", value = user.phone ?: "غير متوفر")
                user.className?.let {
                    InfoCard(icon = Icons.Default.Class, label = "الفصل", value = it)
                }
                InfoCard(icon = Icons.Default.CalendarToday, label = "تاريخ الانضمام", value = user.createdAt)
                InfoCard(
                    icon = if (user.isActive) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    label = "الحالة",
                    value = if (user.isActive) "نشط" else "غير نشط",
                    valueColor = if (user.isActive) ColorSuccess else ColorError
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BluePrimary),
                        border = BorderStroke(1.dp, BluePrimary)
                    ) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تعديل", fontFamily = CairoFontFamily)
                    }

                    Button(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorError, contentColor = Color.White)
                    ) {
                        Icon(Icons.Default.Delete, null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("حذف", fontFamily = CairoFontFamily)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(icon: ImageVector, label: String, value: String, valueColor: Color = Color.Black) {
    RawdatyCard(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = 1.dp,
        containerColor = White
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(BlueLight), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = BluePrimary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelSmall, color = Gray500, fontFamily = CairoFontFamily)
                Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = valueColor, fontFamily = CairoFontFamily)
            }
        }
    }
}
