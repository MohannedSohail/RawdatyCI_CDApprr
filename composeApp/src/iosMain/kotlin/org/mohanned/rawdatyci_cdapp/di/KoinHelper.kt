package org.mohanned.rawdatyci_cdapp.di

import org.koin.core.context.startKoin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

fun initKoin() {
    startKoin {
        modules(
            coreModule(),
            repositoryModule,
            viewModelModule,
            platformModule()
            // Note: platformModule() on iOS should provide local data storage or other iOS specific bits
        )
    }
}
