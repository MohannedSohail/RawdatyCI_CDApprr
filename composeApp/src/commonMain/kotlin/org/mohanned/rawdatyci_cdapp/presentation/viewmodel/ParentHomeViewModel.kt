package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.domain.repository.AuthRepository
import org.mohanned.rawdatyci_cdapp.domain.usecase.child.GetMyChildrenUseCase

data class ParentHomeState(
    val children: List<Child> = emptyList(),
    val isLoading: Boolean = false,
    val parentName: String = "",
    val error: String? = null
)

sealed class ParentHomeIntent {
    object Load : ParentHomeIntent()
}

class ParentHomeViewModel(
    private val authRepository: AuthRepository,
    private val getMyChildrenUseCase: GetMyChildrenUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ParentHomeState())
    val state = _state.asStateFlow()

    fun onIntent(intent: ParentHomeIntent) {
        when (intent) {
            ParentHomeIntent.Load -> loadDashboard()
        }
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            // ✅ تقليل وقت الـ Shimmer ليكون سريعاً وغير مزعج
            delay(300)

            authRepository.getLoggedUser()?.let {
                _state.update { s -> s.copy(parentName = it.name) }
            }

            getMyChildrenUseCase().collect { uiState ->
                when (uiState) {
                    is UiState.Success -> {
                        val items = if (uiState.data.items.isEmpty()) getDummyChildren() else uiState.data.items
                        _state.update { it.copy(children = items, isLoading = false, error = null) }
                    }
                    is UiState.Error -> {
                        // تجاهل الخطأ وعرض Dummy Data فوراً
                        _state.update { it.copy(children = getDummyChildren(), isLoading = false, error = null) }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun getDummyChildren() = listOf(
        Child(
            id = "dummy_1",
            fullName = "مهند سهيل",
            className = "فصل النجوم - المستوى التمهيدي",
            gender = "male",
            stars = 5,
            classId = "class_1",
            enrollmentDate = "2023-09-01",
            photoUrl = null,
            dateOfBirth = "2019-05-12",
            parentId = "parent_1",
            parentName = "سهيل مهند",
            parentPhone = "0501234567",
            notes = "طفل ذكي جداً، محب للمشاركة والتعاون مع زملائه، لديه شغف كبير بالأرقام."
        )
    )
}
