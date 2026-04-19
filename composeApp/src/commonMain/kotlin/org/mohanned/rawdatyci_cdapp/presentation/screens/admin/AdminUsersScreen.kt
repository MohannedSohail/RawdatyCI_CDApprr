package org.mohanned.rawdatyci_cdapp.presentation.screens.admin

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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

object AdminUsersScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: UsersViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.onIntent(UsersIntent.LoadUsers())
        }

        AdminUsersScreenContent(
            state = state,
            initialRole = null,
            onUserClick = { user -> navigator.push(AdminUserDetailsScreen(user.id)) },
            onDelete = { viewModel.onIntent(UsersIntent.DeleteUser(it.id)) },
            onAdd = { navigator.push(AdminAddEditUserScreen(null)) },
            onBack = { navigator.pop() },
            onRefresh = { viewModel.onIntent(UsersIntent.LoadUsers()) },
            onSearch = { q -> viewModel.onIntent(UsersIntent.LoadUsers(search = q)) },
            onTabChanged = { role -> viewModel.onIntent(UsersIntent.LoadUsers(role = role)) },
            onLoadMore = {
                viewModel.onIntent(UsersIntent.LoadUsers(page = state.page + 1))
            }
        )
    }
}

@Composable
fun AdminUsersScreenContent(
    state: org.mohanned.rawdatyci_cdapp.presentation.viewmodel.UsersState,
    initialRole: String? = null,
    onUserClick: (User) -> Unit,
    onDelete: (User) -> Unit,
    onAdd: () -> Unit,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onSearch: (String) -> Unit,
    onTabChanged: (String?) -> Unit,
    onLoadMore: () -> Unit
) {
    val tabs = listOf("الكل", "المعلمات", "أولياء الأمور")
    val roles = listOf(null, "teacher", "parent")
    
    // استخدام remember(initialRole) لتحديث الـ tab المختار عند فتح الشاشة بدور معين
    var selectedTabIndex by remember(initialRole) { 
        mutableStateOf(roles.indexOf(initialRole).coerceAtLeast(0)) 
    }
    
    var searchQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    Scaffold(
        containerColor = AppBg,
        topBar = {
            GlassHeader(
                title = "إدارة المستخدمين",
                onBack = onBack,
                gradient = RawdatyGradients.AdminHeader,
                headerHeight = 140.dp
            )
        },
        floatingActionButton = {
            RawdatyFAB(onClick = onAdd, icon = Icons.Default.PersonAdd)
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            
            Surface(color = White, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        tabs.forEachIndexed { i, title ->
                            val selected = selectedTabIndex == i
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { 
                                        selectedTabIndex = i
                                        onTabChanged(roles[i])
                                    }
                                    .padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    title,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selected) BluePrimary else Gray400,
                                    fontFamily = CairoFontFamily
                                )
                                Spacer(Modifier.height(4.dp))
                                AnimatedVisibility(visible = selected) {
                                    Box(Modifier.size(24.dp, 3.dp).clip(CircleShape).background(BluePrimary))
                                }
                            }
                        }
                    }

                    Box(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { 
                                searchQuery = it
                                onSearch(it)
                            },
                            placeholder = { Text("ابحث بالاسم أو البريد...", fontFamily = CairoFontFamily, color = Gray400) },
                            leadingIcon = { Icon(Icons.Default.Search, null, tint = BluePrimary) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(fontFamily = CairoFontFamily),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BluePrimary,
                                unfocusedBorderColor = Gray200,
                                focusedContainerColor = Gray50,
                                unfocusedContainerColor = Gray50
                            )
                        )
                    }
                }
            }

            if (state.isLoading && state.users.isEmpty()) {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(6) { ShimmerBox(Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(16.dp))) }
                }
            } else if (state.error != null && state.users.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.CloudOff,
                    title = "خطأ في الاتصال",
                    subtitle = state.error,
                    actionText = "إعادة المحاولة",
                    onAction = onRefresh
                )
            } else if (state.users.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.PersonSearch,
                    title = "لا يوجد مستخدمين",
                    subtitle = "لم نتمكن من العثور على أي مستخدمين بهذا التصنيف",
                    actionText = "تحديث",
                    onAction = onRefresh
                )
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.users, key = { it.id }) { user ->
                        UserCardItem(
                            user = user,
                            onClick = { onUserClick(user) },
                            onDelete = { onDelete(user) }
                        )
                    }
                    if (state.isLoading) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = BluePrimary, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserCardItem(user: User, onClick: () -> Unit, onDelete: () -> Unit) {
    RawdatyCard(onClick = onClick, modifier = Modifier.fillMaxWidth(), containerColor = White) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(12.dp)) {
            Box {
                RawdatyAvatar(
                    name = user.name,
                    size = 48.dp,
                    gradient = when (user.role) {
                        UserRole.TEACHER -> RawdatyGradients.AvatarMint
                        UserRole.ADMIN, UserRole.SUPER_ADMIN -> RawdatyGradients.AvatarBlue
                        else -> RawdatyGradients.AvatarBlue
                    }
                )
                if (user.isActive) {
                    Box(Modifier.size(10.dp).clip(CircleShape).background(ColorSuccess).border(1.dp, White, CircleShape).align(Alignment.BottomEnd))
                }
            }

            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(user.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    RoleTag(role = when (user.role) {
                        UserRole.TEACHER -> "معلمة"
                        UserRole.ADMIN -> "مشرف"
                        UserRole.SUPER_ADMIN -> "مدير"
                        else -> "ولي أمر"
                    }, useSmallText = true)
                }
                Text(user.email, style = MaterialTheme.typography.labelSmall, color = Gray500, fontFamily = CairoFontFamily, maxLines = 1)
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.DeleteOutline, null, tint = ColorError.copy(0.7f), modifier = Modifier.size(20.dp))
            }
        }
    }
}
