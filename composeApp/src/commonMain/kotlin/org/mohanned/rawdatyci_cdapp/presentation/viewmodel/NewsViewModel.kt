package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.News
import org.mohanned.rawdatyci_cdapp.domain.usecase.news.*

data class NewsState(
    val news: List<News> = emptyList(),
    val currentNews: News? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val showDeleteDialog: Boolean = false,
    val pendingDeleteId: String? = null,
    val title: String = "",
    val body: String = "",
    val type: String = "news",
    val isVisible: Boolean = true
)

sealed class NewsIntent {
    object Load : NewsIntent()
    data class Search(val query: String) : NewsIntent()
    object LoadMore : NewsIntent()
    data class LoadNewsDetail(val id: String) : NewsIntent()
    data class LoadNewsForEdit(val id: String) : NewsIntent()
    data class TitleChanged(val v: String) : NewsIntent()
    data class BodyChanged(val v: String) : NewsIntent()
    data class TypeChanged(val v: String) : NewsIntent()
    data class VisibilityChanged(val v: Boolean) : NewsIntent()
    object Save : NewsIntent()
    data class DeleteRequest(val id: String) : NewsIntent()
    object ConfirmDelete : NewsIntent()
    object DismissDelete : NewsIntent()
}

sealed class NewsEffect {
    data class ShowMessage(val message: String) : NewsEffect()
}

class NewsViewModel(
    private val getNewsUseCase: GetNewsUseCase,
    private val getNewsByIdUseCase: GetNewsByIdUseCase,
    private val createNewsUseCase: CreateNewsUseCase,
    private val updateNewsUseCase: UpdateNewsUseCase,
    private val deleteNewsUseCase: DeleteNewsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(NewsState())
    val state = _state.asStateFlow()

    private val _effect = Channel<NewsEffect>()
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: NewsIntent) {
        when (intent) {
            NewsIntent.Load -> loadNews()
            is NewsIntent.Search -> loadNews(intent.query)
            NewsIntent.LoadMore -> { /* Pagination logic here */ }
            is NewsIntent.LoadNewsDetail -> loadNewsDetail(intent.id)
            is NewsIntent.LoadNewsForEdit -> loadNewsForEdit(intent.id)
            is NewsIntent.TitleChanged -> _state.update { it.copy(title = intent.v) }
            is NewsIntent.BodyChanged -> _state.update { it.copy(body = intent.v) }
            is NewsIntent.TypeChanged -> _state.update { it.copy(type = intent.v) }
            is NewsIntent.VisibilityChanged -> _state.update { it.copy(isVisible = intent.v) }
            NewsIntent.Save -> saveNews()
            is NewsIntent.DeleteRequest -> _state.update { it.copy(showDeleteDialog = true, pendingDeleteId = intent.id) }
            NewsIntent.ConfirmDelete -> deleteNews()
            NewsIntent.DismissDelete -> _state.update { it.copy(showDeleteDialog = false, pendingDeleteId = null) }
        }
    }

    private fun loadNews(query: String? = null) {
        viewModelScope.launch {
            getNewsUseCase(search = query).collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> _state.update { it.copy(isLoading = true) }
                    is UiState.Success -> _state.update { it.copy(news = uiState.data.items, isLoading = false) }
                    is UiState.Error -> _state.update { it.copy(error = uiState.message, isLoading = false) }
                }
            }
        }
    }

    private fun loadNewsDetail(id: String) {
        viewModelScope.launch {
            getNewsByIdUseCase(id).collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> _state.update { it.copy(isLoading = true) }
                    is UiState.Success -> _state.update { it.copy(currentNews = uiState.data, isLoading = false) }
                    is UiState.Error -> _state.update { it.copy(error = uiState.message, isLoading = false) }
                }
            }
        }
    }

    private fun loadNewsForEdit(id: String) {
        viewModelScope.launch {
            getNewsByIdUseCase(id).collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> _state.update { it.copy(isLoading = true) }
                    is UiState.Success -> _state.update { 
                        it.copy(
                            title = uiState.data.title,
                            body = uiState.data.body,
                            type = uiState.data.type.name.lowercase(),
                            isVisible = uiState.data.isVisible,
                            currentNews = uiState.data,
                            isLoading = false
                        ) 
                    }
                    is UiState.Error -> _state.update { it.copy(error = uiState.message, isLoading = false) }
                }
            }
        }
    }

    private fun saveNews() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val current = _state.value
            val result = if (current.currentNews != null) {
                updateNewsUseCase(current.currentNews.id, current.title, current.body, current.isVisible)
            } else {
                createNewsUseCase(current.title, current.body, current.type, current.isVisible)
            }
            
            if (result.isSuccess) {
                _effect.send(NewsEffect.ShowMessage("تم الحفظ بنجاح"))
                _state.update { it.copy(isSaved = true) }
                loadNews()
            } else {
                _effect.send(NewsEffect.ShowMessage(result.exceptionOrNull()?.message ?: "فشل الحفظ"))
            }
            _state.update { it.copy(isSaving = false) }
        }
    }

    private fun deleteNews() {
        viewModelScope.launch {
            val id = _state.value.pendingDeleteId ?: return@launch
            _state.update { it.copy(isSaving = true, showDeleteDialog = false) }
            deleteNewsUseCase(id).onSuccess {
                _effect.send(NewsEffect.ShowMessage("تم حذف الخبر بنجاح"))
                loadNews()
            }.onFailure {
                _effect.send(NewsEffect.ShowMessage(it.message ?: "فشل الحذف"))
            }
            _state.update { it.copy(isSaving = false, pendingDeleteId = null) }
        }
    }
}
