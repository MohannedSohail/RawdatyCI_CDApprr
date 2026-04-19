package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.User
import org.mohanned.rawdatyci_cdapp.domain.usecase.auth.LogoutUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.profile.ChangePasswordUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.profile.GetProfileUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.profile.UpdateProfileUseCase

data class ProfileState(
    val user: User? = null,
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)

sealed class ProfileEffect {
    object NavigateToLogin : ProfileEffect()
    data class ShowMessage(val message: String) : ProfileEffect()
}

sealed class ProfileIntent {
    object Load : ProfileIntent()
    data class NameChanged(val v: String) : ProfileIntent()
    data class PhoneChanged(val v: String) : ProfileIntent()
    object Save : ProfileIntent()
    data class ChangePassword(val current: String, val new: String, val confirm: String) : ProfileIntent()
    object Logout : ProfileIntent()
}

class ProfileViewModel(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ProfileEffect>()
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.Load -> loadProfile()
            is ProfileIntent.NameChanged -> _state.update { it.copy(name = intent.v) }
            is ProfileIntent.PhoneChanged -> _state.update { it.copy(phone = intent.v) }
            ProfileIntent.Save -> updateProfile()
            is ProfileIntent.ChangePassword -> changePassword(intent.current, intent.new, intent.confirm)
            ProfileIntent.Logout -> logout()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            getProfileUseCase().collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> _state.update { it.copy(isLoading = true) }
                    is UiState.Success -> _state.update { 
                        it.copy(user = uiState.data, name = uiState.data.name, phone = uiState.data.phone ?: "", isLoading = false) 
                    }
                    is UiState.Error -> _state.update { it.copy(error = uiState.message, isLoading = false) }
                }
            }
        }
    }

    private fun updateProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val result = updateProfileUseCase(_state.value.name, _state.value.phone, _state.value.address)
            if (result.isSuccess) {
                _effect.send(ProfileEffect.ShowMessage("تم تحديث الملف الشخصي بنجاح"))
                loadProfile()
            } else {
                _effect.send(ProfileEffect.ShowMessage(result.exceptionOrNull()?.message ?: "فشل التحديث"))
            }
            _state.update { it.copy(isSaving = false) }
        }
    }

    private fun changePassword(current: String, new: String, confirm: String) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val result = changePasswordUseCase(current, new, confirm)
            if (result.isSuccess) {
                _effect.send(ProfileEffect.ShowMessage("تم تغيير كلمة المرور بنجاح"))
            } else {
                _effect.send(ProfileEffect.ShowMessage(result.exceptionOrNull()?.message ?: "فشل تغيير كلمة المرور"))
            }
            _state.update { it.copy(isSaving = false) }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _effect.send(ProfileEffect.NavigateToLogin)
        }
    }
}
