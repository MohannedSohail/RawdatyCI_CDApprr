package org.mohanned.rawdatyci_cdapp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.mohanned.rawdatyci_cdapp.di.coreModule
import org.mohanned.rawdatyci_cdapp.di.platformModule
import org.mohanned.rawdatyci_cdapp.di.repositoryModule
import org.mohanned.rawdatyci_cdapp.di.viewModelModule
import androidx.compose.ui.res.painterResource
import org.jetbrains.compose.resources.painterResource
import rawdatyci_cdapp.composeapp.generated.resources.Res
import rawdatyci_cdapp.composeapp.generated.resources.rawdatylogo

fun main() {
    if (GlobalContext.getOrNull() == null) {
        startKoin {
            modules(
                coreModule(),
                repositoryModule,
                viewModelModule,
                platformModule()
            )
        }
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "روضتي - Rawdaty",
            icon = painterResource(Res.drawable.rawdatylogo)
        ) {
            App()
        }
    }
}
