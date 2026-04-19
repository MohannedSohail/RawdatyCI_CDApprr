package org.mohanned.rawdatyci_cdapp.presentation.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

@Composable
fun OfflineBanner(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(AmberLight.copy(0.7f))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.CloudOff,
                    null,
                    tint = AmberPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    "أنت تتصفح بدون إنترنت - يتم عرض البيانات المخزنة",
                    style = MaterialTheme.typography.labelSmall,
                    color = AmberPrimary,
                    fontWeight = FontWeight.Bold,
                    fontFamily = CairoFontFamily
                )
            }
        }
    }
}

@Composable
fun SyncStatusIndicator(
    status: org.mohanned.rawdatyci_cdapp.domain.model.SyncStatus,
    modifier: Modifier = Modifier
) {
    if (status == org.mohanned.rawdatyci_cdapp.domain.model.SyncStatus.SYNCED) return

    val color = if (status == org.mohanned.rawdatyci_cdapp.domain.model.SyncStatus.PENDING) BluePrimary else AmberPrimary
    val text = if (status == org.mohanned.rawdatyci_cdapp.domain.model.SyncStatus.PENDING) "بانتظار المزامنة..." else "جاري المزامنة..."

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(0.1f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = color,
            fontWeight = FontWeight.Bold,
            fontFamily = CairoFontFamily
        )
    }
}
