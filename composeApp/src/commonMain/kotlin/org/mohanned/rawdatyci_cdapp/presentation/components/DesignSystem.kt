package org.mohanned.rawdatyci_cdapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.mohanned.rawdatyci_cdapp.presentation.theme.*

/**
 * نظام مكونات "روضتي" الموحد
 */

@Composable
fun RawdatyStatusTag(
    text: String,
    statusType: StatusType = StatusType.PENDING
) {
    val (bgColor, textColor) = when (statusType) {
        StatusType.PRESENT, StatusType.SUCCESS -> MintLight to MintPrimary
        StatusType.ABSENT, StatusType.ERROR -> ColorError.copy(alpha = 0.1f) to ColorError
        StatusType.PENDING, StatusType.WARNING -> AmberLight to AmberPrimary
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontFamily = CairoFontFamily
        )
    }
}

enum class StatusType { PRESENT, ABSENT, PENDING, SUCCESS, ERROR, WARNING }

@Composable
fun RawdatyEmptyState(
    title: String,
    description: String,
    icon: ImageVector = Icons.Default.Inbox
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(BlueLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BluePrimary,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Gray900,
            fontFamily = CairoFontFamily
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = Gray600,
            textAlign = TextAlign.Center,
            fontFamily = CairoFontFamily
        )
    }
}

@Composable
fun RawdatySuccessDialog(
    show: Boolean,
    title: String,
    message: String,
    onDismiss: () -> Unit
) {
    if (show) {
        Dialog(onDismissRequest = onDismiss) {
            RawdatyCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                containerColor = White,
                elevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MintPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, null, tint = White, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontFamily = CairoFontFamily
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Gray600,
                        textAlign = TextAlign.Center,
                        fontFamily = CairoFontFamily
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    RawdatyButton(
                        text = "حسناً",
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = MintPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun RawdatyConfirmDialog(
    show: Boolean,
    title: String,
    message: String,
    confirmText: String = "حذف",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = { Icon(Icons.Default.Delete, contentDescription = null, tint = ColorError) },
            title = { Text(title, fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold) },
            text = { Text(message, fontFamily = CairoFontFamily) },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(containerColor = ColorError)
                ) {
                    Text(confirmText, fontFamily = CairoFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("إلغاء", fontFamily = CairoFontFamily, color = Gray600)
                }
            },
            containerColor = White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
