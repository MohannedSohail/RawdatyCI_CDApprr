package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.usecase.news.CreateNewsUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.news.GetNewsByIdUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.news.UpdateNewsUseCase

data class AddEditNewsState(
    val id: String? = null,
    val title: String = "",
    val body: String = "",
    val isVisible: Boolean = true,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
) {
    val isEditMode: Boolean get() = id != null
}

sealed class AddEditNewsEffect {
    data class ShowMessage(val message: String) : AddEditNewsEffect()
    object SaveSuccess : AddEditNewsEffect()
}

sealed class AddEditNewsIntent {
    data class Load(val id: String?) : AddEditNewsIntent()
    data class TitleChanged(val v: String) : AddEditNewsIntent()
    data class BodyChanged(val v: String) : AddEditNewsIntent()
    data class VisibilityChanged(val v: Boolean) : AddEditNewsIntent()
    object Save : AddEditNewsIntent()
}

class AdminAddEditNewsViewModel(
    private val getNewsByIdUseCase: GetNewsByIdUseCase,
    private val createNewsUseCase: CreateNewsUseCase,
    private val updateNewsUseCase: UpdateNewsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(AddEditNewsState())
    val state = _state.asStateFlow()

    private val _effect = Channel<AddEditNewsEffect>()
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: AddEditNewsIntent) {
        when (intent) {
            is AddEditNewsIntent.Load -> load(intent.id)
            is AddEditNewsIntent.TitleChanged -> _state.update { it.copy(title = intent.v, error = null) }
            is AddEditNewsIntent.BodyChanged -> _state.update { it.copy(body = intent.v, error = null) }
            is AddEditNewsIntent.VisibilityChanged -> _state.update { it.copy(isVisible = intent.v) }
            AddEditNewsIntent.Save -> save()
        }
    }

    private fun load(id: String?) = viewModelScope.launch {
        if (id == null) {
            _state.update { AddEditNewsState() }
            return@launch
        }
        _state.update { it.copy(isLoading = true, id = id) }
        getNewsByIdUseCase(id).collect { uiState ->
            when (uiState) {
                is UiState.Success -> {
                    val n = uiState.data
                    _state.update {
                        it.copy(
                            title = n.title,
                            body = n.body,
                            isVisible = n.isVisible,
                            isLoading = false
                        )
                    }
                }
                is UiState.Error -> _state.update { it.copy(isLoading = false, error = uiState.message) }
                UiState.Loading -> { }
            }
        }
    }

    private fun save() = viewModelScope.launch {
        val s = _state.value
        if (s.title.isBlank() || s.body.isBlank()) {
            _effect.send(AddEditNewsEffect.ShowMessage("العنوان والمحتوى مطلوبان"))
            return@launch
        }

        _state.update { it.copy(isSaving = true) }
        val result = if (s.isEditMode) {
            updateNewsUseCase(s.id!!, s.title, s.body, s.isVisible)
        } else {
            createNewsUseCase(s.title, s.body, isVisible = s.isVisible)
        }

        if (result.isSuccess) {
            _effect.send(AddEditNewsEffect.ShowMessage("تم الحفظ بنجاح"))
            _effect.send(AddEditNewsEffect.SaveSuccess)
        } else {
            val errorMsg = result.exceptionOrNull()?.message ?: "فشل الحفظ"
            _state.update { it.copy(isSaving = false, error = errorMsg) }
            _effect.send(AddEditNewsEffect.ShowMessage(errorMsg))
        }
    }
}
