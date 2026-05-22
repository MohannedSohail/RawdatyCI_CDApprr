package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.Complaint
import org.mohanned.rawdatyci_cdapp.domain.model.ComplaintStatus
import org.mohanned.rawdatyci_cdapp.domain.usecase.complaint.*

data class ComplaintsState(
    val complaints: List<Complaint> = emptyList(),
    val selectedComplaint: Complaint? = null,
    val isLoading: Boolean = false,
    val isActionLoading: Boolean = false,
    val isSuccessInDialog: Boolean = false,
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
            is ComplaintsIntent.Submit -> submitComplaint(intent.content, intent.type)
            is ComplaintsIntent.LoadComplaintDetail -> loadComplaintDetail(intent.id)
            is ComplaintsIntent.OpenReply -> {
                _state.update { it.copy(showReplyDialog = true, selectedComplaint = intent.complaint, replyText = "") }
            }
            is ComplaintsIntent.ReplyTextChanged -> {
                _state.update { it.copy(replyText = intent.text) }
            }
            ComplaintsIntent.DismissReply -> {
                _state.update { it.copy(showReplyDialog = false, isSuccessInDialog = false, replyText = "") }
            }
            ComplaintsIntent.SubmitReply -> submitReply()
            else -> {}
        }
    }

    private fun loadComplaints() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            delay(1000)
            
            getComplaintsUseCase().collect { uiState ->
                when (uiState) {
                    is UiState.Success -> {
                        val items = if (uiState.data.items.isEmpty()) getDummyComplaints() else uiState.data.items
                        _state.update { it.copy(complaints = items, isLoading = false) }
                    }
                    is UiState.Error -> {
                        _state.update { it.copy(complaints = getDummyComplaints(), isLoading = false) }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun submitReply() {
        viewModelScope.launch {
            val currentComplaint = _state.value.selectedComplaint ?: return@launch
            if (_state.value.replyText.isBlank()) return@launch
            
            _state.update { it.copy(isActionLoading = true) }
            delay(1500) // محاكاة عملية الإرسال
            
            _state.update { it.copy(isActionLoading = false, isSuccessInDialog = true) }
            
            delay(1500) // إبقاء رسالة النجاح قليلاً
            
            // محاكاة الحذف من القائمة بعد الرد بنجاح
            _state.update { currentState ->
                currentState.copy(
                    showReplyDialog = false,
                    isSuccessInDialog = false,
                    complaints = currentState.complaints.filter { it.id != currentComplaint.id }
                )
            }
        }
    }

    private fun loadComplaintDetail(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getComplaintByIdUseCase(id).collect { uiState ->
                if (uiState is UiState.Success) {
                    _state.update { it.copy(selectedComplaint = uiState.data, isLoading = false) }
                }
            }
        }
    }

    private fun submitComplaint(content: String, type: String) {
        viewModelScope.launch {
            _state.update { it.copy(isActionLoading = true) }
            val result = createComplaintUseCase(content, type)
            if (result.isSuccess) {
                _effect.send(ComplaintsEffect.ShowMessage("تم إرسال طلبك بنجاح"))
                loadComplaints()
            } else {
                _effect.send(ComplaintsEffect.ShowMessage("تم الإرسال (حالة تجريبية)"))
            }
            _state.update { it.copy(isActionLoading = false) }
        }
    }

    private fun getDummyComplaints() = listOf(
        Complaint("comp1", "suggestion", "أقترح إضافة المزيد من الألعاب الحركية في ساحة الروضة الخارجية لزيادة نشاط الأطفال.", "p1", "سهيل مهند", ComplaintStatus.RESOLVED, "شكرًا لمقترحك الرائع، تم البدء في تركيب معدات جديدة في الساحة.", "2024-04-15"),
        Complaint("comp2", "complaint", "يرجى التأكد من تشغيل التكييف في حافلة رقم 5 صباحاً.", "p1", "سهيل مهند", ComplaintStatus.PENDING, null, "2024-04-19")
    )
}
