package org.mohanned.rawdatyci_cdapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import org.mohanned.rawdatyci_cdapp.presentation.navigation.AppNavigation
import org.mohanned.rawdatyci_cdapp.presentation.theme.BlueDark
import org.mohanned.rawdatyci_cdapp.presentation.theme.RawdatyTheme
import org.mohanned.rawdatyci_cdapp.presentation.theme.White

val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState provided")
}

@Composable
fun App() {
    val snackbarHostState = remember { SnackbarHostState() }

    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl,
        LocalSnackbarHostState provides snackbarHostState
    ) {
        RawdatyTheme {
            Scaffold(
                snackbarHost = { 
                    SnackbarHost(hostState = snackbarHostState) { data ->
                        Snackbar(
                            snackbarData = data,
                            containerColor = BlueDark,
                            contentColor = White,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            ) { padding ->
                Box(modifier = androidx.compose.ui.Modifier.padding(padding)) {
                    AppNavigation()
                }
            }
        }
    }
}
