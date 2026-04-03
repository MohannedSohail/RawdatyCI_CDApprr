package org.mohanned.rawdatyci_cdapp.di

import org.koin.core.context.startKoin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.mohanned.rawdatyci_cdapp.domain.repository.SyncManager

fun initKoin() {
    val koinApp = startKoin {
        modules(
            commonModule(),
            repositoryModule,
            viewModelModule,
            platformModule()
        )
    }
    
    // Start SyncManager
    val syncManager: SyncManager = koinApp.koin.get()
    syncManager.start()
}
