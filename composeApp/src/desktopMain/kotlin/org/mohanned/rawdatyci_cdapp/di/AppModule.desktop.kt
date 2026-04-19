package org.mohanned.rawdatyci_cdapp.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.dsl.module
import org.mohanned.rawdatyci_cdapp.core.util.ConnectivityObserver
import org.mohanned.rawdatyci_cdapp.data.local.createDataStore


actual fun platformModule() = module {

    single<DataStore<Preferences>> { createDataStore() }

    single { ConnectivityObserver() }
}