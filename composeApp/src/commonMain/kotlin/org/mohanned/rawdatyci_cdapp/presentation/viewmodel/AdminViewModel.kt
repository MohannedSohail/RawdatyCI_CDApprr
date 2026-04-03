package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import org.mohanned.rawdatyci_cdapp.core.base.BaseViewModel
import org.mohanned.rawdatyci_cdapp.core.base.UiEffect
import org.mohanned.rawdatyci_cdapp.core.base.UiIntent
import org.mohanned.rawdatyci_cdapp.core.base.UiState
import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.model.Classroom
import org.mohanned.rawdatyci_cdapp.domain.model.Complaint
import org.mohanned.rawdatyci_cdapp.domain.model.News
import org.mohanned.rawdatyci_cdapp.domain.model.User
import org.mohanned.rawdatyci_cdapp.domain.repository.ClassesRepository
import org.mohanned.rawdatyci_cdapp.domain.repository.ComplaintsRepository
import org.mohanned.rawdatyci_cdapp.domain.repository.NewsRepository
import org.mohanned.rawdatyci_cdapp.domain.repository.SyncRepository
import org.mohanned.rawdatyci_cdapp.domain.repository.UsersRepository

// ══════════════════════════════════════════════════════
//  DASHBOARD
// ══════════════════════════════════════════════════════
data class DashboardState(
    val isLoading: Boolean = true,
    val totalClasses: Int = 0,
    val totalTeachers: Int = 0,
    val totalParents: Int = 0,
    val recentNews: List<News> = emptyList(),
    val error: String? = null,
) : UiState

sealed class DashboardIntent : UiIntent {
    object Load : DashboardIntent()
    object Refresh : DashboardIntent()
}

sealed class DashboardEffect : UiEffect {
    data class ShowError(val message: String) : DashboardEffect()
}

class DashboardViewModel(
    private val classesRepo: ClassesRepository,
    private val usersRepo: UsersRepository,
    private val newsRepo: NewsRepository,
    private val syncRepo: SyncRepository,
) : BaseViewModel<DashboardState, DashboardIntent, DashboardEffect>(DashboardState()) {

    override suspend fun handleIntent(intent: DashboardIntent) {
        when (intent) {
            DashboardIntent.Load,
            DashboardIntent.Refresh -> {
                syncRepo.syncPendingActions()
                load()
            }
        }
    }

    private suspend fun load() {
        updateState { copy(isLoading = true, error = null) }

        var classesCount = 0
        var teachersCount = 0
        var parentsCount = 0

        when (val r = classesRepo.getClasses()) {
            is ApiResponse.Success -> classesCount = r.data.meta.total
            else -> {}
        }

        when (val r = usersRepo.getUsers(role = "teacher")) {
            is ApiResponse.Success -> teachersCount = r.data.meta.total
            else -> {}
        }

        when (val r = usersRepo.getUsers(role = "parent")) {
            is ApiResponse.Success -> parentsCount = r.data.meta.total
            else -> {}
        }

        val newsRes = newsRepo.getNews()
        val news = if (newsRes is ApiResponse.Success) newsRes.data.data.take(5) else emptyList()

        updateState {
            copy(
                isLoading = false,
                totalClasses = classesCount,
                totalTeachers = teachersCount,
                totalParents = parentsCount,
                recentNews = news,
            )
        }
    }
}

// ══════════════════════════════════════════════════════
//  CLASSROOMS
// ══════════════════════════════════════════════════════
data class ClassroomsState(
    val isLoading: Boolean = true,
    val classrooms: List<Classroom> = emptyList(),
    val query: String = "",
    val currentPage: Int = 1,
    val canLoadMore: Boolean = false,
    val isLoadingMore: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val classToDelete: Classroom? = null,
    val error: String? = null,
) : UiState

sealed class ClassroomsIntent : UiIntent {
    object Load : ClassroomsIntent()
    object LoadMore : ClassroomsIntent()
    data class Search(val query: String) : ClassroomsIntent()
    data class DeleteRequest(val classroom: Classroom) : ClassroomsIntent()
    object ConfirmDelete : ClassroomsIntent()
    object DismissDelete : ClassroomsIntent()
}

