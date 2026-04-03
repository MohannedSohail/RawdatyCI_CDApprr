package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import org.mohanned.rawdatyci_cdapp.core.base.BaseViewModel
import org.mohanned.rawdatyci_cdapp.core.base.UiEffect
import org.mohanned.rawdatyci_cdapp.core.base.UiIntent
import org.mohanned.rawdatyci_cdapp.core.base.UiState
import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiResponse
import org.mohanned.rawdatyci_cdapp.data.local.AppPreferences
import org.mohanned.rawdatyci_cdapp.domain.model.*
import org.mohanned.rawdatyci_cdapp.domain.repository.*

// ══════════════════════════════════════════════════════
//  ATTENDANCE
// ══════════════════════════════════════════════════════
data class AttendanceState(
    val isLoading: Boolean = true,
    val children: List<Child> = emptyList(),
    val attendance: Map<Int, AttendanceStatus> = emptyMap(),
    val classId: Int = 0,
    val date: String = "",
    val isSaving: Boolean = false,
    val isOffline: Boolean = false,
    val error: String? = null,
) : UiState

sealed class AttendanceIntent : UiIntent {
    data class Load(val classId: Int, val date: String) : AttendanceIntent()
    data class Toggle(val childId: Int)                 : AttendanceIntent()
    object SelectAll                                    : AttendanceIntent()
    object Save                                         : AttendanceIntent()
}

sealed class AttendanceEffect : UiEffect {
    data class ShowSuccess(val synced: Int) : AttendanceEffect()
    data class ShowError(val message: String) : AttendanceEffect()
}

class AttendanceViewModel(
    private val classesRepo: ClassesRepository,
    private val attendanceRepo: AttendanceRepository,
) : BaseViewModel<AttendanceState, AttendanceIntent, AttendanceEffect>(AttendanceState()) {

    override suspend fun handleIntent(intent: AttendanceIntent) {
        when (intent) {
            is AttendanceIntent.Load -> load(intent.classId, intent.date)
            is AttendanceIntent.Toggle -> {
                val current = state.value.attendance[intent.childId]
                    ?: AttendanceStatus.PRESENT
                val next = if (current == AttendanceStatus.PRESENT)
                    AttendanceStatus.ABSENT else AttendanceStatus.PRESENT
                updateState {
                    copy(attendance = attendance + (intent.childId to next))
                }
            }
            AttendanceIntent.SelectAll -> {
                updateState {
                    copy(attendance = children.associate {
                        it.id to AttendanceStatus.PRESENT
                    })
                }
            }
            AttendanceIntent.Save -> save()
        }
    }

    private suspend fun load(classId: Int, date: String) {
        updateState { copy(isLoading = true, classId = classId, date = date) }
        when (val r = classesRepo.getChildrenByClass(classId)) {
            is ApiResponse.Success -> {
                val defaultAttendance = r.data.data.associate {
                    it.id to AttendanceStatus.PRESENT
                }
                updateState {
                    copy(
                        isLoading  = false,
                        children   = r.data.data,
                        attendance = defaultAttendance,
                    )
                }
            }
            is ApiResponse.Error -> updateState {
                copy(isLoading = false, error = r.message)
            }
            is ApiResponse.NetworkError -> updateState {
                copy(isLoading = false, isOffline = true)
            }
        }
    }

    private suspend fun save() {
        updateState { copy(isSaving = true) }
        val records = state.value.attendance.map { (childId, status) ->
            Pair(childId, status.name.lowercase())
        }
        when (val r = attendanceRepo.createAttendance(
            classId = state.value.classId,
            date    = state.value.date,
            records = records,
        )) {
            is ApiResponse.Success -> {
                updateState { copy(isSaving = false) }
                emitEffect(AttendanceEffect.ShowSuccess(records.size))
            }
            is ApiResponse.Error -> {
                updateState { copy(isSaving = false) }
                emitEffect(AttendanceEffect.ShowError(r.message))
            }
            is ApiResponse.NetworkError -> {
                updateState { copy(isSaving = false, isOffline = true) }
                emitEffect(AttendanceEffect.ShowSuccess(records.size))
            }
        }
    }
}

// ══════════════════════════════════════════════════════
//  CHAT
// ══════════════════════════════════════════════════════
data class ChatState(
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val conversations: List<Conversation> = emptyList(),
    val messages: List<Message> = emptyList(),
    val currentConversationId: Int? = null,
    val messageText: String = "",
    val currentPage: Int = 1,
    val canLoadMore: Boolean = false,
    val isSending: Boolean = false,
    val error: String? = null,
) : UiState

sealed class ChatIntent : UiIntent {
    object LoadConversations                         : ChatIntent()
    object LoadMoreConversations                     : ChatIntent()
    data class OpenConversation(val id: Int)         : ChatIntent()
    data class LoadMessages(val conversationId: Int) : ChatIntent()
    object LoadMoreMessages                          : ChatIntent()
    data class MessageChanged(val text: String)      : ChatIntent()
    object SendMessage                               : ChatIntent()
}

