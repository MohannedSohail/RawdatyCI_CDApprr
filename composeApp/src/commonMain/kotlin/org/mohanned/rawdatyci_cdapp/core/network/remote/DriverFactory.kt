package org.mohanned.rawdatyci_cdapp.core.network.remote

import app.cash.sqldelight.db.SqlDriver

expect class DriverFactory {
    fun createDriver(): SqlDriver
}
