package org.mohanned.rawdatyci_cdapp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.core.context.startKoin
import org.mohanned.rawdatyci_cdapp.di.*
import org.mohanned.rawdatyci_cdapp.domain.repository.SyncManager

fun main() = application {
    val koinApp = startKoin {
        modules(
            commonModule(),
            repositoryModule,
            viewModelModule,
            platformModule()
        )
    }

    val syncManager: SyncManager = koinApp.koin.get()
    syncManager.start()

    Window(
        onCloseRequest = ::exitApplication,
        title = "Rawdaty — روضتي",
    ) {
        App()
    }
}