sealed class ChatEffect : UiEffect {
    data class NavigateToChat(val id: Int)   : ChatEffect()
    data class ShowError(val msg: String)    : ChatEffect()
    object MessageSent                       : ChatEffect()
}

class ChatViewModel(
    private val repo: ChatRepository,
) : BaseViewModel<ChatState, ChatIntent, ChatEffect>(ChatState()) {

    override suspend fun handleIntent(intent: ChatIntent) {
        when (intent) {
            ChatIntent.LoadConversations -> loadConversations(reset = true)
            ChatIntent.LoadMoreConversations -> loadConversations(reset = false)
            is ChatIntent.OpenConversation -> {
                updateState { copy(currentConversationId = intent.id) }
                emitEffect(ChatEffect.NavigateToChat(intent.id))
            }
            is ChatIntent.LoadMessages -> loadMessages(intent.conversationId, reset = true)
            ChatIntent.LoadMoreMessages -> state.value.currentConversationId?.let { loadMessages(it, reset = false) }
            is ChatIntent.MessageChanged -> {
                updateState { copy(messageText = intent.text) }
            }
            ChatIntent.SendMessage -> sendMessage()
        }
    }

    private suspend fun loadConversations(reset: Boolean) {
        if (reset) updateState { copy(isLoading = true, currentPage = 1, conversations = emptyList()) }
        else {
            if (!state.value.canLoadMore || state.value.isLoadingMore) return
            updateState { copy(isLoadingMore = true) }
        }

        val page = if (reset) 1 else state.value.currentPage + 1
        when (val r = repo.getConversations(page)) {
            is ApiResponse.Success -> updateState {
                copy(
                    isLoading = false,
                    isLoadingMore = false,
                    conversations = if (reset) r.data.data else conversations + r.data.data,
                    currentPage = page,
                    canLoadMore = r.data.meta.page < r.data.meta.lastPage
                )
            }
            else -> updateState { copy(isLoading = false, isLoadingMore = false) }
        }
    }

    private suspend fun loadMessages(id: Int, reset: Boolean) {
        if (reset) updateState { copy(messages = emptyList(), currentPage = 1) }
        
        val page = if (reset) 1 else state.value.currentPage + 1
        when (val r = repo.getMessages(id, page)) {
            is ApiResponse.Success -> updateState {
                copy(
                    messages = if (reset) r.data.data else r.data.data + messages, // Messages usually appended at top if reverse
                    currentPage = page,
                    canLoadMore = r.data.meta.page < r.data.meta.lastPage
                )
            }
            else -> {}
        }
    }

    private suspend fun sendMessage() {
        val text   = state.value.messageText.trim()
        val convId = state.value.currentConversationId ?: return
        if (text.isEmpty()) return

        updateState { copy(isSending = true, messageText = "") }

        when (val r = repo.sendMessage(convId, text)) {
            is ApiResponse.Success -> {
                updateState {
                    copy(isSending = false, messages = messages + r.data)
                }
                emitEffect(ChatEffect.MessageSent)
            }
            else -> {
                updateState { copy(isSending = false, messageText = text) }
                emitEffect(ChatEffect.ShowError("فشل الإرسال، تم الحفظ محلياً"))
            }
        }
    }
}

// ══════════════════════════════════════════════════════
//  PROFILE
// ══════════════════════════════════════════════════════
data class ProfileState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val name: String = "",
    val phone: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
) : UiState

sealed class ProfileIntent : UiIntent {
    object Load                              : ProfileIntent()
    data class NameChanged(val name: String) : ProfileIntent()
    data class PhoneChanged(val ph: String)  : ProfileIntent()
    object Save                              : ProfileIntent()
    object Logout                            : ProfileIntent()
}

sealed class ProfileEffect : UiEffect {
    object NavigateToLogin                      : ProfileEffect()
    data class ShowSuccess(val msg: String)     : ProfileEffect()
    data class ShowError(val msg: String)       : ProfileEffect()
}

class ProfileViewModel(
    private val usersRepo: UsersRepository,
    private val prefs: AppPreferences,
) : BaseViewModel<ProfileState, ProfileIntent, ProfileEffect>(ProfileState()) {

    override suspend fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.Load            -> load()
            is ProfileIntent.NameChanged  -> updateState { copy(name = intent.name) }
            is ProfileIntent.PhoneChanged -> updateState { copy(phone = intent.ph) }
            ProfileIntent.Save            -> save()
            ProfileIntent.Logout          -> logout()
        }
    }

    private suspend fun load() {
        updateState { copy(isLoading = true) }
        when (val r = usersRepo.getProfile()) {
            is ApiResponse.Success -> updateState {
                copy(
                    isLoading = false,
                    user      = r.data,
                    name      = r.data.name,
                    phone     = r.data.phone ?: "",
                )
            }
            else -> updateState { copy(isLoading = false) }
        }
    }

    private suspend fun save() {
        updateState { copy(isSaving = true) }
        when (val r = usersRepo.updateProfile(
            name  = state.value.name,
            phone = state.value.phone.takeIf { it.isNotBlank() },
        )) {
            is ApiResponse.Success -> {
                updateState { copy(isSaving = false, user = r.data) }
                emitEffect(ProfileEffect.ShowSuccess("تم حفظ التغييرات"))
            }
            else -> updateState { copy(isSaving = false) }
        }
    }

    private suspend fun logout() {
        prefs.clearAll()
        emitEffect(ProfileEffect.NavigateToLogin)
    }
}

