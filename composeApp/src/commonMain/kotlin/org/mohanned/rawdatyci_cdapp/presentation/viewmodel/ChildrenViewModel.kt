package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.domain.usecase.child.*

data class ChildrenState(
    val children: List<Child> = emptyList(),
    val currentChild: Child? = null,
    val classId: String? = null,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val canLoadMore: Boolean = false,
    val page: Int = 1,
    val error: String? = null,
    val isActionLoading: Boolean = false
)

sealed class ChildrenIntent {
    data class Load(val classId: String? = null) : ChildrenIntent()
    object LoadMore : ChildrenIntent()
    data class LoadChildDetail(val childId: String) : ChildrenIntent()
    data class RateChild(val childId: String, val stars: Int, val notes: String?) : ChildrenIntent()
}

sealed class ChildrenEffect {
    data class ShowMessage(val message: String) : ChildrenEffect()
}

class ChildrenViewModel(
    private val getChildrenByClassUseCase: GetChildrenByClassUseCase,
    private val getMyChildrenUseCase: GetMyChildrenUseCase,
    private val updateChildUseCase: UpdateChildUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(ChildrenState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ChildrenEffect>()
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: ChildrenIntent) {
        when (intent) {
            is ChildrenIntent.Load -> load(intent.classId, reset = true)
            ChildrenIntent.LoadMore -> if (_state.value.canLoadMore) load(_state.value.classId, reset = false)
            is ChildrenIntent.LoadChildDetail -> loadChildDetail(intent.childId)
            is ChildrenIntent.RateChild -> updateRating(intent.childId, intent.stars, intent.notes)
        }
    }

    private fun load(classId: String?, reset: Boolean) = viewModelScope.launch {
        val page = if (reset) 1 else _state.value.page + 1
        if (reset) _state.update { it.copy(isLoading = true, classId = classId, error = null) }
        else _state.update { it.copy(isLoadingMore = true) }

        val flow = if (classId != null) getChildrenByClassUseCase(classId, page)
        else getMyChildrenUseCase(page)

        flow.collect { uiState ->
            when (uiState) {
                is UiState.Success -> {
                    _state.update { st ->
                        st.copy(
                            children = if (reset) uiState.data.items else st.children + uiState.data.items,
                            page = page,
                            canLoadMore = uiState.data.hasMore,
                            isLoading = false,
                            isLoadingMore = false
                        )
                    }
                }
                is UiState.Error -> {
                    _state.update { it.copy(isLoading = false, isLoadingMore = false, error = uiState.message) }
                }
                UiState.Loading -> { }
            }
        }
    }

    private fun loadChildDetail(childId: String) {
        // Child detail logic could be here or we just find in list
        val child = _state.value.children.find { it.id == childId }
        _state.update { it.copy(currentChild = child) }
    }

    private fun updateRating(childId: String, stars: Int, notes: String?) = viewModelScope.launch {
        _state.update { it.copy(isActionLoading = true) }
        val result = updateChildUseCase(childId, notes = notes, rating = stars)
        if (result.isSuccess) {
            _effect.send(ChildrenEffect.ShowMessage("تم تحديث التقييم بنجاح"))
            load(_state.value.classId, true)
        } else {
            _effect.send(ChildrenEffect.ShowMessage(result.exceptionOrNull()?.message ?: "فشل التحديث"))
        }
        _state.update { it.copy(isActionLoading = false) }
    }
}
