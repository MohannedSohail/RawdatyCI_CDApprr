package org.mohanned.rawdatyci_cdapp.core.network.remote

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import org.mohanned.rawdatyci_cdapp.data.local.RawdatyDatabase

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(RawdatyDatabase.Schema, "rawdaty.db")
    }
}
