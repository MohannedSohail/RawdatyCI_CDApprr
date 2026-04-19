package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.usecase.user.CreateUserUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.user.GetUserByIdUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.user.UpdateUserUseCase

data class AdminAddEditUserState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val role: String = "teacher",
    val classId: String? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val userId: String? = null
)

sealed class AdminAddEditUserIntent {
    data class LoadUser(val id: String) : AdminAddEditUserIntent()
    data class NameChanged(val v: String) : AdminAddEditUserIntent()
    data class EmailChanged(val v: String) : AdminAddEditUserIntent()
    data class PhoneChanged(val v: String) : AdminAddEditUserIntent()
    data class PasswordChanged(val v: String) : AdminAddEditUserIntent()
    data class RoleChanged(val v: String) : AdminAddEditUserIntent()
    data class ClassChanged(val v: String?) : AdminAddEditUserIntent()
    object Save : AdminAddEditUserIntent()
}

sealed class AdminAddEditUserEffect {
    data class ShowMessage(val message: String) : AdminAddEditUserEffect()
    object NavigateBack : AdminAddEditUserEffect()
}

class AdminAddEditUserViewModel(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AdminAddEditUserState())
    val state = _state.asStateFlow()

    private val _effect = Channel<AdminAddEditUserEffect>()
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: AdminAddEditUserIntent) {
        when (intent) {
            is AdminAddEditUserIntent.LoadUser -> loadUser(intent.id)
            is AdminAddEditUserIntent.NameChanged -> _state.update { it.copy(name = intent.v) }
            is AdminAddEditUserIntent.EmailChanged -> _state.update { it.copy(email = intent.v) }
            is AdminAddEditUserIntent.PhoneChanged -> _state.update { it.copy(phone = intent.v) }
            is AdminAddEditUserIntent.PasswordChanged -> _state.update { it.copy(password = intent.v) }
            is AdminAddEditUserIntent.RoleChanged -> _state.update { it.copy(role = intent.v) }
            is AdminAddEditUserIntent.ClassChanged -> _state.update { it.copy(classId = intent.v) }
            AdminAddEditUserIntent.Save -> saveUser()
        }
    }

    private fun loadUser(id: String) {
        viewModelScope.launch {
            getUserByIdUseCase(id).collect { uiState ->
                if (uiState is UiState.Success) {
                    val user = uiState.data
                    _state.update { it.copy(
                        userId = user.id,
                        name = user.name,
                        email = user.email,
                        phone = user.phone ?: "",
                        role = user.role.name.lowercase(),
                        classId = user.classId
                    ) }
                }
            }
        }
    }

    private fun saveUser() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val s = _state.value
            val result = if (s.userId != null) {
                updateUserUseCase(s.userId, s.name, s.phone, true)
            } else {
                createUserUseCase(s.name, s.email, s.password, s.role, s.phone, s.classId)
            }

            if (result.isSuccess) {
                _effect.send(AdminAddEditUserEffect.ShowMessage("تم الحفظ بنجاح"))
                _effect.send(AdminAddEditUserEffect.NavigateBack)
            } else {
                _effect.send(AdminAddEditUserEffect.ShowMessage(result.exceptionOrNull()?.message ?: "فشل الحفظ"))
            }
            _state.update { it.copy(isSaving = false) }
        }
    }
}
