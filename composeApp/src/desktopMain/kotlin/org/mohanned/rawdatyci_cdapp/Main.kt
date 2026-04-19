package org.mohanned.rawdatyci_cdapp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.mohanned.rawdatyci_cdapp.di.coreModule
import org.mohanned.rawdatyci_cdapp.di.platformModule
import org.mohanned.rawdatyci_cdapp.di.repositoryModule
import org.mohanned.rawdatyci_cdapp.di.viewModelModule

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
            title = "روضتي - Rawdaty"
        ) {
            App()
        }
    }
}
