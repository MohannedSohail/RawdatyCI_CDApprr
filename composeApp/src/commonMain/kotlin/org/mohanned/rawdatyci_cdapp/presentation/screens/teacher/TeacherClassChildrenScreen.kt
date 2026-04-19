package org.mohanned.rawdatyci_cdapp.presentation.screens.teacher

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.presentation.components.EmptyState
import org.mohanned.rawdatyci_cdapp.presentation.components.GlassHeader
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyAvatar
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyCard
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyField
import org.mohanned.rawdatyci_cdapp.presentation.theme.AmberLight
import org.mohanned.rawdatyci_cdapp.presentation.theme.AmberPrimary
import org.mohanned.rawdatyci_cdapp.presentation.theme.AppBg
import org.mohanned.rawdatyci_cdapp.presentation.theme.BlueDark
import org.mohanned.rawdatyci_cdapp.presentation.theme.BluePrimary
import org.mohanned.rawdatyci_cdapp.presentation.theme.CairoFontFamily
import org.mohanned.rawdatyci_cdapp.presentation.theme.Gray500
import org.mohanned.rawdatyci_cdapp.presentation.theme.RawdatyGradients
import org.mohanned.rawdatyci_cdapp.presentation.theme.White
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ChildrenState

@Composable
fun TeacherClassChildrenUI(
    className: String,
    state: ChildrenState,
    onStudentClick: (Child) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredChildren =
        state.children.filter { it.fullName.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        containerColor = AppBg,
        topBar = {
            GlassHeader(
                title = "طلاب $className",
                onBack = onBack,
                backgroundColor = BluePrimary
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // Search Bar
            Box(modifier = Modifier.padding(16.dp)) {
                RawdatyField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = "ابحث عن طالب...",
                    leadingIcon = Icons.Default.Search,
                    backgroundColor = White
                )
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BluePrimary)
                }
            } else if (filteredChildren.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.PersonSearch,
                    title = "لا يوجد طلاب",
                    subtitle = "لم يتم العثور على أي طلاب في هذا الفصل حالياً."
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredChildren) { child ->
                        StudentListItem(child = child, onClick = { onStudentClick(child) })
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentListItem(child: Child, onClick: () -> Unit) {
    RawdatyCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = 1.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RawdatyAvatar(
                name = child.fullName,
                size = 52.dp,
                gradient = if (child.gender == "male") RawdatyGradients.AvatarBlue else RawdatyGradients.AvatarMint
            )

            Column(Modifier.weight(1f)) {
                Text(
                    child.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BlueDark,
                    fontFamily = CairoFontFamily
                )
                Text(
                    "آخر تحديث: ${child.enrollmentDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Gray500,
                    fontFamily = CairoFontFamily
                )
            }

            // Quick Info Badge (Stars)
            Surface(
                color = AmberLight.copy(0.3f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        null,
                        tint = AmberPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        child.stars.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Black,
                        color = AmberPrimary
                    )
                }
            }
        }
    }
}
