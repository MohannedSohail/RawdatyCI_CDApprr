package org.mohanned.rawdatyci_cdapp.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import org.jetbrains.compose.resources.painterResource
import org.mohanned.rawdatyci_cdapp.presentation.theme.*
import rawdatyci_cdapp.composeapp.generated.resources.Res
import rawdatyci_cdapp.composeapp.generated.resources.rawdatylogo

// ══════════════════════════════════════════════════════════════════════
//  MODELS
// ══════════════════════════════════════════════════════════════════════

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
)

// ══════════════════════════════════════════════════════════════════════
//  NAVIGATION COMPONENTS
// ══════════════════════════════════════════════════════════════════════

@Composable
fun RawdatyBottomNav(
    items: List<BottomNavItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    NavigationBar(
        containerColor = White,
        tonalElevation = 8.dp
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onSelect(index) },
                icon = {
                    Icon(
                        if (selectedIndex == index) item.selectedIcon else item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { 
                    Text(
                        item.label, 
                        fontFamily = CairoFontFamily,
                        fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Medium,
                        style = MaterialTheme.typography.labelSmall
                    ) 
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BluePrimary,
                    selectedTextColor = BluePrimary,
                    unselectedIconColor = Gray400,
                    unselectedTextColor = Gray400,
                    indicatorColor = BlueLight.copy(alpha = 0.3f)
                )
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════════
//  HEADER COMPONENTS
// ══════════════════════════════════════════════════════════════════════

@Composable
fun GlassHeader(
    title: String, 
    onBack: (() -> Unit)? = null, 
    backgroundColor: Color = BluePrimary,
    gradient: Brush? = null,
    subtitle: String? = null,
    headerHeight: Dp = 160.dp,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val finalGradient = gradient ?: Brush.verticalGradient(listOf(backgroundColor.copy(alpha = 0.9f), backgroundColor))
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(headerHeight)
            .background(finalGradient, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.padding(20.dp).fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (onBack != null) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(40.dp).background(White.copy(0.15f), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = White, modifier = Modifier.size(20.dp))
                    }
                } else {
                    Spacer(Modifier.width(40.dp))
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    actions()
                }
            }
            
            Spacer(Modifier.weight(1f))
            
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
                Text(
                    title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = CairoFontFamily
                )
                if (subtitle != null) {
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = White.copy(alpha = 0.8f),
                        fontFamily = CairoFontFamily
                    )
                }
            }
        }
    }
}

@Composable
fun RawdatyHeader(
    title: String, 
    onBack: () -> Unit, 
    gradient: Brush = RawdatyGradients.HeroBlue,
    subtitle: String? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(gradient)
            .statusBarsPadding()
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = White)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    title, 
                    style = MaterialTheme.typography.titleLarge, 
                    color = White, 
                    fontWeight = FontWeight.Bold,
                    fontFamily = CairoFontFamily
                )
                if (subtitle != null) {
                    Text(
                        subtitle, 
                        style = MaterialTheme.typography.labelMedium, 
                        color = White.copy(0.8f),
                        fontFamily = CairoFontFamily
                    )
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════
//  CARD & LAYOUT COMPONENTS
// ══════════════════════════════════════════════════════════════════════

@Composable
fun RawdatyCard(
    onClick: (() -> Unit)? = null, 
    modifier: Modifier = Modifier,
    backgroundColor: Color = White,
    containerColor: Color? = null,
    elevation: Dp = 2.dp,
    shape: Shape = RoundedCornerShape(20.dp),
    accentBorder: Color? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val finalBgColor = containerColor ?: backgroundColor
    val clickableModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    val borderModifier = if (accentBorder != null) Modifier.border(1.dp, accentBorder, shape) else Modifier
    
    Card(
        modifier = modifier.fillMaxWidth().then(borderModifier).then(clickableModifier),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = finalBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun SectionHeader(
    title: String, 
    actionText: String? = null,
    onSeeAll: (() -> Unit)? = null,
    isCritical: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title, 
            style = MaterialTheme.typography.titleMedium, 
            fontWeight = FontWeight.Black, 
            color = if (isCritical) ColorError else Gray900,
            fontFamily = CairoFontFamily
        )
        if (onSeeAll != null) {
            TextButton(
                onClick = onSeeAll,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    actionText ?: "عرض الكل", 
                    color = BluePrimary, 
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    fontFamily = CairoFontFamily
                )
            }
        }
    }
}

