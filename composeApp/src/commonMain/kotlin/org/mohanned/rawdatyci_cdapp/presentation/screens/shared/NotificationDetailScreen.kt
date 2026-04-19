package org.mohanned.rawdatyci_cdapp.presentation.screens.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mohanned.rawdatyci_cdapp.domain.model.AppNotification
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyButton
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyCard
import org.mohanned.rawdatyci_cdapp.presentation.theme.AppBg
import org.mohanned.rawdatyci_cdapp.presentation.theme.BlueLight
import org.mohanned.rawdatyci_cdapp.presentation.theme.BluePrimary
import org.mohanned.rawdatyci_cdapp.presentation.theme.CairoFontFamily
import org.mohanned.rawdatyci_cdapp.presentation.theme.Gray100
import org.mohanned.rawdatyci_cdapp.presentation.theme.Gray500
import org.mohanned.rawdatyci_cdapp.presentation.theme.RawdatyTheme
import org.mohanned.rawdatyci_cdapp.presentation.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDetailScreen(
    notification: AppNotification,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "تفاصيل الإشعار",
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = AppBg)
            )
        },
        containerColor = AppBg
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(BlueLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    tint = BluePrimary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            RawdatyCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = White
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = BluePrimary,
                        fontFamily = CairoFontFamily
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = notification.createdAt,
                        style = MaterialTheme.typography.labelSmall,
                        color = Gray500,
                        fontFamily = CairoFontFamily
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Gray100)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = notification.body,
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = CairoFontFamily
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            RawdatyButton(
                text = "حسناً، فهمت",
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun NotificationDetailPreview() {
    val dummyNotification = AppNotification(
        id = "1",
        title = "تذكير: موعد اجتماع أولياء الأمور",
        body = "نود تذكيركم بموعد اجتماع أولياء الأمور القادم لمناقشة التطور الدراسي للأطفال في فصل البراعم، وذلك يوم الخميس القادم في تمام الساعة العاشرة صباحاً.",
        type = "REMAINDER",
        isRead = false,
        createdAt = "منذ ساعتين"
    )
    RawdatyTheme {
        NotificationDetailScreen(notification = dummyNotification, onBack = {})
    }
}
