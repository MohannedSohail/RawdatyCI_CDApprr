package org.mohanned.rawdatyci_cdapp.core.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

actual class ConnectivityObserver {
    actual fun observe(): Flow<ConnectivityObserver.Status> {
        return flow {
            emit(ConnectivityObserver.Status.Available)
        }
    }
}
