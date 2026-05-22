package org.mohanned.rawdatyci_cdapp.presentation.screens.teacher

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.*

data class TeacherStudentDetailScreen(val childId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ChildrenViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(childId) {
            viewModel.onIntent(ChildrenIntent.LoadChildDetail(childId))
        }

        Scaffold(
            containerColor = AppBg,
            topBar = {
                ModernHeader(
                    title = "ملف الطالب",
                    subtitle = state.currentChild?.fullName ?: "تحميل البيانات...",
                    onBack = { navigator.pop() },
                    gradient = RawdatyGradients.HeroBlue,
                    headerHeight = 150.dp
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                if (state.isLoading) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        ShimmerBox(Modifier.size(100.dp).clip(CircleShape).align(Alignment.CenterHorizontally))
                        repeat(3) { ShimmerBox(Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(20.dp))) }
                    }
                } else if (state.currentChild != null) {
                    val child = state.currentChild!!
                    AnimateEntrance {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // 1. الأفاتار والمعلومات الأساسية
                            Spacer(Modifier.height(10.dp))
                            RawdatyAvatar(
                                name = child.fullName, 
                                size = 100.dp, 
                                gradient = if (child.gender == "male") RawdatyGradients.AvatarBlue else RawdatyGradients.AvatarMint,
                            )
                            
                            Text(
                                child.fullName, 
                                style = MaterialTheme.typography.headlineSmall, 
                                fontWeight = FontWeight.Black, 
                                color = BlueDark, 
                                fontFamily = CairoFontFamily
                            )
                            
                            Surface(color = BlueLight.copy(0.4f), shape = RoundedCornerShape(10.dp)) {
                                Text(
                                    child.className, 
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    color = BluePrimary, 
                                    fontWeight = FontWeight.Bold, 
                                    fontFamily = CairoFontFamily,
                                    fontSize = 12.sp
                                )
                            }

                            // 2. بطاقة بيانات الطالب
                            StudentInfoCard(child)

                            // 3. قسم ولي الأمر والتواصل
                            ParentContactCard(child) {
                                // منطق التواصل: الانتقال لغرفة الدردشة
                                // navigator.push(ChatRoomScreen(child.parentId ?: "", child.parentName ?: "ولي الأمر"))
                            }
                            
                            Spacer(Modifier.height(30.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentInfoCard(child: Child) {
    RawdatyCard(containerColor = White, elevation = 2.dp) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Badge, null, tint = BluePrimary, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(12.dp))
                Text("المعلومات الشخصية", fontWeight = FontWeight.Bold, color = BlueDark, fontFamily = CairoFontFamily)
            }
            
            HorizontalDivider(color = Gray50)
            
            DetailRow(Icons.Outlined.Cake, "تاريخ الميلاد", child.dateOfBirth ?: "---")
            DetailRow(Icons.Outlined.Transgender, "الجنس", if (child.gender == "male") "ذكر" else "ذكر")
            DetailRow(Icons.Outlined.CalendarToday, "تاريخ التسجيل", child.enrollmentDate)
            
            if (!child.notes.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Text("ملاحظات إضافية:", style = MaterialTheme.typography.labelMedium, color = Gray500, fontFamily = CairoFontFamily)
                Text(child.notes!!, style = MaterialTheme.typography.bodySmall, color = Gray700, fontFamily = CairoFontFamily, lineHeight = 20.sp)
            }
        }
    }
}

@Composable
private fun ParentContactCard(child: Child, onChatClick: () -> Unit) {
    RawdatyCard(containerColor = White, elevation = 2.dp) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ContactPhone, null, tint = AmberPrimary, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(12.dp))
                Text("معلومات ولي الأمر", fontWeight = FontWeight.Bold, color = BlueDark, fontFamily = CairoFontFamily)
            }

            HorizontalDivider(color = Gray50)

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(Modifier.size(44.dp).clip(CircleShape).background(AmberLight.copy(0.4f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, null, tint = AmberPrimary)
                }
                Column(Modifier.weight(1f)) {
                    Text(child.parentName ?: "غير مسجل", fontWeight = FontWeight.Bold, color = Gray800, fontFamily = CairoFontFamily)
                    Text(child.parentPhone ?: "---", style = MaterialTheme.typography.labelSmall, color = Gray500, fontFamily = CairoFontFamily)
                }
            }

            RawdatyButton(
                text = "تواصل مع ولي الأمر",
                onClick = onChatClick,
                icon = Icons.AutoMirrored.Filled.Chat,
                backgroundColor = BluePrimary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DetailRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Icon(icon, null, tint = Gray400, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(12.dp))
        Text(label, modifier = Modifier.weight(1f), color = Gray500, style = MaterialTheme.typography.labelMedium, fontFamily = CairoFontFamily)
        Text(value, color = Gray900, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, fontFamily = CairoFontFamily)
    }
}