@Composable
fun StatCard(
    label: String = "",
    value: String = "",
    number: String? = null,
    icon: ImageVector = Icons.Default.Star,
    color: Color = BluePrimary,
    isCritical: Boolean = false,
    modifier: Modifier = Modifier,
    gradient: Brush? = null,
    onClick: (() -> Unit)? = null
) {
    val displayValue = number ?: value
    val finalColor = if (isCritical) ColorError else color
    
    RawdatyCard(
        modifier = modifier, 
        containerColor = finalColor.copy(alpha = 0.05f),
        onClick = onClick,
        elevation = 0.dp
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                Modifier.size(44.dp).clip(CircleShape).background(if (gradient != null) gradient else SolidColor(finalColor.copy(alpha = 0.15f))), 
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = if (gradient != null) White else finalColor, modifier = Modifier.size(24.dp))
            }
            Column {
                Text(
                    displayValue, 
                    style = MaterialTheme.typography.headlineSmall, 
                    fontWeight = FontWeight.Black, 
                    color = Gray900,
                    fontFamily = CairoFontFamily
                )
                Text(
                    label, 
                    style = MaterialTheme.typography.labelSmall, 
                    color = Gray500,
                    fontWeight = FontWeight.Medium,
                    fontFamily = CairoFontFamily
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════
//  FORM COMPONENTS
// ══════════════════════════════════════════════════════════════════════

@Composable
fun RawdatyField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector = Icons.Default.Edit,
    enabled: Boolean = true,
    placeholder: String? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    isPassword: Boolean = false,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier,
    backgroundColor: Color = White
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, fontFamily = CairoFontFamily, fontWeight = FontWeight.Medium) },
            placeholder = placeholder?.let { { Text(it, fontFamily = CairoFontFamily, color = Gray400) } },
            leadingIcon = { Icon(leadingIcon, null, tint = if(isError) ColorError else BluePrimary, modifier = Modifier.size(22.dp)) },
            enabled = enabled,
            isError = isError,
            singleLine = singleLine,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            textStyle = LocalTextStyle.current.copy(fontFamily = CairoFontFamily),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor,
                disabledContainerColor = Gray50,
                focusedBorderColor = BluePrimary,
                unfocusedBorderColor = Gray200,
                errorBorderColor = ColorError
            )
        )
        if (isError && errorMessage != null) {
            Text(
                errorMessage,
                color = ColorError,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = CairoFontFamily,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun SettingsRow(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    subtitle: String? = null,
    iconColor: Color = BluePrimary,
    showArrow: Boolean = true,
    content: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp, 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(iconColor.copy(0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                color = Gray900,
                fontWeight = FontWeight.SemiBold,
                fontFamily = CairoFontFamily
            )
            if (subtitle != null) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray500,
                    fontFamily = CairoFontFamily
                )
            }
        }
        if (content != null) {
            content()
            Spacer(Modifier.width(8.dp))
        }
        if (showArrow) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = Gray300, modifier = Modifier.size(20.dp))
        }
    }
}

// ══════════════════════════════════════════════════════════════════════
//  UI ELEMENTS (Button, Logo, Tag, etc)
// ══════════════════════════════════════════════════════════════════════

@Composable
fun RawdatyLogo(
    modifier: Modifier = Modifier, 
    color: Color = BluePrimary,
    isWhite: Boolean = false
) {
    Image(
        painter = painterResource(Res.drawable.rawdatylogo),
        contentDescription = "رَوْضَتِي",
        modifier = modifier
    )
}

@Composable
fun RoleTag(role: String, useSmallText: Boolean = false) {
    Surface(
        color = BluePrimary.copy(0.1f),
        shape = RoundedCornerShape(100)
    ) {
        Text(
            role,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = if (useSmallText) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium,
            color = BluePrimary,
            fontWeight = FontWeight.Bold,
            fontFamily = CairoFontFamily
        )
    }
}

@Composable
fun RawdatyAvatar(name: String, size: Dp, gradient: Brush = RawdatyGradients.HeroBlue) {
    Box(
        modifier = Modifier.size(size).clip(CircleShape).background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Text(
            name.take(1).uppercase(),
            color = White,
            style = if(size < 50.dp) MaterialTheme.typography.titleMedium else MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            fontFamily = CairoFontFamily
        )
    }
}

@Composable
fun RawdatyButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = BluePrimary,
    textColor: Color = White,
    useSmallText: Boolean = false,
    isPill: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(if (useSmallText) 40.dp else 56.dp),
        shape = if (isPill) RoundedCornerShape(100.dp) else RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor, contentColor = textColor),
        enabled = !isLoading && enabled,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = textColor, strokeWidth = 2.dp)
        } else {
            if (icon != null) {
                Icon(icon, null, modifier = Modifier.size(if (useSmallText) 18.dp else 24.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text, 
                fontWeight = FontWeight.Bold, 
                style = if (useSmallText) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodyLarge,
                fontFamily = CairoFontFamily
            )
        }
    }
}

