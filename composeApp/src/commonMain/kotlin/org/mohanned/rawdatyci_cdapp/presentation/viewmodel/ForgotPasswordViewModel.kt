package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mohanned.rawdatyci_cdapp.domain.usecase.auth.ForgotPasswordUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.auth.ResetPasswordUseCase

data class ForgotPasswordState(
    val email: String = "",
    val emailError: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isResetSuccess: Boolean = false,
    val newPassword: String = "",
    val confirmPassword: String = "",
)

sealed class ForgotPasswordEffect {
    data class NavigateToOtp(val email: String) : ForgotPasswordEffect()
    data class ShowError(val message: String) : ForgotPasswordEffect()
    data class ShowSuccess(val message: String) : ForgotPasswordEffect()
}

sealed class ForgotPasswordIntent {
    data class EmailChanged(val v: String) : ForgotPasswordIntent()
    object Submit : ForgotPasswordIntent()
    data class NewPasswordChanged(val v: String) : ForgotPasswordIntent()
    data class ConfirmPasswordChanged(val v: String) : ForgotPasswordIntent()
    data class ResetPassword(val token: String) : ForgotPasswordIntent()
}

class ForgotPasswordViewModel(
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ForgotPasswordState())
    val state = _state.asStateFlow()
    private val _effect = Channel<ForgotPasswordEffect>()
    val effect = _effect.receiveAsFlow()

    fun onIntent(i: ForgotPasswordIntent) = when (i) {
        is ForgotPasswordIntent.EmailChanged -> _state.update { it.copy(email = i.v, emailError = null, error = null) }
        is ForgotPasswordIntent.NewPasswordChanged -> _state.update { it.copy(newPassword = i.v, error = null) }
        is ForgotPasswordIntent.ConfirmPasswordChanged -> _state.update { it.copy(confirmPassword = i.v, error = null) }

        is ForgotPasswordIntent.Submit -> viewModelScope.launch {
            if (_state.value.email.isBlank()) {
                _state.update { it.copy(emailError = "البريد مطلوب") }; return@launch
            }
            _state.update { it.copy(isLoading = true, error = null) }
            forgotPasswordUseCase(_state.value.email.trim()).onSuccess {
                _effect.send(ForgotPasswordEffect.NavigateToOtp(_state.value.email))
            }.onFailure {
                _state.update { it.copy(error = it.error) }
            }
            _state.update { it.copy(isLoading = false) }
        }

        is ForgotPasswordIntent.ResetPassword -> viewModelScope.launch {
            val s = _state.value
            if (s.newPassword.length < 6) {
                _state.update { it.copy(error = "كلمة المرور قصيرة جداً") }; return@launch
            }
            if (s.newPassword != s.confirmPassword) {
                _state.update { it.copy(error = "كلمتا المرور غير متطابقتين") }; return@launch
            }

            _state.update { it.copy(isLoading = true, error = null) }
            resetPasswordUseCase(i.token, s.newPassword, s.confirmPassword).onSuccess {
                _state.update { it.copy(isResetSuccess = true) }
                _effect.send(ForgotPasswordEffect.ShowSuccess("تم تغيير كلمة المرور بنجاح"))
            }.onFailure {
                _state.update { it.copy(error = it.error) }
            }
            _state.update { it.copy(isLoading = false) }
        }
    }
}
