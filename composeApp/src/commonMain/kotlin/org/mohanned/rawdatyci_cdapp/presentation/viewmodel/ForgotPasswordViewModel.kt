package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import org.mohanned.rawdatyci_cdapp.core.base.BaseViewModel
import org.mohanned.rawdatyci_cdapp.core.base.UiEffect
import org.mohanned.rawdatyci_cdapp.core.base.UiIntent
import org.mohanned.rawdatyci_cdapp.core.base.UiState
import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.repository.AuthRepository


// ── Forgot Password ───────────────────────────────────
data class ForgotPasswordState(
    val email: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
) : UiState

sealed class ForgotPasswordIntent : UiIntent {
    data class EmailChanged(val email: String) : ForgotPasswordIntent()
    object Submit                              : ForgotPasswordIntent()
}

sealed class ForgotPasswordEffect : UiEffect {
    data class NavigateToOtp(val email: String) : ForgotPasswordEffect()
    data class ShowError(val message: String)   : ForgotPasswordEffect()
}

class ForgotPasswordViewModel(
    private val authRepository: AuthRepository,
) : BaseViewModel<ForgotPasswordState, ForgotPasswordIntent, ForgotPasswordEffect>(
    ForgotPasswordState()
) {
    override suspend fun handleIntent(intent: ForgotPasswordIntent) {
        when (intent) {
            is ForgotPasswordIntent.EmailChanged -> {
                updateState { copy(email = intent.email, emailError = null) }
            }
            is ForgotPasswordIntent.Submit -> submit()
        }
    }

    private suspend fun submit() {
        if (state.value.email.isBlank() || !state.value.email.contains("@")) {
            updateState { copy(emailError = "أدخل بريداً إلكترونياً صحيحاً") }
            return
        }

        updateState { copy(isLoading = true) }

        when (val r = authRepository.forgotPassword(state.value.email.trim())) {
            is ApiResponse.Success -> {
                updateState { copy(isLoading = false) }
                emitEffect(ForgotPasswordEffect.NavigateToOtp(state.value.email))
            }
            is ApiResponse.Error -> {
                updateState { copy(isLoading = false) }
                emitEffect(ForgotPasswordEffect.ShowError(r.message))
            }
            is ApiResponse.NetworkError -> {
                updateState { copy(isLoading = false) }
                emitEffect(ForgotPasswordEffect.ShowError(r.message))
            }
        }
    }
}

// ── OTP ───────────────────────────────────────────────
data class OtpState(
    val email: String = "",
    val otp: String = "",
    val isLoading: Boolean = false,
    val countdown: Int = 60,
    val canResend: Boolean = false,
    val error: String? = null,
) : UiState

sealed class OtpIntent : UiIntent {
    data class OtpChanged(val otp: String) : OtpIntent()
    object Submit                          : OtpIntent()
    object Resend                          : OtpIntent()
    object Tick                            : OtpIntent()
}

sealed class OtpEffect : UiEffect {
    object NavigateToReset              : OtpEffect()
    data class ShowError(val msg: String) : OtpEffect()
}

class OtpViewModel(
    private val authRepository: AuthRepository,
    private val email: String,
) : BaseViewModel<OtpState, OtpIntent, OtpEffect>(OtpState(email = email)) {

    override suspend fun handleIntent(intent: OtpIntent) {
        when (intent) {
            is OtpIntent.OtpChanged -> {
                updateState { copy(otp = intent.otp, error = null) }
            }
            is OtpIntent.Tick -> {
                if (state.value.countdown > 0) {
                    updateState { copy(countdown = countdown - 1) }
                } else {
                    updateState { copy(canResend = true) }
                }
            }
            is OtpIntent.Resend -> {
                updateState { copy(countdown = 60, canResend = false) }
                authRepository.forgotPassword(email)
            }
            is OtpIntent.Submit -> submit()
        }
    }

    private suspend fun submit() {
        if (state.value.otp.length < 4) {
            updateState { copy(error = "أدخل الرمز كاملاً") }
            return
        }

        updateState { copy(isLoading = true) }

        when (val r = authRepository.verifyOtp(email, state.value.otp)) {
            is ApiResponse.Success -> {
                updateState { copy(isLoading = false) }
                emitEffect(OtpEffect.NavigateToReset)
            }
            is ApiResponse.Error -> {
                updateState { copy(isLoading = false, error = r.message) }
            }
            is ApiResponse.NetworkError -> {
                updateState { copy(isLoading = false, error = r.message) }
            }
        }
    }
}