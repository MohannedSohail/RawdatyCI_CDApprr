package org.mohanned.rawdatyci_cdapp.presentation.screens.admin

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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.domain.model.News
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.screens.auth.LoginScreen
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.*

object AdminDashboardScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: DashboardViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        var selectedNavIndex by remember { mutableStateOf(0) }
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            viewModel.onIntent(DashboardIntent.Load)
        }

        LaunchedEffect(Unit) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    DashboardEffect.NavigateToLogin -> navigator.replaceAll(LoginScreen())
                    is DashboardEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                }
            }
        }

        val navItems = listOf(
            BottomNavItem("الرئيسية", Icons.Outlined.GridView, Icons.Filled.GridView),
            BottomNavItem("الفصول", Icons.Outlined.Groups, Icons.Filled.Groups),
            BottomNavItem("المستخدمين", Icons.Outlined.PersonAdd, Icons.Filled.PersonAdd),
            BottomNavItem("الإعدادات", Icons.Outlined.Settings, Icons.Filled.Settings)
        )

        Scaffold(
            containerColor = AppBg,
            contentWindowInsets = WindowInsets(
                0,
                0,
                0,
                0
            ), // للسماح للهيدر بالوصول للأعلى بدون فواصل بيضاء
            bottomBar = {
                RawdatyBottomNav(
                    items = navItems,
                    selectedIndex = selectedNavIndex,
                    onSelect = { selectedNavIndex = it }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier.fillMaxSize().padding(bottom = padding.calculateBottomPadding())
            ) {
                when (selectedNavIndex) {
                    0 -> AdminDashboardHomeContent(
                        state = state,
                        onQuickAction = { action ->
                            when (action) {
                                "الفصول" -> navigator.push(AdminAddClassroomScreen(null))
                                "المستخدمون" -> navigator.push(AdminAddEditUserScreen(null))
                                "الأخبار" -> navigator.push(AdminAddNewsScreen(null))
                                "الشكاوى" -> navigator.push(AdminComplaintsScreen)
                                "إرسال تعميم" -> navigator.push(AdminSendNotificationScreen)
                                "التعميمات" -> navigator.push(AdminNewsScreen)
                                "أولياء الأمور" -> navigator.push(FilteredUsersScreen("parent"))
                                "المعلمين" -> navigator.push(FilteredUsersScreen("teacher"))
                                "الفصول الدراسية" -> navigator.push(AdminClassroomsScreen)
                            }
                        }
                    )

                    1 -> AdminClassroomsContentWrapper()
                    2 -> AdminUsersContentWrapper()
                    3 -> AdminSettingsScreenContentWrapper(
                        onLogout = { viewModel.onIntent(DashboardIntent.Logout) }
                    )
                }
            }
        }
    }
}

data class FilteredUsersScreen(val role: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: UsersViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(role) {
            viewModel.onIntent(UsersIntent.LoadUsers(role = role))
        }

        AdminUsersScreenContent(
            state = state,
            initialRole = role,
            onUserClick = { user -> navigator.push(AdminUserDetailsScreen(user.id)) },
            onDelete = { viewModel.onIntent(UsersIntent.DeleteUser(it.id)) },
            onAdd = { navigator.push(AdminAddEditUserScreen(null)) },
            onBack = { navigator.pop() },
            onRefresh = { viewModel.onIntent(UsersIntent.LoadUsers(role = role)) },
            onSearch = { q -> viewModel.onIntent(UsersIntent.LoadUsers(role = role, search = q)) },
            onTabChanged = { r -> viewModel.onIntent(UsersIntent.LoadUsers(role = r)) },
            onLoadMore = {
                viewModel.onIntent(
                    UsersIntent.LoadUsers(
                        role = role,
                        page = state.page + 1
                    )
                )
            }
        )
    }
}

@Composable
private fun AdminClassroomsContentWrapper() {
    val viewModel: ClassroomsViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val navigator = LocalNavigator.currentOrThrow
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.onIntent(ClassroomsIntent.Load)
    }

    AdminClassroomsScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent,
        onClassClick = { navigator.push(AdminClassDetailScreen(it.id)) },
        onAdd = { navigator.push(AdminAddClassroomScreen(null)) },
        onBack = { /* التبويبات لا تحتاج زر رجوع */ }
    )
}

