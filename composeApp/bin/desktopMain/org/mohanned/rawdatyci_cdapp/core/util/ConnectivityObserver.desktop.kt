package org.mohanned.rawdatyci_cdapp.core.util

import kotlinx.coroutines.flow.Flow

actual class ConnectivityObserver {
    actual fun observe(): Flow<Status> {
        TODO("Not yet implemented")
    }

    actual enum class Status { Available, Unavailable, Losing, Lost }
}