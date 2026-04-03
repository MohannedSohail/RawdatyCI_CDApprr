package org.mohanned.rawdatyci_cdapp.core.util

import kotlinx.coroutines.flow.Flow

expect class ConnectivityObserver {
    fun observe(): Flow<Status>

    enum class Status {
        Available, Unavailable, Losing, Lost
    }
}
