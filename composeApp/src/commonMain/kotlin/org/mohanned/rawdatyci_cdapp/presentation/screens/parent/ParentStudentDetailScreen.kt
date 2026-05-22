package org.mohanned.rawdatyci_cdapp.presentation.screens.parent

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
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
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ChildrenIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ChildrenViewModel

data class ParentStudentDetailScreen(val initialChild: Child) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ChildrenViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        
        LaunchedEffect(initialChild.id) {
            viewModel.onIntent(ChildrenIntent.LoadChildDetail(initialChild.id))
        }

        val child = state.currentChild ?: initialChild

        Scaffold(
            containerColor = AppBg,
            topBar = {
                GlassHeader(
                    title = "تفاصيل طفلي",
                    onBack = { navigator.pop() },
                    gradient = RawdatyGradients.HeroBlue,
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
                if (state.isLoading) {
                    Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BluePrimary)
                    }
                }

                // بطاقة الطفل الأساسية
                RawdatyCard(containerColor = White, elevation = 2.dp) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RawdatyAvatar(
                            child.fullName, 
                            size = 100.dp, 
                            gradient = if (child.gender.lowercase() == "male") RawdatyGradients.AvatarBlue else RawdatyGradients.AvatarMint
                        )
                        Text(
                            child.fullName, 
                            style = MaterialTheme.typography.titleLarge, 
                            fontWeight = FontWeight.Bold, 
                            fontFamily = CairoFontFamily
                        )
                        Text(
                            "الفصل: ${child.className}", 
                            color = Gray500, 
                            fontFamily = CairoFontFamily
                        )
                    }
                }

                // بطاقة الإحصائيات (نجوم الطفل)
                RawdatyCard(containerColor = BluePrimary) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("النجوم المحصلة", color = White.copy(0.8f), fontSize = 12.sp, fontFamily = CairoFontFamily)
                            Text("${child.stars} نجوم", color = White, fontSize = 20.sp, fontWeight = FontWeight.Black, fontFamily = CairoFontFamily)
                        }
                        Icon(Icons.Default.Stars, null, tint = AmberPrimary, modifier = Modifier.size(48.dp))
                    }
                }

                SectionHeader("أدوات ولي الأمر")
                
                // زر الحضور
                ToolItem(
                    title = "سجل الحضور والغياب",
                    subtitle = "متابعة سجل حضور طفلك يومياً",
                    icon = Icons.Default.History,
                    color = MintPrimary,
                    onClick = { navigator.push(ParentAttendanceScreen(child.id, child.fullName)) }
                )

                // زر مراسلة المعلم
                ToolItem(
                    title = "تواصل مع المعلمة",
                    subtitle = "بدء محادثة مباشرة مع معلمة الفصل",
                    icon = Icons.AutoMirrored.Filled.Chat,
                    color = BluePrimary,
                    onClick = { 
                        // navigator.push(ChatRoomScreen(child.classId, child.className, child.fullName))
                    }
                )

                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun ToolItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    RawdatyCard(onClick = onClick, containerColor = White, elevation = 1.dp) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily, fontSize = 14.sp)
                Text(subtitle, color = Gray400, fontSize = 11.sp, fontFamily = CairoFontFamily)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = Gray300)
        }
    }
}
