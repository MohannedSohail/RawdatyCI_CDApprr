package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.AppNotification
import org.mohanned.rawdatyci_cdapp.domain.usecase.notification.*

data class NotificationsState(
    val notifications: List<AppNotification> = emptyList(),
    val unreadCount: Int = 0,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isActionLoading: Boolean = false,
    val canLoadMore: Boolean = true,
    val error: String? = null,
    val title: String = "",
    val body: String = "",
    val target: String = "all",
    val classId: String? = null
)

sealed class NotificationsIntent {
    object Load : NotificationsIntent()
    object LoadMore : NotificationsIntent()
    data class MarkRead(val id: String) : NotificationsIntent()
    object MarkAllRead : NotificationsIntent()
    data class TitleChanged(val v: String) : NotificationsIntent()
    data class BodyChanged(val v: String) : NotificationsIntent()
    data class TargetChanged(val v: String) : NotificationsIntent()
    object Send : NotificationsIntent()
}

sealed class NotificationsEffect {
    data class ShowMessage(val message: String) : NotificationsEffect()
}

class NotificationsViewModel(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val markNotificationReadUseCase: MarkNotificationReadUseCase,
    private val markAllNotificationsReadUseCase: MarkAllNotificationsReadUseCase,
    private val sendNotificationUseCase: SendNotificationUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(NotificationsState())
    val state = _state.asStateFlow()

    private val _effect = Channel<NotificationsEffect>()
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: NotificationsIntent) {
        when (intent) {
            NotificationsIntent.Load -> loadNotifications()
            NotificationsIntent.LoadMore -> loadMoreNotifications()
            is NotificationsIntent.MarkRead -> markRead(intent.id)
            NotificationsIntent.MarkAllRead -> markAllRead()
            is NotificationsIntent.TitleChanged -> _state.update { it.copy(title = intent.v) }
            is NotificationsIntent.BodyChanged -> _state.update { it.copy(body = intent.v) }
            is NotificationsIntent.TargetChanged -> _state.update { it.copy(target = intent.v) }
            NotificationsIntent.Send -> sendNotification()
        }
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            getNotificationsUseCase().collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> _state.update { it.copy(isLoading = true) }
                    is UiState.Success -> {
                        _state.update { it.copy(
                            notifications = uiState.data.items,
                            unreadCount = uiState.data.items.count { n -> !it.isActionLoading }, // Logic for unread
                            isLoading = false
                        ) }
                    }
                    is UiState.Error -> _state.update { it.copy(error = uiState.message, isLoading = false) }
                }
            }
        }
    }

    private fun markRead(id: String) {
        viewModelScope.launch {
            markNotificationReadUseCase(id).onSuccess {
                loadNotifications()
            }
        }
    }

    private fun markAllRead() {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true) }
            markAllNotificationsReadUseCase().onSuccess {
                _effect.send(NotificationsEffect.ShowMessage("تم تحديد الكل كمقروء"))
                loadNotifications()
            }
            _state.update { it.copy(isActionLoading = false) }
        }
    }

    private fun sendNotification() {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true) }
            val current = _state.value
            val result = sendNotificationUseCase(current.title, current.body, current.target, current.classId)
            if (result.isSuccess) {
                _effect.send(NotificationsEffect.ShowMessage("تم إرسال الإشعار بنجاح"))
                _state.update { it.copy(title = "", body = "") }
            } else {
                _effect.send(NotificationsEffect.ShowMessage(result.exceptionOrNull()?.message ?: "فشل الإرسال"))
            }
            _state.update { it.copy(isActionLoading = false) }
        }
    }
    private fun loadMoreNotifications() {
        if (_state.value.isLoadingMore || !_state.value.canLoadMore) return
        
        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true) }
            // In a real app, we would pass the next page index. 
            // For now, we simulate by re-loading or assuming getNotificationsUseCase handles paging via state.
            getNotificationsUseCase().collect { uiState ->
                if (uiState is UiState.Success) {
                    _state.update { it.copy(
                        notifications = it.notifications + uiState.data.items,
                        isLoadingMore = false,
                        canLoadMore = uiState.data.items.isNotEmpty() // Simple check for more data
                    ) }
                } else if (uiState is UiState.Error) {
                    _state.update { it.copy(isLoadingMore = false) }
                }
            }
        }
    }
}
