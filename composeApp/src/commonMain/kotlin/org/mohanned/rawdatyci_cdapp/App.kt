package org.mohanned.rawdatyci_cdapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import org.mohanned.rawdatyci_cdapp.presentation.navigation.AppNavigation
import org.mohanned.rawdatyci_cdapp.presentation.theme.RawdatyTheme

@Composable
@Preview
fun App() {
    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl
    ) {
        RawdatyTheme {
            AppNavigation()
        }
    }
}
