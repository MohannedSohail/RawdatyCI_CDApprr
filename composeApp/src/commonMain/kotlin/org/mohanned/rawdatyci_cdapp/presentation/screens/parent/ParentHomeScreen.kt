package org.mohanned.rawdatyci_cdapp.presentation.screens.parent

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.screens.auth.LoginScreen
import org.mohanned.rawdatyci_cdapp.presentation.screens.shared.NotificationsScreen
import org.mohanned.rawdatyci_cdapp.presentation.screens.shared.ProfileScreen
import org.mohanned.rawdatyci_cdapp.presentation.screens.teacher.ChatConversationsScreen
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.*

object ParentHomeScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ParentHomeViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            viewModel.onIntent(ParentHomeIntent.Load)
        }

        Scaffold(
            containerColor = AppBg,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                Surface(color = White, shadowElevation = 8.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        IconButton(onClick = { /* Stay on Home */ }) { Icon(Icons.Default.Home, null, tint = BluePrimary) }
                        IconButton(onClick = { navigator.push(ChatConversationsScreen) }) { Icon(Icons.Default.ChatBubbleOutline, null, tint = Gray400) }
                        IconButton(onClick = { navigator.push(NotificationsScreen) }) { Icon(Icons.Default.NotificationsNone, null, tint = Gray400) }
                        IconButton(onClick = { navigator.push(ProfileScreen) }) { Icon(Icons.Default.PersonOutline, null, tint = Gray400) }
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                WaveHeader(
                    title = "أهلاً بك، ${state.parentName}",
                    subtitle = "نحن هنا لخدمتكم وراحة أطفالكم",
                    gradient = RawdatyGradients.Splash,
                    headerHeight = 220.dp
                )

                Column(
                    modifier = Modifier.padding(horizontal = 20.dp).offset(y = (-40).dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    if (state.isLoading) {
                        ShimmerBox(Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(24.dp)))
                    } else if (state.children.isEmpty()) {
                        EmptyState(title = "لا يوجد أطفال", subtitle = "لم يتم تسجيل أطفال لكِ بعد", icon = Icons.Default.ChildCare)
                    } else {
                        state.children.forEach { child ->
                            ChildStatusCard(child = child, onClick = { 
                                navigator.push(ParentChildDetailScreen(child.id))
                            })
                        }
                    }

                    SectionHeader("الوصول السريع")
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ParentActionItem("الألعاب", Icons.Default.Games, BluePrimary) { /* navigator.push(GamesHubScreen()) */ }
                        ParentActionItem("الرسائل", Icons.Default.Chat, MintPrimary) { navigator.push(ChatConversationsScreen) }
                        ParentActionItem("الشكاوى", Icons.Default.Feedback, AmberPrimary) { navigator.push(ParentComplaintScreen) }
                    }
                    
                    Spacer(Modifier.height(20.dp))
                }
            }
        }
    }
}

@Composable
private fun ChildStatusCard(child: Child, onClick: () -> Unit) {
    RawdatyCard(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        onClick = onClick,
        containerColor = White,
        elevation = 4.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            RawdatyAvatar(child.fullName, size = 64.dp, gradient = if (child.gender == "male") RawdatyGradients.AvatarBlue else RawdatyGradients.AvatarMint)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(child.fullName, style = MaterialTheme.typography.titleLarge, color = Gray900, fontWeight = FontWeight.Black, fontFamily = CairoFontFamily)
                Text(child.className, style = MaterialTheme.typography.labelSmall, color = Gray500, fontFamily = CairoFontFamily)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                    Box(Modifier.size(10.dp).clip(CircleShape).background(ColorSuccess))
                    Text("متواجد بالمدرسة", style = MaterialTheme.typography.bodySmall, color = ColorSuccess, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
                }
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = Gray300, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
private fun ParentActionItem(label: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.width(100.dp).clip(RoundedCornerShape(20.dp)).clickable { onClick() }.padding(8.dp)
    ) {
        Surface(modifier = Modifier.size(56.dp), shape = RoundedCornerShape(16.dp), color = color.copy(0.1f)) {
            Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = color, modifier = Modifier.size(28.dp)) }
        }
        Text(label, style = MaterialTheme.typography.labelMedium, color = Gray800, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily, textAlign = TextAlign.Center)
    }
}
