package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.domain.model.Classroom
import org.mohanned.rawdatyci_cdapp.domain.model.User
import org.mohanned.rawdatyci_cdapp.domain.usecase.classroom.*
import org.mohanned.rawdatyci_cdapp.domain.usecase.user.GetUsersUseCase

data class ClassroomsState(
    val classrooms: List<Classroom> = emptyList(),
    val currentClass: Classroom? = null,
    val children: List<Child> = emptyList(),
    val teachers: List<User> = emptyList(),
    val name: String = "",
    val teacherId: String? = null,
    val capacity: String = "",
    val query: String = "",
    val page: Int = 1,
    val total: Int = 0,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isLoadingMore: Boolean = false,
    val canLoadMore: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val showDeleteDialog: Boolean = false,
    val pendingDeleteId: String? = null
)

sealed class ClassroomsEffect {
    data class ShowMessage(val message: String) : ClassroomsEffect()
}

sealed class ClassroomsIntent {
    object Load : ClassroomsIntent()
    data class Search(val query: String) : ClassroomsIntent()
    object LoadMore : ClassroomsIntent()
    data class LoadClassForEdit(val classId: String) : ClassroomsIntent()
    data class LoadClassDetail(val classId: String) : ClassroomsIntent()
    data class NameChanged(val name: String) : ClassroomsIntent()
    data class TeacherChanged(val teacherId: String?, val teacherName: String) : ClassroomsIntent()
    data class CapacityChanged(val capacity: String) : ClassroomsIntent()
    object Save : ClassroomsIntent()
    data class DeleteRequest(val id: String) : ClassroomsIntent()
    object ConfirmDelete : ClassroomsIntent()
    object DismissDelete : ClassroomsIntent()
}

