package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.User
import org.mohanned.rawdatyci_cdapp.domain.usecase.user.*

data class UsersState(
    val users: List<User> = emptyList(),
    val currentUser: User? = null,
    val totalCount: Int = 0,
    val page: Int = 1,
    val isLoading: Boolean = false,
    val isActionLoading: Boolean = false,
    val isUserSaved: Boolean = false,
    val error: String? = null,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val role: String = "teacher",
    val classId: String? = null
)

sealed class UsersEffect {
    data class ShowMessage(val message: String) : UsersEffect()
}

sealed class UsersIntent {
    data class LoadUsers(val role: String? = null, val search: String? = null, val page: Int = 1) : UsersIntent()
    data class LoadUserForEdit(val id: String) : UsersIntent()
    data class CreateUser(val name: String, val email: String, val password: String, val role: String, val phone: String? = null, val classId: String? = null) : UsersIntent()
    data class UpdateUser(val id: String, val name: String? = null, val phone: String? = null, val isActive: Boolean? = null) : UsersIntent()
    data class DeleteUser(val id: String) : UsersIntent()
    data class NameChanged(val name: String) : UsersIntent()
    data class EmailChanged(val email: String) : UsersIntent()
    data class PhoneChanged(val phone: String) : UsersIntent()
    data class PasswordChanged(val password: String) : UsersIntent()
    data class RoleChanged(val role: String) : UsersIntent()
    data class ClassChanged(val classId: String?) : UsersIntent()
    object Save : UsersIntent()
    object LoadMore : UsersIntent()
    data class Search(val q: String) : UsersIntent()
    data class TabChanged(val tab: Int) : UsersIntent()
}

class UsersViewModel(
    private val getUsersUseCase: GetUsersUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(UsersState())
    val state = _state.asStateFlow()
    
    private val _effect = Channel<UsersEffect>()
    val effect = _effect.receiveAsFlow()
    
    fun onIntent(intent: UsersIntent) {
        when (intent) {
            is UsersIntent.LoadUsers -> loadUsers(intent.role, intent.search, intent.page)
            is UsersIntent.CreateUser -> createUser(intent.name, intent.email, intent.password, intent.role, intent.phone, intent.classId)
            is UsersIntent.UpdateUser -> updateUser(intent.id, intent.name, intent.phone, intent.isActive)
            is UsersIntent.DeleteUser -> deleteUser(intent.id)
            is UsersIntent.LoadUserForEdit -> loadUserForEdit(intent.id)
            is UsersIntent.NameChanged -> _state.update { it.copy(name = intent.name) }
            is UsersIntent.EmailChanged -> _state.update { it.copy(email = intent.email) }
            is UsersIntent.PhoneChanged -> _state.update { it.copy(phone = intent.phone) }
            is UsersIntent.PasswordChanged -> _state.update { it.copy(password = intent.password) }
            is UsersIntent.RoleChanged -> _state.update { it.copy(role = intent.role) }
            is UsersIntent.ClassChanged -> _state.update { it.copy(classId = intent.classId) }
            UsersIntent.Save -> { }
            UsersIntent.LoadMore -> { loadUsers(page = _state.value.page + 1) }
            is UsersIntent.Search -> { loadUsers(search = intent.q) }
            is UsersIntent.TabChanged -> { 
                val role = when(intent.tab) {
                    1 -> "teacher"
                    2 -> "parent"
                    3 -> "admin"
                    else -> null
                }
                loadUsers(role = role)
            }
        }
    }
    
    private fun loadUsers(role: String? = null, search: String? = null, page: Int = 1) {
        viewModelScope.launch {
            getUsersUseCase(role, search, page).collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> _state.update { it.copy(isLoading = true) }
                    is UiState.Success -> _state.update { 
                        it.copy(users = uiState.data.items, totalCount = uiState.data.total, isLoading = false, page = page) 
                    }
                    is UiState.Error -> _state.update { 
                        it.copy(error = uiState.message, isLoading = false) 
                    }
                }
            }
        }
    }

    private fun loadUserForEdit(id: String) {
        viewModelScope.launch {
            getUserByIdUseCase(id).collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> _state.update { it.copy(isLoading = true) }
                    is UiState.Success -> _state.update { 
                        it.copy(
                            currentUser = uiState.data, 
                            isLoading = false, 
                            name = uiState.data.name, 
                            email = uiState.data.email, 
                            phone = uiState.data.phone ?: "", 
                            role = uiState.data.role.name
                        ) 
                    }
                    is UiState.Error -> _state.update { it.copy(error = uiState.message, isLoading = false) }
                }
            }
        }
    }
    
    private fun createUser(name: String, email: String, password: String, role: String, phone: String?, classId: String?) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true) }
            val result = createUserUseCase(name, email, password, role, phone, classId)
            if (result.isSuccess) {
                _effect.send(UsersEffect.ShowMessage("تم إنشاء المستخدم بنجاح"))
                _state.update { it.copy(isUserSaved = true) }
                loadUsers(null, null, 1)
            } else {
                _effect.send(UsersEffect.ShowMessage(result.exceptionOrNull()?.message ?: "فشل إنشاء المستخدم"))
            }
            _state.update { it.copy(isActionLoading = false) }
        }
    }
    
    private fun updateUser(id: String, name: String?, phone: String?, isActive: Boolean?) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true) }
            val result = updateUserUseCase(id, name, phone, isActive)
            if (result.isSuccess) {
                _effect.send(UsersEffect.ShowMessage("تم تحديث المستخدم بنجاح"))
                _state.update { it.copy(isUserSaved = true) }
                loadUsers(null, null, 1)
            } else {
                _effect.send(UsersEffect.ShowMessage(result.exceptionOrNull()?.message ?: "فشل تحديث المستخدم"))
            }
            _state.update { it.copy(isActionLoading = false) }
        }
    }
    
    private fun deleteUser(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true) }
            val result = deleteUserUseCase(id)
            if (result.isSuccess) {
                _effect.send(UsersEffect.ShowMessage("تم حذف المستخدم بنجاح"))
                loadUsers(null, null, 1)
            } else {
                _effect.send(UsersEffect.ShowMessage(result.exceptionOrNull()?.message ?: "فشل حذف المستخدم"))
            }
            _state.update { it.copy(isActionLoading = false) }
        }
    }
}