@Composable
fun RawdatyOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = BluePrimary
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, color),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color)
    ) {
        Text(text, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge, fontFamily = CairoFontFamily)
    }
}

@Composable
fun RawdatyDivider() {
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp), color = Gray100, thickness = 1.dp)
}

@Composable
fun RawdatyFAB(icon: ImageVector, onClick: () -> Unit, containerColor: Color = BluePrimary) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = containerColor,
        contentColor = White,
        shape = CircleShape
    ) {
        Icon(icon, null)
    }
}

// ══════════════════════════════════════════════════════════════════════
//  FEEDBACK & UTILITY COMPONENTS
// ══════════════════════════════════════════════════════════════════════

@Composable
fun LoadingScreen() {
    Box(Modifier.fillMaxSize().background(AppBg), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = BluePrimary, modifier = Modifier.size(48.dp))
    }
}

@Composable
fun EmptyState(
    title: String, 
    subtitle: String = "", 
    icon: ImageVector = Icons.Default.Inbox,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, null, modifier = Modifier.size(72.dp), tint = Gray200)
        Spacer(Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Gray700, fontFamily = CairoFontFamily, textAlign = TextAlign.Center)
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Gray500, textAlign = TextAlign.Center, fontFamily = CairoFontFamily)
        
        if (actionText != null && onAction != null) {
            Spacer(Modifier.height(24.dp))
            RawdatyButton(
                text = actionText,
                onClick = onAction,
                backgroundColor = BluePrimary.copy(0.1f),
                textColor = BluePrimary,
                modifier = Modifier.wrapContentWidth()
            )
        }
    }
}

@Composable
fun DeleteConfirmDialog(
    title: String = "تأكيد الحذف",
    message: String = "هل أنت متأكد من رغبتك في حذف هذا العنصر؟ لا يمكن التراجع عن هذا الإجراء.",
    onConfirm: () -> Unit, 
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold) },
        text = { Text(message, fontFamily = CairoFontFamily) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("حذف", color = ColorError, fontWeight = FontWeight.Bold, fontFamily = CairoFontFamily)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = Gray500, fontFamily = CairoFontFamily)
            }
        },
        containerColor = White,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun OfflineIndicator() {
    Surface(
        color = ColorError.copy(0.1f),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CloudOff, null, tint = ColorError, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Text("أنت تعمل حالياً في وضع عدم الاتصال", style = MaterialTheme.typography.labelSmall, color = ColorError, fontFamily = CairoFontFamily)
        }
    }
}

@Composable
fun StarCircle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(40.dp).clip(CircleShape).background(AmberPrimary.copy(0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.Star, null, tint = AmberPrimary, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun NotificationItem(
    title: String, 
    body: String, 
    time: String, 
    isRead: Boolean = true,
    isUnread: Boolean? = null,
    icon: ImageVector = Icons.Default.Notifications,
    onClick: (() -> Unit)? = null
) {
    val finalIsRead = if (isUnread != null) !isUnread else isRead
    RawdatyCard(
        backgroundColor = if (finalIsRead) White else BluePrimary.copy(0.05f),
        onClick = onClick,
        elevation = if (finalIsRead) 1.dp else 0.dp,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, 
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                Modifier.size(48.dp).clip(CircleShape).background(if (finalIsRead) Gray100 else BluePrimary.copy(0.1f)), 
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = if (finalIsRead) Gray400 else BluePrimary, modifier = Modifier.size(24.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(
                    title, 
                    style = MaterialTheme.typography.titleSmall, 
                    fontWeight = FontWeight.Bold, 
                    color = if (finalIsRead) Gray800 else BluePrimary,
                    fontFamily = CairoFontFamily
                )
                Text(
                    body, 
                    style = MaterialTheme.typography.bodySmall, 
                    color = Gray600, 
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = CairoFontFamily
                )
                Text(
                    time, 
                    style = MaterialTheme.typography.labelSmall, 
                    color = Gray400,
                    fontFamily = CairoFontFamily
                )
            }
            if (!finalIsRead) Box(Modifier.size(8.dp).clip(CircleShape).background(BluePrimary))
        }
    }
}

@Composable
fun ShimmerBox(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition()
    val alpha by transition.animateFloat(
        initialValue = 0.3f, targetValue = 0.6f,
        animationSpec = infiniteRepeatable(animation = tween(1000), repeatMode = RepeatMode.Reverse)
    )
    Box(modifier = modifier.background(Gray200.copy(alpha = alpha)))
}

@Composable
fun AnimateEntrance(delay: Int = 0, content: @Composable () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (delay > 0) kotlinx.coroutines.delay(delay.toLong())
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(500)) + slideInVertically(initialOffsetY = { 40 }, animationSpec = tween(500)),
        content = { content() }
    )
}
