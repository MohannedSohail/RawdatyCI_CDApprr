package org.mohanned.rawdatyci_cdapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import java.io.File

lateinit var appContext: Context

actual fun createDataStore(): DataStore<Preferences> =
    PreferenceDataStoreFactory.create(
        produceFile = {
            File(appContext.filesDir, DATA_STORE_FILE_NAME)
        }
    )