// ══════════════════════════════════════════════════════
//  NOTIFICATIONS
// ══════════════════════════════════════════════════════
data class NotificationsState(
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val notifications: List<AppNotification> = emptyList(),
    val currentPage: Int = 1,
    val canLoadMore: Boolean = false,
    val unreadCount: Int = 0,
    val error: String? = null,
) : UiState

sealed class NotificationsIntent : UiIntent {
    object Load                      : NotificationsIntent()
    object LoadMore                  : NotificationsIntent()
    data class MarkRead(val id: Int) : NotificationsIntent()
    object MarkAllRead               : NotificationsIntent()
}

sealed class NotificationsEffect : UiEffect

class NotificationsViewModel(
    private val repo: NotificationsRepository,
) : BaseViewModel<NotificationsState, NotificationsIntent, NotificationsEffect>(NotificationsState()) {
    override suspend fun handleIntent(intent: NotificationsIntent) {
        when(intent) {
            NotificationsIntent.Load -> load(reset = true)
            NotificationsIntent.LoadMore -> load(reset = false)
            is NotificationsIntent.MarkRead -> {
                repo.markRead(intent.id)
                updateState {
                    val updated = notifications.map { if(it.id == intent.id) it.copy(isRead = true) else it }
                    copy(notifications = updated, unreadCount = updated.count { !it.isRead })
                }
            }
            NotificationsIntent.MarkAllRead -> {
                repo.markAllRead()
                updateState {
                    val updated = notifications.map { it.copy(isRead = true) }
                    copy(notifications = updated, unreadCount = 0)
                }
            }
        }
    }

    private suspend fun load(reset: Boolean) {
        if (reset) updateState { copy(isLoading = true, currentPage = 1, notifications = emptyList()) }
        else {
            if (!state.value.canLoadMore || state.value.isLoadingMore) return
            updateState { copy(isLoadingMore = true) }
        }

        val page = if (reset) 1 else state.value.currentPage + 1
        when (val r = repo.getNotifications(page)) {
            is ApiResponse.Success -> updateState {
                copy(
                    isLoading = false,
                    isLoadingMore = false,
                    notifications = if (reset) r.data.data else notifications + r.data.data,
                    unreadCount = if (reset) r.data.data.count { !it.isRead } else unreadCount + r.data.data.count { !it.isRead },
                    currentPage = page,
                    canLoadMore = r.data.meta.page < r.data.meta.lastPage
                )
            }
            else -> updateState { copy(isLoading = false, isLoadingMore = false) }
        }
    }
}

// ══════════════════════════════════════════════════════
//  GAME
// ══════════════════════════════════════════════════════
data class GameState(
    val isLoading: Boolean = false,
    val questions: List<GameQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOption: String? = null,
    val isAnswered: Boolean = false,
    val isCorrect: Boolean = false,
    val score: Int = 0,
) : UiState {
    val currentQuestion: GameQuestion? get() = questions.getOrNull(currentIndex)
}

sealed class GameIntent : UiIntent {
    data class Start(val type: GameType) : GameIntent()
    data class SelectOption(val option: String) : GameIntent()
    object CheckAnswer : GameIntent()
    object NextQuestion : GameIntent()
}

sealed class GameEffect : UiEffect {
    data class ShowResult(val score: Int, val total: Int, val stars: Int, val elapsedSecs: Int) : GameEffect()
}

class GameViewModel(
    private val repo: GamesRepository,
) : BaseViewModel<GameState, GameIntent, GameEffect>(GameState()) {
    override suspend fun handleIntent(intent: GameIntent) {
        when(intent) {
            is GameIntent.Start -> {
                updateState { copy(isLoading = true) }
                when (val r = repo.getQuestions(intent.type.name.lowercase())) {
                    is ApiResponse.Success -> updateState {
                        copy(
                            isLoading = false,
                            questions = r.data,
                            currentIndex = 0,
                            score = 0,
                            selectedOption = null,
                            isAnswered = false
                        )
                    }
                    else -> updateState { copy(isLoading = false) }
                }
            }
            is GameIntent.SelectOption -> {
                updateState { copy(selectedOption = intent.option) }
            }
            GameIntent.CheckAnswer -> {
                val q = state.value.currentQuestion ?: return
                val correct = q.correctAnswer == state.value.selectedOption
                updateState { copy(isAnswered = true, isCorrect = correct, score = if(correct) score + 1 else score) }
            }
            GameIntent.NextQuestion -> {
                if(state.value.currentIndex + 1 < state.value.questions.size) {
                    updateState { copy(currentIndex = currentIndex + 1, selectedOption = null, isAnswered = false) }
                } else {
                    emitEffect(GameEffect.ShowResult(state.value.score, state.value.questions.size, 3, 60))
                }
            }
        }
    }
}