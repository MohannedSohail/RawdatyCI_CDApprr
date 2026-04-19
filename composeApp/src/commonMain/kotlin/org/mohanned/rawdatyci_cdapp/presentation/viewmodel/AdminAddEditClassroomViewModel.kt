package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.User
import org.mohanned.rawdatyci_cdapp.domain.usecase.classroom.*
import org.mohanned.rawdatyci_cdapp.domain.usecase.user.GetUsersUseCase

data class AddEditClassroomState(
    val id: String? = null,
    val name: String = "",
    val teacherId: String? = null,
    val teacherName: String = "",
    val capacity: String = "",
    val teachers: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
) {
    val isEditMode: Boolean get() = id != null
}

sealed class AddEditClassroomEffect {
    data class ShowMessage(val message: String) : AddEditClassroomEffect()
    object SaveSuccess : AddEditClassroomEffect()
}

sealed class AddEditClassroomIntent {
    data class Load(val id: String?) : AddEditClassroomIntent()
    data class NameChanged(val name: String) : AddEditClassroomIntent()
    data class TeacherChanged(val id: String?, val name: String) : AddEditClassroomIntent()
    data class CapacityChanged(val capacity: String) : AddEditClassroomIntent()
    object Save : AddEditClassroomIntent()
}

class AdminAddEditClassroomViewModel(
    private val getClassesUseCase: GetClassesUseCase,
    private val getClassByIdUseCase: GetClassByIdUseCase,
    private val createClassUseCase: CreateClassUseCase,
    private val updateClassUseCase: UpdateClassUseCase,
    private val getTeachersUseCase: GetUsersUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(AddEditClassroomState())
    val state = _state.asStateFlow()

    private val _effect = Channel<AddEditClassroomEffect>()
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: AddEditClassroomIntent) {
        when (intent) {
            is AddEditClassroomIntent.Load -> load(intent.id)
            is AddEditClassroomIntent.NameChanged -> _state.update { it.copy(name = intent.name, error = null) }
            is AddEditClassroomIntent.TeacherChanged -> _state.update { it.copy(teacherId = intent.id, teacherName = intent.name) }
            is AddEditClassroomIntent.CapacityChanged -> _state.update { it.copy(capacity = intent.capacity) }
            AddEditClassroomIntent.Save -> save()
        }
    }

    private fun load(id: String?) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true, id = id) }

        getTeachersUseCase(role = "teacher").collect { uiState ->
            if (uiState is UiState.Success) {
                _state.update { it.copy(teachers = uiState.data.items) }
            }
        }

        if (id != null) {
            getClassByIdUseCase(id).collect { uiState ->
                when (uiState) {
                    is UiState.Success -> {
                        _state.update {
                            it.copy(
                                name = uiState.data.name,
                                teacherId = uiState.data.teacherId,
                                teacherName = uiState.data.teacherName ?: "",
                                capacity = uiState.data.capacity?.toString() ?: "",
                                isLoading = false
                            )
                        }
                    }
                    is UiState.Error -> _state.update { it.copy(isLoading = false, error = uiState.message) }
                    UiState.Loading -> { }
                }
            }
        } else {
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun save() = viewModelScope.launch {
        val s = _state.value
        if (s.name.isBlank()) {
            _state.update { it.copy(error = "اسم الفصل مطلوب") }
            return@launch
        }

        _state.update { it.copy(isSaving = true) }
        val result = if (s.isEditMode) {
            updateClassUseCase(s.id!!, s.name, s.teacherId, isActive = true)
        } else {
            createClassUseCase(s.name, "فصل دراسي", s.teacherId, s.capacity.toIntOrNull())
        }

        if (result.isSuccess) {
            _effect.send(AddEditClassroomEffect.ShowMessage("تم الحفظ بنجاح"))
            _effect.send(AddEditClassroomEffect.SaveSuccess)
        } else {
            _state.update { it.copy(isSaving = false, error = result.exceptionOrNull()?.message ?: "فشل الحفظ") }
        }
    }
}
