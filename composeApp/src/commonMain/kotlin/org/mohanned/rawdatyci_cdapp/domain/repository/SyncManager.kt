package org.mohanned.rawdatyci_cdapp.domain.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.mohanned.rawdatyci_cdapp.core.util.ConnectivityObserver

class SyncManager(
    private val syncRepository: SyncRepository,
    private val connectivityObserver: ConnectivityObserver
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun start() {
        connectivityObserver.observe()
            .onEach { status ->
                if (status == ConnectivityObserver.Status.Available) {
                    syncRepository.syncPendingActions()
                }
            }
            .launchIn(scope)
    }
}
