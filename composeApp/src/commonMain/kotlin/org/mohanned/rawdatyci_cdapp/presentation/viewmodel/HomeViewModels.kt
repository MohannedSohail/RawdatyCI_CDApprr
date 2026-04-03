package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import org.mohanned.rawdatyci_cdapp.core.base.*
import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiResponse
import org.mohanned.rawdatyci_cdapp.data.local.AppPreferences
import org.mohanned.rawdatyci_cdapp.domain.model.*
import org.mohanned.rawdatyci_cdapp.domain.repository.*

// ══════════════════════════════════════════════════════
//  TEACHER HOME
// ══════════════════════════════════════════════════════
data class TeacherHomeState(
    val isLoading: Boolean = true,
    val teacherName: String = "",
    val classroomName: String = "",
    val attendanceSummary: String = "0 / 0",
    val attendancePercent: Int = 0,
    val taskProgress: Float = 0f,
    val error: String? = null,
) : UiState

sealed class TeacherHomeIntent : UiIntent {
    object Load : TeacherHomeIntent()
}

class TeacherHomeViewModel(
    private val prefs: AppPreferences,
    private val classesRepo: ClassesRepository,
    private val attendanceRepo: AttendanceRepository,
) : BaseViewModel<TeacherHomeState, TeacherHomeIntent, UiEffect>(TeacherHomeState()) {

    override suspend fun handleIntent(intent: TeacherHomeIntent) {
        when (intent) {
            TeacherHomeIntent.Load -> load()
        }
    }

    private suspend fun load() {
        val name = prefs.getUserName() ?: "المعلم"
        updateState { copy(isLoading = true, teacherName = name) }

        // Fetch children to get attendance count
        when (val r = classesRepo.getChildrenByClass(1)) { // Example classId
            is ApiResponse.Success -> {
                val total = r.data.data.size
                updateState {
                    copy(
                        isLoading = false,
                        attendanceSummary = "0 / $total",
                        attendancePercent = 0,
                        taskProgress = 0.5f
                    )
                }
            }
            is ApiResponse.NetworkError -> {
                // Network error doesn't have data, just message
                updateState {
                    copy(
                        isLoading = false,
                        error = r.message
                    )
                }
            }
            else -> updateState { copy(isLoading = false) }
        }
    }
}

// ══════════════════════════════════════════════════════
//  PARENT HOME
// ══════════════════════════════════════════════════════
data class ParentHomeState(
    val isLoading: Boolean = true,
    val parentName: String = "",
    val children: List<Child> = emptyList(),
    val unreadMessages: Int = 0,
    val error: String? = null,
) : UiState

sealed class ParentHomeIntent : UiIntent {
    object Load : ParentHomeIntent()
}

class ParentHomeViewModel(
    private val prefs: AppPreferences,
    private val classesRepo: ClassesRepository,
    private val chatRepo: ChatRepository,
) : BaseViewModel<ParentHomeState, ParentHomeIntent, UiEffect>(ParentHomeState()) {

    override suspend fun handleIntent(intent: ParentHomeIntent) {
        when (intent) {
            ParentHomeIntent.Load -> load()
        }
    }

    private suspend fun load() {
        val name = prefs.getUserName() ?: "ولي الأمر"
        updateState { copy(isLoading = true, parentName = name) }

        // Fetch children
        val childrenRes = classesRepo.getMyChildren()
        val chatRes = chatRepo.getConversations(1)

        updateState {
            copy(
                isLoading = false,
                parentName = name,
                children = if (childrenRes is ApiResponse.Success) childrenRes.data.data else emptyList(),
                unreadMessages = if (chatRes is ApiResponse.Success) chatRes.data.data.sumOf { it.unreadCount } else 0
            )
        }
    }
}

// ══════════════════════════════════════════════════════
//  SETTINGS
// ══════════════════════════════════════════════════════
data class SettingsState(
    val language: String = "العربية",
    val isDarkMode: Boolean = false,
    val notificationsOn: Boolean = true,
) : UiState

sealed class SettingsIntent : UiIntent {
    object Load : SettingsIntent()
    data class ToggleDarkMode(val on: Boolean) : SettingsIntent()
    data class ToggleNotifications(val on: Boolean) : SettingsIntent()
    data class ChangeLanguage(val lang: String) : SettingsIntent()
}

class SettingsViewModel(
    private val prefs: AppPreferences,
) : BaseViewModel<SettingsState, SettingsIntent, UiEffect>(SettingsState()) {

    override suspend fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            SettingsIntent.Load -> {
                updateState {
                    copy(
                        language = "العربية",
                        isDarkMode = false,
                        notificationsOn = true
                    )
                }
            }
            is SettingsIntent.ToggleDarkMode -> {
                prefs.setDarkMode(intent.on)
                updateState { copy(isDarkMode = intent.on) }
            }
            is SettingsIntent.ToggleNotifications -> {
                prefs.setNotificationsOn(intent.on)
                updateState { copy(notificationsOn = intent.on) }
            }
            is SettingsIntent.ChangeLanguage -> {
                prefs.setLanguage(intent.lang)
                updateState { copy(language = if (intent.lang == "ar") "العربية" else "English") }
            }
        }
    }
}
