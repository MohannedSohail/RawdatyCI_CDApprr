package org.mohanned.rawdatyci_cdapp.core.network.remote

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.rawdaty.data.local.db.RawdatyDatabase

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        RawdatyDatabase.Schema.create(driver)
        return driver
    }
}
