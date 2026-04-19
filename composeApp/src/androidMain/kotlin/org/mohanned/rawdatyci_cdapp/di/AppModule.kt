@file:JvmName("AndroidAppModuleKt")

package org.mohanned.rawdatyci_cdapp.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.dsl.module
import org.mohanned.rawdatyci_cdapp.core.util.ConnectivityObserver
import org.koin.android.ext.koin.androidContext
import org.mohanned.rawdatyci_cdapp.data.local.createDataStore
import org.mohanned.rawdatyci_cdapp.data.local.appContext

actual fun platformModule() = module {
    single { ConnectivityObserver(androidContext()) }
    single<DataStore<Preferences>> {
        appContext = androidContext()
        createDataStore()
    }
}