sealed class ClassroomsEffect : UiEffect {
    data class ShowSuccess(val message: String) : ClassroomsEffect()
    data class ShowError(val message: String) : ClassroomsEffect()
}

class ClassroomsViewModel(
    private val repo: ClassesRepository,
) : BaseViewModel<ClassroomsState, ClassroomsIntent, ClassroomsEffect>(ClassroomsState()) {

    override suspend fun handleIntent(intent: ClassroomsIntent) {
        when (intent) {
            ClassroomsIntent.Load -> load(reset = true)
            ClassroomsIntent.LoadMore -> load(reset = false)
            is ClassroomsIntent.Search -> {
                updateState { copy(query = intent.query) }
                load(reset = true)
            }
            is ClassroomsIntent.DeleteRequest -> updateState {
                copy(showDeleteDialog = true, classToDelete = intent.classroom)
            }
            ClassroomsIntent.DismissDelete -> updateState {
                copy(showDeleteDialog = false, classToDelete = null)
            }
            ClassroomsIntent.ConfirmDelete -> deleteClass()
        }
    }

    private suspend fun load(reset: Boolean) {
        if (reset) {
            updateState { copy(isLoading = true, currentPage = 1, classrooms = emptyList()) }
        } else {
            if (!state.value.canLoadMore || state.value.isLoadingMore) return
            updateState { copy(isLoadingMore = true) }
        }

        val page = if (reset) 1 else state.value.currentPage + 1
        when (val r = repo.getClasses(search = if (state.value.query.isBlank()) null else state.value.query, page = page)) {
            is ApiResponse.Success -> {
                updateState {
                    copy(
                        isLoading = false,
                        isLoadingMore = false,
                        classrooms = if (reset) r.data.data else classrooms + r.data.data,
                        currentPage = page,
                        canLoadMore = r.data.meta.page < r.data.meta.lastPage
                    )
                }
            }
            else -> updateState { copy(isLoading = false, isLoadingMore = false) }
        }
    }

    private suspend fun deleteClass() {
        val target = state.value.classToDelete ?: return
        updateState { copy(showDeleteDialog = false, classToDelete = null) }

        when (val r = repo.deleteClass(target.id)) {
            is ApiResponse.Success -> {
                updateState {
                    copy(classrooms = classrooms.filter { it.id != target.id })
                }
                emitEffect(ClassroomsEffect.ShowSuccess("تم حذف الفصل بنجاح"))
            }
            is ApiResponse.Error -> emitEffect(ClassroomsEffect.ShowError(r.message))
            is ApiResponse.NetworkError -> emitEffect(ClassroomsEffect.ShowError(r.message))
        }
    }
}

// ══════════════════════════════════════════════════════
//  USERS
// ══════════════════════════════════════════════════════
data class UsersState(
    val isLoading: Boolean = true,
    val users: List<User> = emptyList(),
    val query: String = "",
    val selectedTab: Int = 0,
    val currentPage: Int = 1,
    val canLoadMore: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
) : UiState

sealed class UsersIntent : UiIntent {
    object Load : UsersIntent()
    object LoadMore : UsersIntent()
    data class Search(val query: String) : UsersIntent()
    data class TabChanged(val tab: Int) : UsersIntent()
    data class DeleteUser(val id: Int) : UsersIntent()
}

sealed class UsersEffect : UiEffect {
    data class ShowSuccess(val message: String) : UsersEffect()
    data class ShowError(val message: String) : UsersEffect()
}

