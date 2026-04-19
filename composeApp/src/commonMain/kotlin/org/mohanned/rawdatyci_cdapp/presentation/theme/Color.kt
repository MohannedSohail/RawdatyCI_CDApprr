package org.mohanned.rawdatyci_cdapp.presentation.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ══════════════════════════════════════════════════════════════════════
//  RAW DATY DESIGN SYSTEM v2.0
//  Simple/Flat Aesthetic — Unified Palette
// ══════════════════════════════════════════════════════════════════════

// 1. NEUTRAL PALETTE
val White = Color(0xFFFFFFFF)
val Black = Color(0xFF000000)
val Transparent = Color(0x00000000)

val Gray50 = Color(0xFFF8FAFC)
val Gray100 = Color(0xFFF1F5F9)
val Gray200 = Color(0xFFE2E8F0)
val Gray300 = Color(0xFFCBD5E1)
val Gray400 = Color(0xFF94A3B8)
val Gray500 = Color(0xFF64748B)
val Gray600 = Color(0xFF475569)
val Gray700 = Color(0xFF334155)
val Gray800 = Color(0xFF1E293B)
val Gray900 = Color(0xFF0F172A)

// 2. UNIFIED PRIMARY PALETTE
// Arabic Blue (#1E4C6F)
val BluePrimary = Color(0xFF1E4C6F)
val BlueLight = Color(0xFFE8F0F5)
val BlueDark = Color(0xFF15354D)

// Mint Green (#2E7A4D)
val MintPrimary = Color(0xFF2E7A4D)
val MintLight = Color(0xFFEAF2ED)
val MintDark = Color(0xFF205535)

// Arabic Amber (#F59E0B)
val AmberPrimary = Color(0xFFF59E0B)
val AmberLight = Color(0xFFFFFBEB)
val AmberDark = Color(0xFFD97706)

// 3. SEMANTIC PALETTE
val AppBg = Color(0xFFF8FAFC)
val SurfaceLight = Color(0xFFFFFFFF)

val ColorError = Color(0xFFE11D48)
val ColorSuccess = Color(0xFF16A34A)
val ColorInfo = BluePrimary
val ColorWarning = AmberPrimary

// 4. MODERN GRADIENTS
object RawdatyGradients {
    // Splash Screen (3-way)
    val Splash = Brush.verticalGradient(
        listOf(Color(0xFF0F2A3E), Color(0xFF1E4C6F), Color(0xFF256640))
    )
    val onBoarding = Brush.verticalGradient(
        listOf(
            BlueDark.copy(alpha = 0.9f),
            BlueDark.copy(alpha = 0.8f),
            BluePrimary.copy(alpha = 0.7f),
            MintPrimary.copy(alpha = 0.5f)
        )
    )

    // Auth & Dashboards
    val AdminHeader = Brush.verticalGradient(listOf(Color(0xFF163A55), BluePrimary))
    val ParentHeader = Brush.verticalGradient(listOf(Color(0xFF1D5233), MintPrimary))
    val LoginButton = Brush.horizontalGradient(listOf(Color(0xFF1E4C6F), Color(0xFF255D87)))

    val Primary = Brush.verticalGradient(listOf(BluePrimary, BlueDark))
    val Success = Brush.verticalGradient(listOf(Color(0xFF22C55E), Color(0xFF16A34A)))
    val Warning = Brush.verticalGradient(listOf(Color(0xFFFBBF24), Color(0xFFD97706)))
    val Error = Brush.verticalGradient(listOf(Color(0xFFF43F5E), Color(0xFFBE123C)))
    val ErrorColor = Brush.verticalGradient(listOf(Color(0xFFF43F5E), Color(0xFFBE123C)))

    // Header & Hero (Semantic)
    val Header = Brush.horizontalGradient(listOf(BluePrimary, BlueDark))
    val HeroBlue = Brush.verticalGradient(listOf(BluePrimary, BlueDark))
    val HeroMint = Brush.verticalGradient(listOf(MintPrimary, MintDark))

    // Avatar (Flat-friendly variants)
    val AvatarBlue = Brush.linearGradient(listOf(BluePrimary, Color(0xFF3B82F6)))
    val AvatarMint = Brush.linearGradient(listOf(MintPrimary, Color(0xFF10B981)))
    val AvatarAmber = Brush.linearGradient(listOf(AmberPrimary, Color(0xFFFBBF24)))

    // Stat Surfaces (Fading to white)
    val StatBlue = Brush.verticalGradient(listOf(BlueLight, White))
    val StatMint = Brush.verticalGradient(listOf(MintLight, White))
    val StatAmber = Brush.verticalGradient(listOf(AmberLight, White))
    val StatError = Brush.verticalGradient(listOf(ColorError.copy(alpha = 0.05f), White))
}
