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
import org.mohanned.rawdatyci_cdapp.domain.usecase.auth.VerifyOtpUseCase

data class OtpState(
    val otp: String = "",
    val countdown: Int = 120,
    val canResend: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val resetToken: String? = null
)

sealed class OtpEffect {
    object NavigateToReset : OtpEffect()
    data class ShowError(val msg: String) : OtpEffect()
}

sealed class OtpIntent {
    data class OtpChanged(val v: String) : OtpIntent()
    object Submit : OtpIntent()
    object Resend : OtpIntent()
    object Tick : OtpIntent()
}

class OtpViewModel(
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val email: String
) : ViewModel() {
    private val _state = MutableStateFlow(OtpState())
    val state = _state.asStateFlow()
    private val _effect = Channel<OtpEffect>()
    val effect = _effect.receiveAsFlow()

    fun onIntent(i: OtpIntent) = when (i) {
        is OtpIntent.OtpChanged -> _state.update { it.copy(otp = i.v, error = null) }
        OtpIntent.Tick -> _state.update { s ->
            if (s.countdown > 0) s.copy(countdown = s.countdown - 1)
            else s.copy(canResend = true)
        }

        OtpIntent.Submit -> viewModelScope.launch {
            if (_state.value.otp.length < 4) {
                _state.update { it.copy(error = "أدخل الرمز كاملاً") }; return@launch
            }
            _state.update { it.copy(isLoading = true) }
            verifyOtpUseCase(email, _state.value.otp).onSuccess { token ->
                _state.update { it.copy(resetToken = token) }
                _effect.send(OtpEffect.NavigateToReset)
            }.onFailure {
                _state.update { it.copy(error = it.error) }
            }
            _state.update { it.copy(isLoading = false) }
        }

        OtpIntent.Resend -> viewModelScope.launch {
            forgotPasswordUseCase(email)
            _state.update { it.copy(countdown = 120, canResend = false) }
        }
    }
}