class UsersViewModel(
    private val repo: UsersRepository,
) : BaseViewModel<UsersState, UsersIntent, UsersEffect>(UsersState()) {

    override suspend fun handleIntent(intent: UsersIntent) {
        when (intent) {
            UsersIntent.Load -> load(reset = true)
            UsersIntent.LoadMore -> load(reset = false)
            is UsersIntent.Search -> {
                updateState { copy(query = intent.query) }
                load(reset = true)
            }
            is UsersIntent.TabChanged -> {
                updateState { copy(selectedTab = intent.tab) }
                load(reset = true)
            }
            is UsersIntent.DeleteUser -> deleteUser(intent.id)
        }
    }

    private suspend fun load(reset: Boolean) {
        if (reset) {
            updateState { copy(isLoading = true, currentPage = 1, users = emptyList()) }
        } else {
            if (!state.value.canLoadMore || state.value.isLoadingMore) return
            updateState { copy(isLoadingMore = true) }
        }

        val page = if (reset) 1 else state.value.currentPage + 1
        val role = when (state.value.selectedTab) {
            0 -> "teacher"
            1 -> "parent"
            else -> null
        }
        
        when (val r = repo.getUsers(role = role, search = if (state.value.query.isBlank()) null else state.value.query, page = page)) {
            is ApiResponse.Success -> updateState {
                copy(
                    isLoading = false,
                    isLoadingMore = false,
                    users = if (reset) r.data.data else users + r.data.data,
                    currentPage = page,
                    canLoadMore = r.data.meta.page < r.data.meta.lastPage
                )
            }
            else -> updateState { copy(isLoading = false, isLoadingMore = false) }
        }
    }

    private suspend fun deleteUser(id: Int) {
        when (val r = repo.deleteUser(id)) {
            is ApiResponse.Success -> {
                updateState { copy(users = users.filter { it.id != id }) }
                emitEffect(UsersEffect.ShowSuccess("تم حذف المستخدم"))
            }
            is ApiResponse.Error -> emitEffect(UsersEffect.ShowError(r.message))
            is ApiResponse.NetworkError -> emitEffect(UsersEffect.ShowError(r.message))
        }
    }
}

// ══════════════════════════════════════════════════════
//  NEWS
// ══════════════════════════════════════════════════════
data class NewsState(
    val isLoading: Boolean = true,
    val news: List<News> = emptyList(),
    val query: String = "",
    val currentPage: Int = 1,
    val canLoadMore: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
) : UiState

sealed class NewsIntent : UiIntent {
    object Load : NewsIntent()
    object LoadMore : NewsIntent()
    data class Search(val query: String) : NewsIntent()
    data class Delete(val id: Int) : NewsIntent()
}

sealed class NewsEffect : UiEffect {
    data class ShowSuccess(val msg: String) : NewsEffect()
    data class ShowError(val msg: String) : NewsEffect()
}

class NewsViewModel(
    private val repo: NewsRepository,
) : BaseViewModel<NewsState, NewsIntent, NewsEffect>(NewsState()) {

    override suspend fun handleIntent(intent: NewsIntent) {
        when (intent) {
            NewsIntent.Load -> load(reset = true)
            NewsIntent.LoadMore -> load(reset = false)
            is NewsIntent.Search -> {
                updateState { copy(query = intent.query) }
                load(reset = true)
            }
            is NewsIntent.Delete -> deleteNews(intent.id)
        }
    }

    private suspend fun load(reset: Boolean) {
        if (reset) {
            updateState { copy(isLoading = true, currentPage = 1, news = emptyList()) }
        } else {
            if (!state.value.canLoadMore || state.value.isLoadingMore) return
            updateState { copy(isLoadingMore = true) }
        }

        val page = if (reset) 1 else state.value.currentPage + 1
        when (val r = repo.getNews(search = if (state.value.query.isBlank()) null else state.value.query, page = page)) {
            is ApiResponse.Success -> {
                updateState {
                    copy(
                        isLoading = false,
                        isLoadingMore = false,
                        news = if (reset) r.data.data else news + r.data.data,
                        currentPage = page,
                        canLoadMore = r.data.meta.page < r.data.meta.lastPage
                    )
                }
            }
            else -> updateState { copy(isLoading = false, isLoadingMore = false) }
        }
    }

    private suspend fun deleteNews(id: Int) {
        when (val r = repo.deleteNews(id)) {
            is ApiResponse.Success -> {
                updateState { copy(news = news.filter { it.id != id }) }
                emitEffect(NewsEffect.ShowSuccess("تم حذف الخبر"))
            }
            else -> {}
        }
    }
}

