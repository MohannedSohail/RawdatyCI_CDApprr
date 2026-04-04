package org.mohanned.rawdatyci_cdapp.presentation.screens.admin

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import org.mohanned.rawdatyci_cdapp.domain.model.User
import org.mohanned.rawdatyci_cdapp.domain.model.UserRole
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

@Composable
fun AdminUsersScreen(
    users: List<User>,
    query: String,
    selectedTab: Int,
    isLoading: Boolean,
    isLoadingMore: Boolean,
    canLoadMore: Boolean,
    onSearch: (String) -> Unit,
    onLoadMore: () -> Unit,
    onTabChange: (Int) -> Unit,
    onUserClick: (User) -> Unit,
    onAdd: () -> Unit,
    onBack: () -> Unit,
) {
    val tabs = listOf("المعلمات", "أولياء الأمور")
    val listState = rememberLazyListState()

    // Infinite Scrolling Logic
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && lastVisibleIndex >= users.size - 2 && canLoadMore && !isLoadingMore && !isLoading) {
                    onLoadMore()
                }
            }
    }

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
            // Premium Animated Tabs
            Surface(
                color = White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    tabs.forEachIndexed { i, title ->
                        val selected = selectedTab == i
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onTabChange(i) }
                                .padding(vertical = 14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = if (selected) BluePrimary else Gray400,
                                fontFamily = CairoFontFamily
                            )
                            Spacer(Modifier.height(4.dp))
                            AnimatedVisibility(
                                visible = selected,
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Box(
                                    Modifier.size(32.dp, 3.dp).clip(CircleShape)
                                        .background(BluePrimary)
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar Section with Cairo
            Box(modifier = Modifier.fillMaxWidth().background(White).padding(16.dp)) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onSearch,
                    placeholder = {
                        Text(
                            "ابحث بالاسم أو البريد...",
                            fontFamily = CairoFontFamily,
                            color = Gray400
                        )
                    },
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

            if (isLoading) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(6) {
                        ShimmerBox(
                            Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(16.dp))
                        )
                    }
                }
            } else if (users.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.PersonSearch,
                    title = "لم يتم العثور على نتائج",
                    subtitle = "تأكد من كتابة الاسم بشكل صحيح أو جرب كلمات بحث أخرى",
                    actionText = "إضافة مستخدم جديد",
                    onAction = onAdd
                )
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(users, key = { it.id }) { user ->
                        UserRowItem(user = user, onClick = { onUserClick(user) })
                    }

                    if (isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = BluePrimary,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    item { Spacer(Modifier.height(80.dp)) } // FAB spacing
                }
            }
        }
    }
}

@Composable
private fun UserRowItem(user: User, onClick: () -> Unit) {
    RawdatyCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = 2.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box {
                RawdatyAvatar(
                    name = user.name,
                    size = 56.dp,
                    gradient = if (user.role == UserRole.TEACHER) RawdatyGradients.AvatarMint else RawdatyGradients.AvatarBlue
                )
                if (user.isActive) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(ColorSuccess)
                            .border(2.dp, White, CircleShape)
                            .align(Alignment.BottomEnd)
                    )
                }
            }

            Column(Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        user.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = Gray900,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CairoFontFamily,
                        modifier = Modifier.weight(1f, fill = false),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    RoleTag(
                        role = if (user.role == UserRole.TEACHER) "معلمة" else "ولي أمر",
                        useSmallText = true
                    )
                }
                Text(
                    user.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray500,
                    fontFamily = CairoFontFamily
                )
                if (user.className != null) {
                    Spacer(Modifier.height(4.dp))
                    Surface(
                        color = BlueLight.copy(0.4f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.School,
                                null,
                                tint = BluePrimary,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                user.className,
                                style = MaterialTheme.typography.labelSmall,
                                color = BluePrimary,
                                fontFamily = CairoFontFamily
                            )
                        }
                    }
                }
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

@Preview
@Composable
fun AdminUsersPreview() {
    RawdatyTheme {
        val dummyUsers = listOf(
            User(
                1,
                "سارة أحمد",
                "sara@rawdaty.com",
                "058844225",
                UserRole.TEACHER,
                "",
                true,
                classId = 1,
                className = "فصل النجوم",
                createdAt = "20 ابريل"
            ),
            User(
                2, "محمد العلي", "mohammed@mail.com", "05544223", UserRole.PARENT, "", true,
                classId = 2,
                className = "فصل الفيوم",
                createdAt = "15 مايو"
            ),
            User(
                3,
                "نورة خالد",
                "noura@rawdaty.com",
                "0501112223",
                UserRole.TEACHER,
                "",
                true,
                classId = 2,
                className = "فصل الأمل",
                createdAt = "15 مايو"
            )
        )
        AdminUsersScreen(
            users = dummyUsers,
            query = "",
            selectedTab = 0,
            isLoading = false,
            isLoadingMore = false,
            canLoadMore = true,
            onSearch = {},
            onLoadMore = {},
            onTabChange = {},
            onUserClick = {},
            onAdd = {},
            onBack = {}
        )
    }
}
