package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import org.mohanned.rawdatyci_cdapp.core.base.BaseViewModel
import org.mohanned.rawdatyci_cdapp.core.base.UiEffect
import org.mohanned.rawdatyci_cdapp.core.base.UiIntent
import org.mohanned.rawdatyci_cdapp.core.base.UiState
import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiResponse
import org.mohanned.rawdatyci_cdapp.data.local.AppPreferences
import org.mohanned.rawdatyci_cdapp.domain.model.UserRole
import org.mohanned.rawdatyci_cdapp.domain.repository.AuthRepository


// ── State ─────────────────────────────────────────────
data class AuthState(
    val identifier: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val identifierError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = null,
) : UiState

// ── Intent ────────────────────────────────────────────
sealed class AuthIntent : UiIntent {
    data class IdentifierChanged(val identifier: String) : AuthIntent()
    data class PasswordChanged(val password: String)     : AuthIntent()
    object Submit                                        : AuthIntent()
    object ClearErrors                                   : AuthIntent()
}

// ── Effect ────────────────────────────────────────────
sealed class AuthEffect : UiEffect {
    data class NavigateToDashboard(val role: UserRole) : AuthEffect()
    data class ShowError(val message: String)          : AuthEffect()
}

// ── ViewModel ─────────────────────────────────────────
class AuthViewModel(
    private val authRepository: AuthRepository,
    private val prefs: AppPreferences,
) : BaseViewModel<AuthState, AuthIntent, AuthEffect>(AuthState()) {

    override suspend fun handleIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.IdentifierChanged -> {
                updateState {
                    copy(identifier = intent.identifier, identifierError = null)
                }
            }
            is AuthIntent.PasswordChanged -> {
                updateState {
                    copy(password = intent.password, passwordError = null)
                }
            }
            is AuthIntent.ClearErrors -> {
                updateState {
                    copy(
                        identifierError = null,
                        passwordError   = null,
                        generalError    = null,
                    )
                }
            }
            is AuthIntent.Submit -> submit()
        }
    }

    private suspend fun submit() {
        val current = state.value

        // Validation
        var hasError = false

        if (current.identifier.isBlank()) {
            updateState { copy(identifierError = "أدخل اسم المستخدم أو البريد الإلكتروني") }
            hasError = true
        }

        if (current.password.length < 6) {
            updateState { copy(passwordError = "كلمة المرور 6 أحرف على الأقل") }
            hasError = true
        }

        if (hasError) return

        // Call API
        updateState { copy(isLoading = true, generalError = null) }

        when (val result = authRepository.login(
            identifier = current.identifier.trim(),
            password   = current.password,
        )) {
            is ApiResponse.Success -> {
                updateState { copy(isLoading = false) }
                emitEffect(
                    AuthEffect.NavigateToDashboard(result.data.first.role)
                )
            }
            is ApiResponse.Error -> {
                updateState {
                    copy(isLoading = false, generalError = result.message)
                }
                emitEffect(AuthEffect.ShowError(result.message))
            }
            is ApiResponse.NetworkError -> {
                updateState {
                    copy(isLoading = false, generalError = result.message)
                }
                emitEffect(AuthEffect.ShowError(result.message))
            }
        }
    }
}