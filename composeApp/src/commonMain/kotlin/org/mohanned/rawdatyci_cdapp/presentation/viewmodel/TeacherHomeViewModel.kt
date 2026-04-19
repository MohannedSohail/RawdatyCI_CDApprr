package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.Classroom
import org.mohanned.rawdatyci_cdapp.domain.repository.AuthRepository
import org.mohanned.rawdatyci_cdapp.domain.usecase.classroom.GetClassesUseCase

data class TeacherHomeState(
    val teacherName: String = "",
    val classes: List<Classroom> = emptyList(),
    val attendanceSummary: String = "0/0",
    val attendancePercent: Float = 0f,
    val unreadMessages: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class TeacherHomeIntent {
    object Load : TeacherHomeIntent()
}

class TeacherHomeViewModel(
    private val authRepository: AuthRepository,
    private val getClassesUseCase: GetClassesUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(TeacherHomeState())
    val state = _state.asStateFlow()

    fun onIntent(intent: TeacherHomeIntent) {
        when (intent) {
            TeacherHomeIntent.Load -> loadDashboard()
        }
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val user = authRepository.getLoggedUser()
            
            getClassesUseCase().collect { uiState ->
                when (uiState) {
                    is UiState.Success -> {
                        _state.update { it.copy(
                            teacherName = user?.name ?: "المعلمة",
                            classes = uiState.data.items,
                            isLoading = false
                        ) }
                    }
                    is UiState.Error -> {
                        _state.update { it.copy(isLoading = false, error = uiState.message) }
                    }
                    UiState.Loading -> { }
                }
            }
        }
    }
}
