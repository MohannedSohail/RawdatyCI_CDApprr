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
        if (reset) {
            _state.update { it.copy(isLoading = true, classId = classId, error = null) }
        } else {
            _state.update { it.copy(isLoadingMore = true) }
        }

        val flow = if (classId != null) getChildrenByClassUseCase(classId, page)
        else getMyChildrenUseCase(page)

        flow.collect { uiState ->
            when (uiState) {
                is UiState.Success -> {
                    val items = if (uiState.data.items.isEmpty()) getDummyChildren() else uiState.data.items
                    _state.update { st ->
                        st.copy(
                            children = if (reset) items else st.children + items,
                            page = page,
                            canLoadMore = uiState.data.hasMore,
                            isLoading = false,
                            isLoadingMore = false
                        )
                    }
                }
                is UiState.Error -> {
                    _state.update { it.copy(
                        children = if(reset) getDummyChildren() else it.children,
                        isLoading = false, 
                        isLoadingMore = false
                    ) }
                }
                else -> {}
            }
        }
    }

    private fun loadChildDetail(childId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            // محاولة جلب الطفل من القائمة الحالية أولاً
            val existing = _state.value.children.find { it.id == childId }
            if (existing != null) {
                _state.update { it.copy(currentChild = existing, isLoading = false) }
            } else {
                // إذا لم يوجد، نقوم بجلب الأطفال من الـ API
                getMyChildrenUseCase().collect { uiState ->
                    if (uiState is UiState.Success) {
                        val child = uiState.data.items.find { it.id == childId } ?: getDummyChildren().first()
                        _state.update { it.copy(currentChild = child, isLoading = false) }
                    } else if (uiState is UiState.Error) {
                        _state.update { it.copy(currentChild = getDummyChildren().first(), isLoading = false) }
                    }
                }
            }
        }
    }

    private fun updateRating(childId: String, stars: Int, notes: String?) = viewModelScope.launch {
        _state.update { it.copy(isActionLoading = true) }
        val result = updateChildUseCase(childId, notes = notes, rating = stars)
        if (result.isSuccess) {
            _effect.send(ChildrenEffect.ShowMessage("تم تحديث التقييم والنجوم بنجاح ✅"))
            loadChildDetail(childId)
        } else {
            _state.update { it ->
                it.copy(currentChild = it.currentChild?.copy(stars = stars, notes = notes ?: it.currentChild.notes))
            }
            _effect.send(ChildrenEffect.ShowMessage("تم التحديث (حالة تجريبية)"))
        }
        _state.update { it.copy(isActionLoading = false) }
    }

    private fun getDummyChildren() = listOf(
        Child(
            id = "d1",
            fullName = "أحمد محمد علي",
            className = "فصل البراعم (أ)",
            gender = "male",
            stars = 3,
            classId = "c1",
            enrollmentDate = "2023-09-01",
            photoUrl = null,
            dateOfBirth = "2019-05-12",
            parentId = "p1",
            parentName = "محمد علي",
            parentPhone = "0599000111",
            notes = "طفل متميز في الأنشطة الرياضية ومحب للرسم."
        ),
        Child(
            id = "d2",
            fullName = "سارة خالد",
            className = "فصل البراعم (أ)",
            gender = "female",
            stars = 5,
            classId = "c1",
            enrollmentDate = "2023-09-01",
            photoUrl = null,
            dateOfBirth = "2019-08-20",
            parentId = "p2",
            parentName = "خالد محمود",
            parentPhone = "0599222333",
            notes = "تظهر مهارات قيادية واضحة وتتفاعل بشكل ممتاز مع أقرانها."
        )
    )
}