@Composable
private fun AdminUsersContentWrapper() {
    val viewModel: UsersViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val navigator = LocalNavigator.currentOrThrow

    LaunchedEffect(Unit) {
        viewModel.onIntent(UsersIntent.LoadUsers())
    }

    AdminUsersScreenContent(
        state = state,
        initialRole = null, // هنا نمرر الـ role لكي يتم تحديد التبويب (Tab) الصحيح
        onUserClick = { user -> navigator.push(AdminUserDetailsScreen(user.id)) },
        onDelete = { viewModel.onIntent(UsersIntent.DeleteUser(it.id)) },
        onAdd = { navigator.push(AdminAddEditUserScreen(null)) },
        onBack = { /* التبويبات لا تحتاج زر رجوع */ },
        onRefresh = { viewModel.onIntent(UsersIntent.LoadUsers()) },
        onSearch = { q -> viewModel.onIntent(UsersIntent.LoadUsers(search = q)) },
        onTabChanged = { role -> viewModel.onIntent(UsersIntent.LoadUsers(role = role)) },
        onLoadMore = { viewModel.onIntent(UsersIntent.LoadUsers(page = state.page + 1)) }
    )
}

@Composable
private fun AdminSettingsScreenContentWrapper(onLogout: () -> Unit) {
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
        onBack = { /* التبويبات لا تحتاج زر رجوع */ }
    )
}

@Composable
fun AdminDashboardHomeContent(
    state: DashboardState,
    onQuickAction: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // الهيدر مع دعم RTL وإزالة الفراغات البيضاء
        Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
            WaveHeader(
                title = "لوحة التحكم",
                subtitle = "نظام رَوْضَتِي المتكامل",
                gradient = RawdatyGradients.AdminHeader,
                headerHeight = 220.dp
            )

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                        .padding(bottom = 55.dp, start = 16.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    RawdatyAvatar(
                        name = state.user?.name ?: "Admin",
                        size = 50.dp,
                        gradient = RawdatyGradients.AvatarMint
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            "مرحباً،",
                            style = MaterialTheme.typography.labelMedium,
                            color = White.copy(0.7f),
                            fontFamily = CairoFontFamily
                        )
                        Text(
                            state.user?.name ?: "المدير",
                            style = MaterialTheme.typography.titleLarge,
                            color = White,
                            fontWeight = FontWeight.Black,
                            fontFamily = CairoFontFamily
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
            AnimateEntrance(delay = 200) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        label = "أولياء الأمور",
                        value = state.totalParents.toString(),
                        icon = Icons.Default.Groups,
                        color = BluePrimary,
                        gradient = RawdatyGradients.Primary,
                        modifier = Modifier.weight(1f)
                    ) { onQuickAction("أولياء الأمور") }
                    StatCard(
                        label = "المعلمين / المعلمات",
                        value = state.totalTeachers.toString(),
                        icon = Icons.Default.School,
                        color = MintPrimary,
                        gradient = RawdatyGradients.AvatarMint,
                        modifier = Modifier.weight(1f)
                    ) { onQuickAction("المعلمين") }
                }
            }

            Column {
                AnimateEntrance(delay = 300) {
                    StatCard(
                        label = "الفصول الدراسية",
                        value = state.totalClasses.toString(),
                        icon = Icons.Default.Class,
                        color = AmberPrimary,
                        gradient = RawdatyGradients.AvatarAmber,
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                    ) { onQuickAction("الفصول الدراسية") }
                }

                Spacer(Modifier.height(24.dp))
                SectionHeader("الوصول السريع")
                AnimateEntrance(delay = 400) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                    ) {
                        QuickActionItem(
                            "إضافة فصل",
                            Icons.Default.AddHomeWork,
                            BluePrimary
                        ) { onQuickAction("الفصول") }
                        QuickActionItem(
                            "الشكاوى",
                            Icons.Default.Feedback,
                            AmberPrimary
                        ) { onQuickAction("الشكاوى") }
                        QuickActionItem(
                            " اضافة خبر",
                            Icons.Default.Newspaper,
                            Color.DarkGray
                        ) { onQuickAction("الأخبار") }

                        QuickActionItem(
                            "إضافة مستخدم",
                            Icons.Default.PersonAdd,
                            MintPrimary
                        ) { onQuickAction("المستخدمون") }
                        QuickActionItem(
                            "إرسال تعميم",
                            Icons.Default.Campaign,
                            ColorError
                        ) { onQuickAction("إرسال تعميم") }

                    }
                }

