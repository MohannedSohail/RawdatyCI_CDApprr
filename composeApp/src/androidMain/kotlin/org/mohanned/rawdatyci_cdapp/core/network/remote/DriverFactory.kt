package org.mohanned.rawdatyci_cdapp.core.network.remote

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.rawdaty.db.RawdatyDatabase

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(RawdatyDatabase.Schema, context, "rawdaty.db")
    }
}
