package org.mohanned.rawdatyci_cdapp.di

import org.koin.dsl.module
import org.mohanned.rawdatyci_cdapp.core.network.remote.DriverFactory
import org.mohanned.rawdatyci_cdapp.core.util.ConnectivityObserver

actual fun platformModule() = module {
    single { DriverFactory() }
    single { ConnectivityObserver() }
}
