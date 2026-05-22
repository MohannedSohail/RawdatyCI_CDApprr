package org.mohanned.rawdatyci_cdapp.presentation.screens.teacher

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.domain.model.Classroom
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.screens.shared.ProfileScreen
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.TeacherHomeIntent
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.TeacherHomeState
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.TeacherHomeViewModel

object TeacherHomeScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var selectedNavIndex by remember { mutableStateOf(0) }

        val navItems = listOf(
            BottomNavItem("الرئيسية", Icons.Outlined.Home, Icons.Filled.Home),
            BottomNavItem("فصولي", Icons.Outlined.Class, Icons.Filled.Class),
            BottomNavItem("الأخبار", Icons.Outlined.Newspaper, Icons.Filled.Newspaper),
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
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                AnimatedContent(
                    targetState = selectedNavIndex,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(400)) + slideInVertically(
                            initialOffsetY = { 20 },
                            animationSpec = tween(400)
                        ) togetherWith
                                fadeOut(animationSpec = tween(400))
                    },
                    label = "teacher_tab_transition"
                ) { targetIndex ->
                    when (targetIndex) {
                        0 -> TeacherHomeTabContent(
                            onComplaintsClick = { navigator.push(TeacherComplaintsScreen) },
                            onAllStudentsClick = {
                                navigator.push(
                                    TeacherClassChildrenScreen(
                                        classId = null,
                                        className = "جميع طلابي"
                                    )
                                )
                            },
                            onTabSelect = { selectedNavIndex = it }
                        )

                        1 -> TeacherMyClassesScreen.Content()
                        2 -> TeacherNewsScreen.Content()
                        3 -> ProfileScreen.Content()
                    }
                }
            }
        }
    }
}

