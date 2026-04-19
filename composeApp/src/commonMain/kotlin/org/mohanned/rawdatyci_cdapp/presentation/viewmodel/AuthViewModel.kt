package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.UserRole
import org.mohanned.rawdatyci_cdapp.domain.repository.AuthRepository
import org.mohanned.rawdatyci_cdapp.domain.usecase.auth.LoginUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.auth.LogoutUseCase

data class AuthState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class AuthIntent {
    data class EmailChanged(val v: String) : AuthIntent()
    data class PasswordChanged(val v: String) : AuthIntent()
    object Login : AuthIntent()
    object Logout : AuthIntent()
}

sealed class AuthEffect {
    data class NavigateToDashboard(val role: UserRole) : AuthEffect()
    object NavigateToOnboarding : AuthEffect()
    data class ShowError(val message: String) : AuthEffect()
}

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val authRepository: AuthRepository // For session check
) : ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    private val _effect = Channel<AuthEffect>()
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.EmailChanged -> _state.update { it.copy(email = intent.v, error = null) }
            is AuthIntent.PasswordChanged -> _state.update { it.copy(password = intent.v, error = null) }
            AuthIntent.Login -> login()
            AuthIntent.Logout -> logout()
        }
    }

    fun checkSession() = viewModelScope.launch {
        authRepository.getLoggedUser()?.let {
            _effect.send(AuthEffect.NavigateToDashboard(it.role))
        } ?: run {
            _effect.send(AuthEffect.NavigateToOnboarding)
        }
    }

    private fun login() = viewModelScope.launch {
        loginUseCase(_state.value.email, _state.value.password).collect { uiState ->
            when (uiState) {
                is UiState.Loading -> _state.update { it.copy(isLoading = true, error = null) }
                is UiState.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(AuthEffect.NavigateToDashboard(uiState.data.role))
                }
                is UiState.Error -> {
                    _state.update { it.copy(isLoading = false, error = uiState.message) }
                    _effect.send(AuthEffect.ShowError(uiState.message))
                }
            }
        }
    }

    private fun logout() = viewModelScope.launch {
        logoutUseCase().onSuccess {
            _effect.send(AuthEffect.NavigateToOnboarding)
        }.onFailure {
             _effect.send(AuthEffect.ShowError(it.message ?: "فشل تسجيل الخروج"))
        }
    }
}
