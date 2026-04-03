package org.mohanned.rawdatyci_cdapp

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.mohanned.rawdatyci_cdapp.data.local.appContext
import org.mohanned.rawdatyci_cdapp.di.*
import org.mohanned.rawdatyci_cdapp.domain.repository.SyncManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}

class RawdatyApplication : Application() {
    private val syncManager: SyncManager by inject()

    override fun onCreate() {
        super.onCreate()
        appContext = this

        startKoin {
            androidContext(this@RawdatyApplication)
            modules(
                commonModule(),
                repositoryModule,
                viewModelModule,
                platformModule()
            )
        }
        syncManager.start()
    }
}
