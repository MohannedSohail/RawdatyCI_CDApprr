package org.mohanned.rawdatyci_cdapp.core.util
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

actual class ConnectivityObserver {
    private val _status = MutableStateFlow(Status.Available)
     val status: Flow<Status> = _status.asStateFlow()

    actual fun observe(): Flow<Status> {
        return _status.asStateFlow()
    }

    actual enum class Status {
        Available, Unavailable, Losing, Lost
    }
}