package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.domain.repository.AuthRepository
import org.mohanned.rawdatyci_cdapp.domain.usecase.child.GetMyChildrenUseCase

data class ParentHomeState(
    val parentName: String = "",
    val children: List<Child> = emptyList(),
    val unreadMessages: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class ParentHomeIntent {
    object Load : ParentHomeIntent()
}

class ParentHomeViewModel(
    private val authRepository: AuthRepository,
    private val getMyChildrenUseCase: GetMyChildrenUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ParentHomeState())
    val state = _state.asStateFlow()

    fun onIntent(intent: ParentHomeIntent) {
        when (intent) {
            ParentHomeIntent.Load -> loadDashboard()
        }
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val user = authRepository.getLoggedUser()
            
            getMyChildrenUseCase().collect { uiState ->
                when (uiState) {
                    is UiState.Success -> {
                        _state.update { it.copy(
                            parentName = user?.name ?: "",
                            children = uiState.data.items,
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
