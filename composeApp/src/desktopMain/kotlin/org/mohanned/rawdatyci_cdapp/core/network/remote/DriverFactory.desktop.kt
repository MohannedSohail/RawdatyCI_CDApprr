package org.mohanned.rawdatyci_cdapp.core.network.remote

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.rawdaty.db.RawdatyDatabase
import io.ktor.http.ContentDisposition.Companion.File
import java.io.File


actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        // إنشاء مجلد التطبيق إذا لم يكن موجوداً
        val userHome = System.getProperty("user.home")
        val appDir = File("$userHome/.rawdati")
        if (!appDir.exists()) {
            appDir.mkdirs()
        }

        val dbFile = File(appDir, "rawdaty.db")
        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")

        // إنشاء الجداول إذا لم تكن موجودة
        RawdatyDatabase.Schema.create(driver)

        return driver
    }
}