@Composable
fun TeacherHomeTabContent(
    onComplaintsClick: () -> Unit,
    onAllStudentsClick: () -> Unit,
    onTabSelect: (Int) -> Unit
) {
    val navigator = LocalNavigator.currentOrThrow
    val viewModel: TeacherHomeViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onIntent(TeacherHomeIntent.Load)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TeacherHomeHeader(teacherName = state.teacherName)

        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .offset(y = (-25).dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // البطاقة الإحصائية (Stat Card)
            AnimateEntrance(delay = 100) {
                RawdatyCard(containerColor = White, elevation = 2.dp) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatItem(
                            "الفصول",
                            state.classes.size.toString(),
                            Icons.Default.Class,
                            BluePrimary
                        )
                        VerticalDivider(
                            modifier = Modifier
                                .height(40.dp)
                                .width(1.dp),
                            color = Gray100
                        )
                        StatItem(
                            "الحضور",
                            state.attendanceSummary,
                            Icons.Default.HowToReg,
                            MintPrimary
                        )
                        VerticalDivider(
                            modifier = Modifier
                                .height(40.dp)
                                .width(1.dp),
                            color = Gray100
                        )
                        StatItem(
                            "الطلاب",
                            state.totalStudentsCount.toString(),
                            Icons.Default.ChildCare,
                            AmberPrimary
                        )
                    }
                }
            }

            // ✅ قسم فصولي الدراسية (تصميم أصغر وأمتع)
            Column {
                SectionHeader(
                    title = "فصولي الدراسية",
                    actionText = "عرض الكل",
                    onSeeAll = { navigator.push(TeacherMyClassesScreen) }
                )

                if (state.isLoading) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(3) {
                            ShimmerBox(
                                Modifier
                                    .size(130.dp, 140.dp)
                                    .clip(RoundedCornerShape(22.dp))
                            )
                        }
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(start = 4.dp, end = 4.dp, bottom = 8.dp)
                    ) {
                        itemsIndexed(state.classes) { index, classroom ->
                            AnimateEntrance(delay = 200 + (index * 100)) {
                                TeacherClassCard(
                                    classroom = classroom,
                                    onClick = {
                                        navigator.push(
                                            TeacherAttendanceScreen(
                                                classroom.id,
                                                classroom.name
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // ✅ قسم الوصول السريع المحدث
            Column {
                SectionHeader("الخدمات السريعة")
                AnimateEntrance(delay = 400) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        ServiceCard(
                            "الشكاوى",
                            Icons.Outlined.Feedback,
                            AmberPrimary,
                            Modifier.weight(1f),
                            onClick = onComplaintsClick
                        )
                        ServiceCard(
                            "التعميمات",
                            Icons.Outlined.Campaign,
                            MintPrimary,
                            Modifier.weight(1f),
                            onClick = { onTabSelect(2) })
                    }
                }
                AnimateEntrance(delay = 500) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        ServiceCard(
                            "الطلاب",
                            Icons.Outlined.PeopleAlt,
                            BluePrimary,
                            Modifier.weight(1f),
                            onClick = onAllStudentsClick
                        )
                        ServiceCard(
                            "المحادثات",
                            Icons.Outlined.Chat,
                            BlueDark,
                            Modifier.weight(1f),
                            onClick = { onTabSelect(1) })
                    }
                }
            }

            // ✅ قسم طلاب فصلي
            Column {
                SectionHeader(
                    title = "طلاب فصلي",
                    actionText = "عرض الكل",
                    onSeeAll = onAllStudentsClick
                )

                if (state.isLoading) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(3) {
                            ShimmerBox(
                                Modifier
                                    .size(140.dp, 160.dp)
                                    .clip(RoundedCornerShape(22.dp))
                            )
                        }
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(start = 4.dp, end = 4.dp, bottom = 8.dp)
                    ) {
                        itemsIndexed(state.recentStudents) { index, student ->
                            AnimateEntrance(delay = 600 + (index * 100)) {
                                TeacherStudentHorizontalCard(
                                    student = student,
                                    onClick = { navigator.push(TeacherStudentDetailScreen(student.id)) }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
fun TeacherHomeHeader(teacherName: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(LocalWaveShape())
            .background(RawdatyGradients.Primary)
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "أهلاً بكِ،",
                style = MaterialTheme.typography.labelLarge,
                color = White.copy(0.7f),
                fontFamily = CairoFontFamily
            )
            Text(
                if (teacherName.isNotBlank()) teacherName else "المعلمة",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = White,
                fontFamily = CairoFontFamily
            )
            Text(
                "نتمنى لكِ يوماً دراسياً سعيداً وموفقاً",
                style = MaterialTheme.typography.bodySmall,
                color = White.copy(0.85f),
                fontFamily = CairoFontFamily,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun LocalWaveShape() = object : Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height - 40f)
            quadraticBezierTo(size.width / 2f, size.height + 40f, 0f, size.height - 40f)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
private fun StatItem(label: String, value: String, icon: ImageVector, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, null, tint = color.copy(0.6f), modifier = Modifier.size(20.dp))
        Text(
            value,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            color = Gray900,
            fontFamily = CairoFontFamily
        )
        Text(label, fontSize = 11.sp, color = Gray500, fontFamily = CairoFontFamily)
    }
}

@Composable
private fun TeacherClassCard(classroom: Classroom, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = White,
        shape = RoundedCornerShape(22.dp),
        shadowElevation = 3.dp,
        modifier = Modifier.width(135.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(BlueLight.copy(0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Groups,
                    null,
                    tint = BluePrimary,
                    modifier = Modifier.size(26.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    classroom.name,
                    fontWeight = FontWeight.ExtraBold,
                    color = BlueDark,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = CairoFontFamily
                )

                Spacer(Modifier.height(4.dp))

                Surface(
                    color = MintPrimary.copy(0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${classroom.childrenCount} طفل",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = MintPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = CairoFontFamily
                    )
                }
            }
        }
    }
}

@Composable
private fun ActivityItem(title: String, time: String, icon: ImageVector, color: Color) {
    RawdatyCard(containerColor = White, elevation = 1.dp) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Gray800,
                    fontFamily = CairoFontFamily
                )
                Text(time, fontSize = 11.sp, color = Gray400, fontFamily = CairoFontFamily)
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                null,
                tint = Gray300,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun ServiceCard(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = White,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 4.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(color.copy(0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(26.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(
                title,
                fontWeight = FontWeight.Bold,
                color = BlueDark,
                fontSize = 14.sp,
                fontFamily = CairoFontFamily
            )
        }
    }
}

@Composable
private fun TeacherStudentHorizontalCard(student: Child, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = White,
        shape = RoundedCornerShape(22.dp),
        shadowElevation = 3.dp,
        modifier = Modifier.width(140.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RawdatyAvatar(
                name = student.fullName,
                size = 50.dp,
                gradient = if (student.gender == "male") RawdatyGradients.AvatarBlue else RawdatyGradients.AvatarMint
            )
            Text(
                text = student.fullName,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = BlueDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = CairoFontFamily
            )
            Text(
                text = student.className,
                style = MaterialTheme.typography.labelSmall,
                color = Gray500,
                maxLines = 1,
                fontFamily = CairoFontFamily
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.Star, null, tint = AmberPrimary, modifier = Modifier.size(14.dp))
                Text(
                    student.stars.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BlueDark
                )
            }
        }
    }
}
