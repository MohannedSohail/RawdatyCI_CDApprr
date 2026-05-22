package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.News
import org.mohanned.rawdatyci_cdapp.domain.model.NewsType
import org.mohanned.rawdatyci_cdapp.domain.usecase.news.*

data class NewsState(
    val news: List<News> = emptyList(),
    val currentNews: News? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val title: String = "",
    val body: String = "",
    val type: String = "news",
    val isVisible: Boolean = true,
    val showDeleteConfirmDialog: Boolean = false,
    val selectedNewsId: String? = null,
    val searchQuery: String = ""
)

sealed class NewsIntent {
    object Load : NewsIntent()
    data class Search(val query: String) : NewsIntent()
    data class LoadNewsDetail(val id: String) : NewsIntent()
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
            is NewsIntent.Search -> {
                _state.update { it.copy(searchQuery = intent.query) }
                loadNews(intent.query)
            }
            is NewsIntent.LoadNewsDetail -> loadNewsDetail(intent.id)
            is NewsIntent.DeleteRequest -> _state.update { it.copy(showDeleteConfirmDialog = true, selectedNewsId = intent.id) }
            NewsIntent.ConfirmDelete -> deleteNews()
            NewsIntent.DismissDelete -> _state.update { it.copy(showDeleteConfirmDialog = false, selectedNewsId = null) }
        }
    }

    private fun loadNews(query: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            delay(300) // Shimmer سريع

            getNewsUseCase(search = query).collect { uiState ->
                when (uiState) {
                    is UiState.Success -> {
                        val items = if (uiState.data.items.isEmpty()) getDummyNews() else uiState.data.items
                        _state.update { it.copy(news = items, isLoading = false, error = null) }
                    }
                    is UiState.Error -> {
                        // Fallback to dummy data
                        val items = getDummyNews().filter { 
                            query == null || it.title.contains(query, true) || it.body.contains(query, true) 
                        }
                        _state.update { it.copy(news = items, isLoading = false, error = null) }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun loadNewsDetail(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getNewsByIdUseCase(id).collect { uiState ->
                if (uiState is UiState.Success) {
                    _state.update { it.copy(currentNews = uiState.data, isLoading = false) }
                } else if (uiState is UiState.Error) {
                    _state.update { it.copy(currentNews = getDummyNews().find { it.id == id }, isLoading = false) }
                }
            }
        }
    }

    private fun deleteNews() {
        val id = _state.value.selectedNewsId ?: return
        viewModelScope.launch {
            _state.update { it.copy(showDeleteConfirmDialog = false, isLoading = true) }
            val result = deleteNewsUseCase(id)
            if (result.isSuccess) {
                _effect.send(NewsEffect.ShowMessage("تم حذف الخبر بنجاح"))
                loadNews()
            } else {
                _effect.send(NewsEffect.ShowMessage("تم الحذف من القائمة محلياً (Dummy Mode)"))
                _state.update { it.copy(
                    news = it.news.filterNot { n -> n.id == id },
                    isLoading = false
                ) }
            }
        }
    }

    private fun getDummyNews() = listOf(
        News(
            id = "n1",
            title = "انطلاق الرحلة المدرسية السنوية",
            body = "نعلن لجميع أولياء الأمور الكرام عن بدء التسجيل في الرحلة السنوية للروضة إلى حديقة الحيوانات الأسبوع القادم. يرجى مراجعة الإدارة للتسجيل.",
            imageUrl = null,
            isVisible = true,
            type = NewsType.NEWS,
            authorName = "الإدارة العامة",
            createdAt = "2024-04-21"
        ),
        News(
            id = "n2",
            title = "إجازة رسمية بمناسبة عيد الفطر",
            body = "بمناسبة قرب حلول عيد الفطر السعيد، تقرر أن تكون الإجازة اعتباراً من يوم الثلاثاء القادم وحتى نهاية الأسبوع. كل عام وأنتم بخير.",
            imageUrl = null,
            isVisible = true,
            type = NewsType.ANNOUNCEMENT,
            authorName = "إدارة الروضة",
            createdAt = "2024-04-20"
        ),
        News(
            id = "n3",
            title = "ورشة عمل للمعلمات: أساليب التعليم النشط",
            body = "سيتم عقد ورشة عمل تدريبية لجميع المعلمات يوم السبت القادم في قاعة التدريب الكبرى حول أساليب التعليم النشط الحديثة.",
            imageUrl = null,
            isVisible = false,
            type = NewsType.NEWS,
            authorName = "قسم التطوير التربوي",
            createdAt = "2024-04-19"
        )
    )
}