class ClassroomsViewModel(
    private val getClassesUseCase: GetClassesUseCase,
    private val getClassByIdUseCase: GetClassByIdUseCase,
    private val createClassUseCase: CreateClassUseCase,
    private val updateClassUseCase: UpdateClassUseCase,
    private val deleteClassUseCase: DeleteClassUseCase,
    private val getTeachersUseCase: GetUsersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ClassroomsState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ClassroomsEffect>()
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: ClassroomsIntent) {
        when (intent) {
            is ClassroomsIntent.Load -> loadClassrooms()
            is ClassroomsIntent.Search -> searchClassrooms(intent.query)
            ClassroomsIntent.LoadMore -> loadMoreClassrooms()
            is ClassroomsIntent.LoadClassForEdit -> loadClassForEdit(intent.classId)
            is ClassroomsIntent.LoadClassDetail -> loadClassDetail(intent.classId)
            is ClassroomsIntent.NameChanged -> _state.update { it.copy(name = intent.name, error = null) }
            is ClassroomsIntent.TeacherChanged -> _state.update { it.copy(teacherId = intent.teacherId) }
            is ClassroomsIntent.CapacityChanged -> _state.update { it.copy(capacity = intent.capacity) }
            ClassroomsIntent.Save -> saveClassroom()
            is ClassroomsIntent.DeleteRequest -> _state.update { 
                it.copy(showDeleteDialog = true, pendingDeleteId = intent.id) 
            }
            ClassroomsIntent.ConfirmDelete -> deleteClassroom()
            ClassroomsIntent.DismissDelete -> _state.update { 
                it.copy(showDeleteDialog = false, pendingDeleteId = null) 
            }
        }
    }

    private fun loadClassrooms() {
        viewModelScope.launch {
            getClassesUseCase(page = 1).collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> _state.update { it.copy(isLoading = true) }
                    is UiState.Success -> {
                        _state.update { 
                            it.copy(
                                classrooms = uiState.data.items,
                                total = uiState.data.total,
                                page = 1,
                                canLoadMore = uiState.data.hasMore,
                                isLoading = false
                            )
                        }
                        loadTeachers()
                    }
                    is UiState.Error -> _state.update { 
                        it.copy(error = uiState.message, isLoading = false) 
                    }
                }
            }
        }
    }

    private fun searchClassrooms(query: String) {
        _state.update { it.copy(query = query, page = 1) }
        viewModelScope.launch {
            getClassesUseCase(search = query.takeIf { it.isNotBlank() }, page = 1).collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> _state.update { it.copy(isLoading = true) }
                    is UiState.Success -> {
                        _state.update { 
                            it.copy(
                                classrooms = uiState.data.items,
                                total = uiState.data.total,
                                page = 1,
                                canLoadMore = uiState.data.hasMore,
                                isLoading = false
                            )
                        }
                    }
                    is UiState.Error -> _state.update { 
                        it.copy(error = uiState.message, isLoading = false) 
                    }
                }
            }
        }
    }

    private fun loadMoreClassrooms() {
        if (!_state.value.canLoadMore || _state.value.isLoadingMore) return
        
        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true) }
            val nextPage = _state.value.page + 1
            getClassesUseCase(
                search = _state.value.query.takeIf { it.isNotBlank() },
                page = nextPage
            ).collect { uiState ->
                when (uiState) {
                    is UiState.Success -> {
                        _state.update { 
                            it.copy(
                                classrooms = it.classrooms + uiState.data.items,
                                page = nextPage,
                                canLoadMore = uiState.data.hasMore,
                                isLoadingMore = false
                            )
                        }
                    }
                    else -> _state.update { it.copy(isLoadingMore = false) }
                }
            }
        }
    }

    private fun loadTeachers() {
        viewModelScope.launch {
            getTeachersUseCase(role = "teacher", page = 1).collect { uiState ->
                if (uiState is UiState.Success) {
                    _state.update { it.copy(teachers = uiState.data.items) }
                }
            }
        }
    }

    private fun loadClassForEdit(classId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getClassByIdUseCase(classId).collect { uiState ->
                when (uiState) {
                    is UiState.Success -> {
                        _state.update { 
                            it.copy(
                                name = uiState.data.name,
                                teacherId = uiState.data.teacherId,
                                capacity = uiState.data.capacity?.toString() ?: "",
                                isLoading = false
                            )
                        }
                    }
                    is UiState.Error -> _state.update { 
                        it.copy(error = uiState.message, isLoading = false) 
                    }
                    else -> { }
                }
            }
        }
    }

    private fun loadClassDetail(classId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getClassByIdUseCase(classId).collect { uiState ->
                when (uiState) {
                    is UiState.Success -> {
                        _state.update { 
                            it.copy(
                                currentClass = uiState.data,
                                isLoading = false
                            )
                        }
                    }
                    is UiState.Error -> _state.update { 
                        it.copy(error = uiState.message, isLoading = false) 
                    }
                    else -> { }
                }
            }
        }
    }

    private fun saveClassroom() {
        viewModelScope.launch {
            val currentState = _state.value
            if (currentState.name.isBlank()) {
                _state.update { it.copy(error = "اسم الفصل مطلوب") }
                return@launch
            }

            _state.update { it.copy(isSaving = true, error = null) }

            val result = if (currentState.currentClass != null) {
                updateClassUseCase(
                    id = currentState.currentClass.id,
                    name = currentState.name,
                    teacherId = currentState.teacherId,
                    isActive = true
                )
            } else {
                createClassUseCase(
                    name = currentState.name,
                    description = null,
                    teacherId = currentState.teacherId,
                    capacity = currentState.capacity.toIntOrNull()
                )
            }

            result.onSuccess {
                _effect.send(ClassroomsEffect.ShowMessage("تم حفظ الفصل بنجاح"))
                _state.update { it.copy(isSaving = false, isSaved = true) }
                loadClassrooms()
            }.onFailure {
                _state.update { it.copy(isSaving = false, error = it.error ?: "حدث خطأ") }
                _effect.send(ClassroomsEffect.ShowMessage(it.message ?: "حدث خطأ"))
            }
        }
    }

    private fun deleteClassroom() {
        viewModelScope.launch {
            val id = _state.value.pendingDeleteId ?: return@launch
            _state.update { it.copy(isSaving = true, showDeleteDialog = false) }
            
            deleteClassUseCase(id).onSuccess {
                _effect.send(ClassroomsEffect.ShowMessage("تم حذف الفصل بنجاح"))
                _state.update { it.copy(isSaving = false, pendingDeleteId = null) }
                loadClassrooms()
            }.onFailure {
                _state.update { it.copy(isSaving = false, pendingDeleteId = null) }
                _effect.send(ClassroomsEffect.ShowMessage(it.message ?: "حدث خطأ"))
            }
        }
    }
}
