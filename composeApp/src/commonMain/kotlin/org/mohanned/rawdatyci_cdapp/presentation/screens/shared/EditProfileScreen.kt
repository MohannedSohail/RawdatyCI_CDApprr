package org.mohanned.rawdatyci_cdapp.presentation.screens.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mohanned.rawdatyci_cdapp.domain.model.LoggedUser
import org.mohanned.rawdatyci_cdapp.domain.model.UserRole
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyButton
import org.mohanned.rawdatyci_cdapp.presentation.components.RawdatyField
import org.mohanned.rawdatyci_cdapp.presentation.theme.AppBg
import org.mohanned.rawdatyci_cdapp.presentation.theme.BlueLight
import org.mohanned.rawdatyci_cdapp.presentation.theme.BluePrimary
import org.mohanned.rawdatyci_cdapp.presentation.theme.CairoFontFamily
import org.mohanned.rawdatyci_cdapp.presentation.theme.MintPrimary
import org.mohanned.rawdatyci_cdapp.presentation.theme.RawdatyTheme
import org.mohanned.rawdatyci_cdapp.presentation.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    user: LoggedUser,
    onBack: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(user.name) }
    var email by remember { mutableStateOf(user.email) }
    var phone by remember { mutableStateOf("0599123456") } // Dummy current phone

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "تعديل الملف الشخصي",
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Avatar Selection
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(BlueLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "",
                        modifier = Modifier.size(50.dp),
                        tint = BluePrimary
                    )
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MintPrimary)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            RawdatyField(
                value = name,
                onValueChange = { name = it },
                label = "الاسم الكامل",
                leadingIcon = Icons.Default.Person
            )

            RawdatyField(
                value = email,
                onValueChange = { email = it },
                label = "البريد الإلكتروني",
                leadingIcon = Icons.Default.Email
            )

            RawdatyField(
                value = phone,
                onValueChange = { phone = it },
                label = "رقم الهاتف",
                leadingIcon = Icons.Default.Phone
            )

            Spacer(modifier = Modifier.weight(1f))

            RawdatyButton(
                text = "حفظ التغييرات",
                onClick = { onSave(name, email, phone) },
                icon = Icons.Default.Save,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun EditProfilePreview() {
    RawdatyTheme {
        EditProfileScreen(
            user = LoggedUser(
                "1",
                "محمد علي",
                "mohammed@example.com",
                UserRole.TEACHER,
                null,
                "null"
            ),
            onBack = {},
            onSave = { _, _, _ -> }
        )
    }
}
