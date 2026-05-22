package org.mohanned.rawdatyci_cdapp.presentation.screens.parent

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.Games
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.presentation.components.AnimateEntrance
import org.mohanned.rawdatyci_cdapp.presentation.components.BottomNavItem
import org.mohanned.rawdatyci_cdapp.presentation.components.ParentHomeHeader
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyAvatar
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyBottomNav
import org.mohanned.rawdatyci_cdapp.presentation.components.SectionHeader
import org.mohanned.rawdatyci_cdapp.presentation.components.ShimmerBox
import org.mohanned.rawdatyci_cdapp.presentation.screens.shared.NotificationsScreen
import org.mohanned.rawdatyci_cdapp.presentation.screens.shared.ProfileScreen
import org.mohanned.rawdatyci_cdapp.presentation.screens.teacher.ChatConversationsScreen
import org.mohanned.rawdatyci_cdapp.presentation.theme.AmberPrimary
import org.mohanned.rawdatyci_cdapp.presentation.theme.AppBg
import org.mohanned.rawdatyci_cdapp.presentation.theme.BlueDark
import org.mohanned.rawdatyci_cdapp.presentation.theme.BluePrimary
import org.mohanned.rawdatyci_cdapp.presentation.theme.CairoFontFamily
import org.mohanned.rawdatyci_cdapp.presentation.theme.Gray300
import org.mohanned.rawdatyci_cdapp.presentation.theme.Gray500
import org.mohanned.rawdatyci_cdapp.presentation.theme.MintPrimary
import org.mohanned.rawdatyci_cdapp.presentation.theme.RawdatyGradients
import org.mohanned.rawdatyci_cdapp.presentation.theme.White
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ParentHomeIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ParentHomeViewModel

object ParentHomeScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var selectedNavIndex by remember { mutableStateOf(0) }

        val navItems = listOf(
            BottomNavItem("الرئيسية", Icons.Outlined.Home, Icons.Filled.Home),
            BottomNavItem("الرسائل", Icons.Outlined.ChatBubbleOutline, Icons.Filled.ChatBubble),
            BottomNavItem("التنبيهات", Icons.Outlined.NotificationsNone, Icons.Filled.Notifications),
            BottomNavItem("حسابي", Icons.Outlined.PersonOutline, Icons.Filled.Person)
        )

        Scaffold(
            containerColor = AppBg,
            bottomBar = {
                RawdatyBottomNav(
                    items = navItems,
                    selectedIndex = selectedNavIndex,
                    onSelect = { selectedNavIndex = it }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = padding.calculateBottomPadding())
            ) {
                AnimatedContent(
                    targetState = selectedNavIndex,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith 
                        fadeOut(animationSpec = tween(300))
                    },
                    label = "tab_transition"
                ) { targetIndex ->
                    when (targetIndex) {
                        0 -> HomeTabContent(
                            onTabSelect = { selectedNavIndex = it },
                            onNotificationClick = { selectedNavIndex = 2 }
                        )
                        1 -> ChatConversationsScreen.Content()
                        2 -> NotificationsScreen.Content()
                        3 -> ProfileScreen.Content()
                    }
                }
            }
        }
    }
}

@Composable
fun HomeTabContent(onTabSelect: (Int) -> Unit, onNotificationClick: () -> Unit) {
    val navigator = LocalNavigator.currentOrThrow
    val viewModel: ParentHomeViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onIntent(ParentHomeIntent.Load)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ParentHomeHeader(
            parentName = state.parentName,
            onNotificationClick = onNotificationClick,
            onProfileClick = { onTabSelect(3) }
        )

        AnimateEntrance {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp).offset(y = (-25).dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))


                SectionHeader("الخدمات السريعة")

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ServiceCard("الأخبار", Icons.Outlined.Newspaper, BluePrimary, Modifier.weight(1f)) {
                        navigator.push(ParentNewsScreen)
                    }
                    ServiceCard("الشكاوى", Icons.Outlined.Feedback, AmberPrimary, Modifier.weight(1f)) {
                        navigator.push(ParentComplaintsScreen)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ServiceCard("الألعاب", Icons.Outlined.Games, MintPrimary, Modifier.weight(1f)) {
                        val childId = state.children.firstOrNull()?.id ?: "dummy_1"
                        navigator.push(ParentGamesScreen(childId))
                    }
                    ServiceCard("المحادثات", Icons.Outlined.Chat, BlueDark, Modifier.weight(1f)) {
                        onTabSelect(1)
                    }
                }

                Spacer(Modifier.height(30.dp))

                Text(
                    "أطفالي المسجلين",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = BlueDark,
                    fontFamily = CairoFontFamily
                )

                if (state.isLoading) {
                    ShimmerBox(Modifier.fillMaxWidth().height(96.dp).clip(RoundedCornerShape(24.dp)))
                } else {
                    state.children.forEach { child ->
                        ModernChildCard(child) { 
                            navigator.push(ParentChildDetailScreen(child.id)) 
                        }
                    }
                }



                Spacer(Modifier.height(30.dp))
            }
        }
    }
}
@Composable
private fun ModernChildCard(child: Child, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = White,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth().height(96.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RawdatyAvatar(
                child.fullName,
                size = 60.dp,
                gradient = if (child.gender == "male") RawdatyGradients.AvatarBlue else RawdatyGradients.AvatarMint
            )
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(child.fullName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BlueDark, fontFamily = CairoFontFamily)
                Text(child.className, color = Gray500, fontSize = 12.sp, fontFamily = CairoFontFamily)

                Row(modifier = Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Stars, null, tint = AmberPrimary, modifier = Modifier.size(14.dp))
                    Text("${child.stars} نجوم", color = AmberPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
                }
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = Gray300)
        }
    }
}

@Composable
private fun ServiceCard(title: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = White,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 4.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(48.dp).background(color.copy(0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(title, fontWeight = FontWeight.Bold, color = BlueDark, fontSize = 13.sp, fontFamily = CairoFontFamily)
        }
    }
}