//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    SectionHeader(
//                        "آخر الأخبار",
//                        actionText = "عرض الكل",
//                        onSeeAll = { onQuickAction("التعميمات") })
//                    AnimateEntrance(delay = 500) {
//                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
//                            state.recentNews.take(3).forEach { item ->
//                                RecentNewsItem(item) { onQuickAction("التعميمات") }
//                            }
//                        }
//                    }
//                }

                if (state.isLoading) {
                    Box(
                        Modifier.fillMaxWidth().height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BluePrimary)
                    }
                } else if (state.recentNews.isEmpty()) {
                    EmptyStateCard("لا توجد أخبار حالياً")
                } else {
                    if (state.recentNews.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        SectionHeader(
                            "آخر الأخبار",
                            actionText = "عرض الكل",
                            onSeeAll = { onQuickAction("التعميمات") })
                        AnimateEntrance(delay = 500) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                state.recentNews.take(3).forEach { item ->
                                    RecentNewsItem(item) { onQuickAction("التعميمات") }
                                }
                            }
                        }
                    }
                }

            }
            Spacer(Modifier.height(40.dp))
        }
    }

}


@Composable
fun EmptyStateCard(text: String) {
    Box(
        modifier = Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(16.dp))
            .background(White).border(1.dp, Gray100, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Gray400, fontFamily = CairoFontFamily)
    }
}

@Composable
fun DashboardNewsItem(news: News) {
    RawdatyCard(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        onClick = {}
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)).background(Gray100),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Newspaper, null, tint = BluePrimary)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    news.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    fontFamily = CairoFontFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    news.body,
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray500,
                    fontFamily = CairoFontFamily,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = Gray300)
        }
    }
}

//@Composable
//fun StatCard(
//    label: String,
//    value: String,
//    icon: ImageVector,
//    color: Color,
//    gradient: androidx.compose.ui.graphics.Brush,
//    modifier: Modifier = Modifier,
//    onClick: () -> Unit
//) {
//    RawdatyCard(onClick = onClick, modifier = modifier) {
//        Row(
//            modifier = Modifier.padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Column(Modifier.weight(1f)) {
//                Text(
//                    value,
//                    style = MaterialTheme.typography.headlineMedium,
//                    fontWeight = FontWeight.Black,
//                    color = Gray900,
//                    fontFamily = CairoFontFamily
//                )
//                Text(
//                    label,
//                    style = MaterialTheme.typography.labelSmall,
//                    color = Gray500,
//                    fontFamily = CairoFontFamily
//                )
//            }
//            Box(
//                modifier = Modifier.size(40.dp).clip(CircleShape).background(gradient),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(icon, null, tint = White, modifier = Modifier.size(20.dp))
//            }
//        }
//    }
//}

@Composable
private fun RecentNewsItem(news: News, onClick: () -> Unit) {
    RawdatyCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = White,
        onClick = onClick,
        elevation = 2.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                Modifier.size(40.dp).clip(RoundedCornerShape(12.dp))
                    .background(if (news.isVisible) AmberLight else BlueLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (news.isVisible) Icons.Default.PushPin else Icons.Default.Description,
                    null,
                    tint = if (news.isVisible) AmberPrimary else BluePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column(Modifier.weight(1f)) {
                Text(
                    news.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = CairoFontFamily,
                    color = Color.Black
                )
                Text(
                    news.createdAt,
                    style = MaterialTheme.typography.labelSmall,
                    color = Gray400,
                    fontFamily = CairoFontFamily
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                null,
                tint = Gray300,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun QuickActionItem(label: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.width(95.dp).clip(RoundedCornerShape(24.dp)).clickable { onClick() }
            .padding(vertical = 12.dp)
    ) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(20.dp),
            color = color.copy(0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    null,
                    tint = color,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = Gray800,
            fontWeight = FontWeight.SemiBold,
            fontFamily = CairoFontFamily,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