// ══════════════════════════════════════════════════════
//  COMPLAINTS
// ══════════════════════════════════════════════════════
data class ComplaintsState(
    val isLoading: Boolean = true,
    val complaints: List<Complaint> = emptyList(),
    val selectedTab: Int = 0,
    val currentPage: Int = 1,
    val canLoadMore: Boolean = false,
    val isLoadingMore: Boolean = false,
    val replyText: String = "",
    val showReplyDialog: Boolean = false,
    val selectedComplaint: Complaint? = null,
    val error: String? = null,
) : UiState

sealed class ComplaintsIntent : UiIntent {
    object Load : ComplaintsIntent()
    object LoadMore : ComplaintsIntent()
    data class SelectTab(val tab: Int) : ComplaintsIntent()
    data class ReplyChanged(val text: String) : ComplaintsIntent()
    data class OpenReply(val complaint: Complaint) : ComplaintsIntent()
    object SubmitReply : ComplaintsIntent()
    object DismissReply : ComplaintsIntent()
    data class Submit(val title: String, val body: String) : ComplaintsIntent()
}

sealed class ComplaintsEffect : UiEffect {
    object ShowSuccess : ComplaintsEffect()
}

class ComplaintsViewModel(
    private val repo: ComplaintsRepository,
) : BaseViewModel<ComplaintsState, ComplaintsIntent, ComplaintsEffect>(ComplaintsState()) {

    override suspend fun handleIntent(intent: ComplaintsIntent) {
        when (intent) {
            ComplaintsIntent.Load -> load(reset = true)
            ComplaintsIntent.LoadMore -> load(reset = false)
            is ComplaintsIntent.SelectTab -> {
                updateState { copy(selectedTab = intent.tab) }
                load(reset = true)
            }
            is ComplaintsIntent.ReplyChanged -> updateState { copy(replyText = intent.text) }
            is ComplaintsIntent.OpenReply -> updateState {
                copy(showReplyDialog = true, selectedComplaint = intent.complaint, replyText = "")
            }
            ComplaintsIntent.DismissReply -> updateState {
                copy(showReplyDialog = false, selectedComplaint = null)
            }
            ComplaintsIntent.SubmitReply -> submitReply()
            is ComplaintsIntent.Submit -> submitComplaint(intent.title, intent.body)
        }
    }

    private suspend fun load(reset: Boolean) {
        if (reset) {
            updateState { copy(isLoading = true, currentPage = 1, complaints = emptyList()) }
        } else {
            if (!state.value.canLoadMore || state.value.isLoadingMore) return
            updateState { copy(isLoadingMore = true) }
        }

        val page = if (reset) 1 else state.value.currentPage + 1
        when (val r = repo.getComplaints(
            status = if (state.value.selectedTab == 0) "pending" else "resolved",
            page = page
        )) {
            is ApiResponse.Success -> updateState {
                copy(
                    isLoading = false,
                    isLoadingMore = false,
                    complaints = if (reset) r.data.data else complaints + r.data.data,
                    currentPage = page,
                    canLoadMore = r.data.meta.page < r.data.meta.lastPage
                )
            }
            else -> updateState { copy(isLoading = false, isLoadingMore = false) }
        }
    }

    private suspend fun submitReply() {
        val complaint = state.value.selectedComplaint ?: return
        val reply = state.value.replyText
        if (reply.isBlank()) return

        when (val r = repo.replyToComplaint(
            complaint.id, reply,
            status = "resolved",
        )) {
            is ApiResponse.Success -> {
                updateState { copy(showReplyDialog = false) }
                load(reset = true)
            }
            else -> {}
        }
    }

    private suspend fun submitComplaint(title: String, body: String) {
        updateState { copy(isLoading = true) }
        when (val r = repo.createComplaint(title, body)) {
            is ApiResponse.Success -> {
                updateState { copy(isLoading = false) }
                emitEffect(ComplaintsEffect.ShowSuccess)
            }
            else -> updateState { copy(isLoading = false) }
        }
    }
}