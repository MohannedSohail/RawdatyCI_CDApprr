package org.mohanned.rawdatyci_cdapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath

expect fun createDataStore(): DataStore<Preferences>

internal const val DATA_STORE_FILE_NAME = "rawdaty_prefs.preferences_pb"
