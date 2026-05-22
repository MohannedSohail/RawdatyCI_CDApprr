package org.mohanned.rawdatyci_cdapp.presentation.screens.teacher

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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

data class TeacherClassChildrenScreen(val classId: String? = null, val className: String = "جميع الطلاب") : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ChildrenViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        var searchQuery by remember { mutableStateOf("") }

        LaunchedEffect(classId) {
            viewModel.onIntent(ChildrenIntent.Load(classId))
        }

        val filteredChildren = state.children.filter { 
            it.fullName.contains(searchQuery, ignoreCase = true) 
        }

        Scaffold(
            containerColor = AppBg,
            topBar = {
                ModernHeader(
                    title = className,
                    subtitle = "قائمة الطلاب والتقييمات",
                    onBack = { navigator.pop() },
                    gradient = RawdatyGradients.Primary,
                    headerHeight = 140.dp
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                // Search Bar
                Box(modifier = Modifier.padding(horizontal = 20.dp).offset(y = (-25).dp)) {
                    RawdatyCard(containerColor = White, elevation = 4.dp, shape = RoundedCornerShape(16.dp)) {
                        RawdatyField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = "ابحث عن اسم الطالب...",
                            leadingIcon = Icons.Default.Search,
                            backgroundColor = Color.Transparent
                        )
                    }
                }

                if (state.isLoading) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        repeat(6) { ShimmerBox(Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(16.dp))) }
                    }
                } else if (filteredChildren.isEmpty()) {
                    EmptyState(
                        icon = Icons.Default.PersonSearch,
                        title = "لا يوجد طلاب",
                        subtitle = "لم يتم العثور على أي طلاب حالياً."
                    )
                } else {
                    AnimateEntrance {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 30.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(filteredChildren) { child ->
                                StudentListItem(
                                    child = child, 
                                    onClick = { navigator.push(TeacherStudentDetailScreen(child.id)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentListItem(child: Child, onClick: () -> Unit) {
    RawdatyCard(
        modifier = Modifier.padding(horizontal = 20.dp),
        onClick = onClick,
        elevation = 2.dp,
        containerColor = White,
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // صورة الطالب/الأفاتار
            RawdatyAvatar(
                name = child.fullName,
                size = 64.dp,
                gradient = if (child.gender == "male") RawdatyGradients.AvatarBlue else RawdatyGradients.AvatarMint
            )

            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // اسم الطالب
                Text(
                    child.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BlueDark,
                    fontFamily = CairoFontFamily,
                    maxLines = 1
                )

                // صف التقييم (النجوم) واسم الفصل
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = AmberPrimary, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = child.stars.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = AmberPrimary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = CairoFontFamily
                        )
                    }
                    Text(
                        child.className,
                        style = MaterialTheme.typography.labelSmall,
                        color = Gray500,
                        fontFamily = CairoFontFamily
                    )
                }

                // بيانات ولي الأمر بالأسفل
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Person, null, tint = Gray400, modifier = Modifier.size(14.dp))
                        Text(
                            text = "ولي الأمر: ${child.parentName ?: "غير مسجل"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Gray600,
                            fontFamily = CairoFontFamily
                        )
                    }

                    // بيانات التواصل (الإيميل أو الهاتف)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.AlternateEmail, null, tint = Gray400, modifier = Modifier.size(14.dp))
                        Text(
                            text = child.parentPhone ?: "لا يوجد بيانات تواصل",
                            style = MaterialTheme.typography.labelSmall,
                            color = Gray500,
                            fontFamily = CairoFontFamily
                        )
                    }
                }
            }
        }
    }
}
