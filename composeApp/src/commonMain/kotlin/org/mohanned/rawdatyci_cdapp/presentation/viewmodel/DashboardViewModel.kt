package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.*
import org.mohanned.rawdatyci_cdapp.domain.repository.AuthRepository
import org.mohanned.rawdatyci_cdapp.domain.usecase.child.GetMyChildrenUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.classroom.GetClassesUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.complaint.GetComplaintsUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.news.GetNewsUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.user.GetUsersUseCase

data class DashboardState(
    val user: LoggedUser? = null,
    val totalTeachers: Int = 0,
    val totalParents: Int = 0,
    val totalClasses: Int = 0,
    val totalChildren: Int = 0,
    val myChildren: List<Child> = emptyList(),
    val recentNews: List<News> = emptyList(),
    val recentComplaints: List<Complaint> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class DashboardIntent {
    object Load : DashboardIntent()
    object Logout : DashboardIntent()
}

sealed class DashboardEffect {
    object NavigateToLogin : DashboardEffect()
    data class ShowMessage(val message: String) : DashboardEffect()
}

class DashboardViewModel(
    private val authRepository: AuthRepository,
    private val getUsersUseCase: GetUsersUseCase,
    private val getClassesUseCase: GetClassesUseCase,
    private val getNewsUseCase: GetNewsUseCase,
    private val getComplaintsUseCase: GetComplaintsUseCase,
    private val getMyChildrenUseCase: GetMyChildrenUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(DashboardState())
    val state = _state.asStateFlow()
    
    private val _effect = Channel<DashboardEffect>()
    val effect = _effect.receiveAsFlow()
    
    fun onIntent(intent: DashboardIntent) {
        when (intent) {
            DashboardIntent.Load -> loadDashboard()
            DashboardIntent.Logout -> logout()
        }
    }
    
    private fun loadDashboard() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val loggedUser = authRepository.getLoggedUser()
            _state.update { it.copy(user = loggedUser) }
            
            if (loggedUser == null) {
                _effect.send(DashboardEffect.NavigateToLogin)
                return@launch
            }
            
            when (loggedUser.role) {
                UserRole.ADMIN, UserRole.SUPER_ADMIN -> {
                    loadTeachersCount()
                    loadParentsCount()
                    loadClassesCount()
                    loadRecentComplaints()
                }
                UserRole.TEACHER -> {
                    loadClassesCount()
                }
                UserRole.PARENT -> {
                    loadMyChildren()
                }
            }
            
            loadRecentNews()
            // We set isLoading=false after initiating all requests. 
            // In a more complex app, we'd count active requests.
            _state.update { it.copy(isLoading = false) }
        }
    }
    
    private fun loadTeachersCount() {
        viewModelScope.launch {
            getUsersUseCase(role = "teacher").collect { uiState ->
                if (uiState is UiState.Success) {
                    _state.update { it.copy(totalTeachers = uiState.data.total) }
                }
            }
        }
    }

    private fun loadParentsCount() {
        viewModelScope.launch {
            getUsersUseCase(role = "parent").collect { uiState ->
                if (uiState is UiState.Success) {
                    _state.update { it.copy(totalParents = uiState.data.total) }
                }
            }
        }
    }

    private fun loadClassesCount() {
        viewModelScope.launch {
            getClassesUseCase().collect { uiState ->
                if (uiState is UiState.Success) {
                    _state.update { it.copy(totalClasses = uiState.data.total) }
                }
            }
        }
    }

    private fun loadRecentComplaints() {
        viewModelScope.launch {
            getComplaintsUseCase().collect { uiState ->
                if (uiState is UiState.Success) {
                    _state.update { it.copy(recentComplaints = uiState.data.items.take(5)) }
                }
            }
        }
    }

    private fun loadMyChildren() {
        viewModelScope.launch {
            getMyChildrenUseCase().collect { uiState ->
                if (uiState is UiState.Success) {
                    _state.update { it.copy(myChildren = uiState.data.items, totalChildren = uiState.data.total) }
                }
            }
        }
    }
    
    private fun loadRecentNews() {
        viewModelScope.launch {
            getNewsUseCase().collect { uiState ->
                if (uiState is UiState.Success) {
                    _state.update { it.copy(recentNews = uiState.data.items.take(5)) }
                }
            }
        }
    }
    
    private fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _effect.send(DashboardEffect.NavigateToLogin)
        }
    }
}
