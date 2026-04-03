package org.mohanned.rawdatyci_cdapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import java.io.File

actual fun createDataStore(): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            val userHome = System.getProperty("user.home")
            val dataDir = File(userHome, ".rawdaty")
            if (!dataDir.exists()) {
                dataDir.mkdirs()
            }
            File(dataDir, "rawdaty.preferences_pb").absolutePath.toPath()
        }
    )
}
