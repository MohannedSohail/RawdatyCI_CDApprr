package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.Complaint
import org.mohanned.rawdatyci_cdapp.domain.usecase.complaint.*

data class ComplaintsState(
    val complaints: List<Complaint> = emptyList(),
    val selectedComplaint: Complaint? = null,
    val isLoading: Boolean = false,
    val isActionLoading: Boolean = false,
    val isSuccessInDialog: Boolean = false, // حالة جديدة لإظهار النجاح داخل الحوار
    val error: String? = null,
    val replyText: String = "",
    val showReplyDialog: Boolean = false
)

sealed class ComplaintsIntent {
    object Load : ComplaintsIntent()
    data class TabChanged(val tab: Int) : ComplaintsIntent()
    object LoadMore : ComplaintsIntent()
    data class Submit(val title: String, val content: String, val type: String) : ComplaintsIntent()
    data class OpenReply(val complaint: Complaint) : ComplaintsIntent()
    data class ReplyTextChanged(val text: String) : ComplaintsIntent()
    object SubmitReply : ComplaintsIntent()
    object DismissReply : ComplaintsIntent()
    data class LoadComplaintDetail(val id: String) : ComplaintsIntent()
}

sealed class ComplaintsEffect {
    data class ShowMessage(val message: String) : ComplaintsEffect()
}

class ComplaintsViewModel(
    private val getComplaintsUseCase: GetComplaintsUseCase,
    private val getComplaintByIdUseCase: GetComplaintByIdUseCase,
    private val createComplaintUseCase: CreateComplaintUseCase,
    private val replyToComplaintUseCase: ReplyToComplaintUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ComplaintsState())
    val state = _state.asStateFlow()

    private val _effect = Channel<ComplaintsEffect>()
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: ComplaintsIntent) {
        when (intent) {
            ComplaintsIntent.Load -> loadComplaints()
            is ComplaintsIntent.TabChanged -> { loadComplaints() }
            is ComplaintsIntent.Submit -> submitComplaint(intent.content, intent.type)
            is ComplaintsIntent.OpenReply -> _state.update { it.copy(showReplyDialog = true, selectedComplaint = intent.complaint, isSuccessInDialog = false) }
            is ComplaintsIntent.ReplyTextChanged -> _state.update { it.copy(replyText = intent.text) }
            ComplaintsIntent.SubmitReply -> submitReplySimulated()
            ComplaintsIntent.DismissReply -> _state.update { it.copy(showReplyDialog = false, selectedComplaint = null, replyText = "", isSuccessInDialog = false) }
            is ComplaintsIntent.LoadComplaintDetail -> loadComplaintDetail(intent.id)
            else -> {}
        }
    }

    private fun loadComplaints() {
        viewModelScope.launch {
            getComplaintsUseCase().collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> _state.update { it.copy(isLoading = true) }
                    is UiState.Success -> _state.update { it.copy(complaints = uiState.data.items, isLoading = false) }
                    is UiState.Error -> _state.update { it.copy(error = uiState.message, isLoading = false) }
                }
            }
        }
    }

    private fun loadComplaintDetail(id: String) {
        viewModelScope.launch {
            getComplaintByIdUseCase(id).collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> _state.update { it.copy(isLoading = true) }
                    is UiState.Success -> _state.update { it.copy(selectedComplaint = uiState.data, isLoading = false) }
                    is UiState.Error -> _state.update { it.copy(error = uiState.message, isLoading = false) }
                }
            }
        }
    }

    private fun submitComplaint(content: String, type: String) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true) }
            val result = createComplaintUseCase(content, type)
            if (result.isSuccess) {
                _effect.send(ComplaintsEffect.ShowMessage("تم الإرسال بنجاح"))
                loadComplaints()
            } else {
                _effect.send(ComplaintsEffect.ShowMessage(result.exceptionOrNull()?.message ?: "فشل الإرسال"))
            }
            _state.update { it.copy(isActionLoading = false) }
        }
    }

    private fun submitReplySimulated() {
        val complaintToReply = _state.value.selectedComplaint ?: return
        
        viewModelScope.launch {
            // 1. إظهار الـ Progress
            _state.update { it.copy(isActionLoading = true, isSuccessInDialog = false) }
            
            delay(1500) // وقت المحاكاة
            
            // 2. إخفاء الـ Progress وإظهار رسالة النجاح داخل الحوار
            _state.update { it.copy(isActionLoading = false, isSuccessInDialog = true) }
            
            delay(1000) // وقت عرض كلمة "تم الرد بنجاح" قبل إغلاق الحوار
            
            // 3. إغلاق الحوار وحذف الشكوى من القائمة
            _state.update { currentState ->
                val updatedList = currentState.complaints.filter { it.id != complaintToReply.id }
                currentState.copy(
                    complaints = updatedList,
                    showReplyDialog = false,
                    selectedComplaint = null,
                    replyText = "",
                    isSuccessInDialog = false
                )
            }
        }
    }
}
