package org.mohanned.rawdatyci_cdapp.presentation.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mohanned.rawdatyci_cdapp.presentation.components.*
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminRolesScreen(
    onBack: () -> Unit
) {
    val roles = listOf(
        RoleItem("مدير نظام (Super Admin)", "صلاحيات كاملة للتحكم في الفصول والمستخدمين", true),
        RoleItem("معلمة (Teacher)", "تسجيل الحضور، الدردشة مع أولياء الأمور، إدارة الأخبار", false),
        RoleItem("ولي أمر (Parent)", "متابعة الطفل، الألعاب التعليمية، تقديم الشكاوى", false)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "الأدوار والصلاحيات",
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
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "إدارة أنواع المستخدمين في النظام وصلاحيات كل فئة.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gray600,
                    fontFamily = CairoFontFamily,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(roles) { role ->
                RoleCard(role)
            }
        }
    }
}

data class RoleItem(val name: String, val description: String, val isFullAccess: Boolean)

@Composable
fun RoleCard(role: RoleItem) {
    RawdatyCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Security, contentDescription = null, tint = BluePrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    role.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontFamily = CairoFontFamily
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                role.description,
                style = MaterialTheme.typography.bodySmall,
                color = Gray600,
                fontFamily = CairoFontFamily
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Gray100)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "تعديل الصلاحيات",
                    color = BluePrimary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    fontFamily = CairoFontFamily
                )
                RawdatyStatusTag(
                    text = if (role.isFullAccess) "وصول كامل" else "وصول محدود",
                    statusType = if (role.isFullAccess) StatusType.SUCCESS else StatusType.WARNING
                )
            }
        }
    }
}


@Preview
@Composable
fun AdminRolesPreview() {
    RawdatyTheme {
        AdminRolesScreen(onBack = {})
    }
}
