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
import org.mohanned.rawdatyci_cdapp.domain.usecase.classroom.GetClassesUseCase
import org.mohanned.rawdatyci_cdapp.core.network.TokenManager
import org.mohanned.rawdatyci_cdapp.data.remote.dto.CreateChildRequest
import org.mohanned.rawdatyci_cdapp.domain.model.Classroom
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

data class AdminAddEditUserState(
    val name: String = "",
    val usernameEn: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val role: String = "teacher",
    val classId: String? = null,
    val childName: String = "",
    val childBirthDate: String = "",
    val childGender: String = "male",
    val childClassId: String? = null,
    
    val classes: List<Classroom> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val debugInfo: String? = null,
    val userId: String? = null
)

sealed class AdminAddEditUserIntent {
    data class LoadUser(val id: String) : AdminAddEditUserIntent()
    object LoadClasses : AdminAddEditUserIntent()
    data class NameChanged(val v: String) : AdminAddEditUserIntent()
    data class UsernameEnChanged(val v: String) : AdminAddEditUserIntent()
    data class EmailChanged(val v: String) : AdminAddEditUserIntent()
    data class PhoneChanged(val v: String) : AdminAddEditUserIntent()
    data class PasswordChanged(val v: String) : AdminAddEditUserIntent()
    data class RoleChanged(val v: String) : AdminAddEditUserIntent()
    data class ChildNameChanged(val v: String) : AdminAddEditUserIntent()
    data class ChildBirthDateChanged(val v: String) : AdminAddEditUserIntent()
    data class ChildGenderChanged(val v: String) : AdminAddEditUserIntent()
    data class ChildClassChanged(val v: String?) : AdminAddEditUserIntent()
    object Save : AdminAddEditUserIntent()
    object ClearDebug : AdminAddEditUserIntent()
}

sealed class AdminAddEditUserEffect {
    data class ShowMessage(val message: String) : AdminAddEditUserEffect()
    object NavigateBack : AdminAddEditUserEffect()
}

class AdminAddEditUserViewModel(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val getClassesUseCase: GetClassesUseCase
) : ViewModel(), KoinComponent {

    private val tokenManager: TokenManager by inject()
    private val _state = MutableStateFlow(AdminAddEditUserState())
    val state = _state.asStateFlow()

    private val _effect = Channel<AdminAddEditUserEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        onIntent(AdminAddEditUserIntent.LoadClasses)
    }

    fun onIntent(intent: AdminAddEditUserIntent) {
        when (intent) {
            is AdminAddEditUserIntent.LoadUser -> loadUser(intent.id)
            AdminAddEditUserIntent.LoadClasses -> loadClasses()
            is AdminAddEditUserIntent.NameChanged -> _state.update { it.copy(name = intent.v) }
            is AdminAddEditUserIntent.UsernameEnChanged -> {
                val filtered = intent.v.filter { it in 'a'..'z' || it in 'A'..'Z' || it in '0'..'9' || it == '.' }
                _state.update { it.copy(usernameEn = filtered) }
                generateSmartEmail()
            }
            is AdminAddEditUserIntent.RoleChanged -> {
                _state.update { it.copy(role = intent.v) }
                generateSmartEmail()
            }
            is AdminAddEditUserIntent.PhoneChanged -> _state.update { it.copy(phone = intent.v) }
            is AdminAddEditUserIntent.PasswordChanged -> _state.update { it.copy(password = intent.v) }
            is AdminAddEditUserIntent.ChildNameChanged -> _state.update { it.copy(childName = intent.v) }
            is AdminAddEditUserIntent.ChildBirthDateChanged -> _state.update { it.copy(childBirthDate = intent.v) }
            is AdminAddEditUserIntent.ChildGenderChanged -> _state.update { it.copy(childGender = intent.v) }
            is AdminAddEditUserIntent.ChildClassChanged -> _state.update { it.copy(childClassId = intent.v) }
            AdminAddEditUserIntent.Save -> saveUser()
            AdminAddEditUserIntent.ClearDebug -> _state.update { it.copy(debugInfo = null) }
            else -> {}
        }
    }

    private fun loadClasses() {
        viewModelScope.launch {
            getClassesUseCase().collect { uiState ->
                if (uiState is UiState.Success) {
                    _state.update { it.copy(classes = uiState.data.items) }
                }
            }
        }
    }

    private fun generateSmartEmail() {
        if (_state.value.userId != null) return
        val identifier = _state.value.usernameEn.lowercase()
        if (identifier.isBlank()) return
        
        viewModelScope.launch {
            val slug = tokenManager.getTenantSlug()
            val smartEmail = "${identifier}.${_state.value.role}@${slug}.rawdaty.app"
            _state.update { it.copy(email = smartEmail) }
        }
    }

    private fun loadUser(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getUserByIdUseCase(id).collect { uiState ->
                if (uiState is UiState.Success) {
                    val user = uiState.data
                    _state.update { it.copy(
                        userId = user.id,
                        name = user.name,
                        email = user.email,
                        phone = user.phone ?: "",
                        role = user.role.name.lowercase(),
                        isLoading = false
                    ) }
                }
            }
        }
    }

    private fun saveUser() {
        viewModelScope.launch {
            if (_state.value.email.isBlank() && _state.value.userId == null) {
                _effect.send(AdminAddEditUserEffect.ShowMessage("يرجى كتابة مُعرف الإيميل بالإنجليزي"))
                return@launch
            }
            _state.update { it.copy(isSaving = true, debugInfo = null) }
            val s = _state.value
            
            val children = if (s.role == "parent" && s.childName.isNotBlank()) {
                listOf(
                    CreateChildRequest(
                        name = s.childName,
                        // إرسال قيمة افتراضية للـ parent_id لأن السيرفر يطلبها إجبارياً
                        parentId = "00000000-0000-0000-0000-000000000000", 
                        classId = s.childClassId,
                        birthDate = s.childBirthDate.ifBlank { "2020-01-01" },
                        gender = s.childGender
                    )
                )
            } else null


            val debugJson = "Role: ${s.role}\n" +
                    "Name: ${s.name}\n" +
                    "Children count: ${children?.size ?: 0}\n" +
                    "First Child Class: ${children?.firstOrNull()?.classId}"

            println("DEBUG: Preparing to save user. $debugJson")

            val result = if (s.userId != null) {
                updateUserUseCase(s.userId, s.name, s.phone, true)
            } else {
                createUserUseCase(s.name, s.email, s.password, s.role, s.phone, null, children)
            }

            if (result.isSuccess) {
                _effect.send(AdminAddEditUserEffect.ShowMessage("تم حفظ البيانات بنجاح ✅"))
                _effect.send(AdminAddEditUserEffect.NavigateBack)
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "خطأ غير معروف"
                _effect.send(AdminAddEditUserEffect.ShowMessage("فشل الحفظ: $errorMsg"))
            }
            _state.update { it.copy(isSaving = false) }
        }
    }
}
