package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.data.remote.dto.AttendanceRecordRequest
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceRecord
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceStatus
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceSummary
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.domain.usecase.attendance.*
import org.mohanned.rawdatyci_cdapp.domain.usecase.child.GetChildrenByClassUseCase
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.Clock

data class AttendanceState(
    val children: List<Child> = emptyList(),
    val attendanceMap: Map<String, AttendanceStatus> = emptyMap(),
    val attendanceRecords: List<AttendanceRecord> = emptyList(),
    val attendanceRate: Float = 0f,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)

sealed class AttendanceIntent {
    data class LoadChildren(val classId: String) : AttendanceIntent()
    data class UpdateStatus(val childId: String, val status: AttendanceStatus) : AttendanceIntent()
    object SelectAll : AttendanceIntent()
    data class Save(val classId: String) : AttendanceIntent()
    data class LoadChildAttendance(val childId: String) : AttendanceIntent()
    data class LoadMonthlyReport(val month: String, val classId: String?) : AttendanceIntent()
}

sealed class AttendanceEffect {
    data class ShowMessage(val message: String) : AttendanceEffect()
}

class AttendanceViewModel(
    private val getChildrenByClassUseCase: GetChildrenByClassUseCase,
    private val createAttendanceUseCase: CreateAttendanceUseCase,
    private val getChildAttendanceUseCase: GetChildAttendanceUseCase,
    private val getMonthlyAttendanceReportUseCase: GetMonthlyAttendanceReportUseCase,
    private val updateAttendanceRecordUseCase: UpdateAttendanceRecordUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AttendanceState())
    val state = _state.asStateFlow()

    private val _effect = Channel<AttendanceEffect>()
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: AttendanceIntent) {
        when (intent) {
            is AttendanceIntent.LoadChildren -> loadChildren(intent.classId)
            is AttendanceIntent.UpdateStatus -> updateStatus(intent.childId, intent.status)
            AttendanceIntent.SelectAll -> selectAll()
            is AttendanceIntent.Save -> saveAttendance(intent.classId)
            is AttendanceIntent.LoadChildAttendance -> loadChildAttendance(intent.childId)
            is AttendanceIntent.LoadMonthlyReport -> loadMonthlyReport(intent.month, intent.classId)
        }
    }

    private fun loadChildren(classId: String) {
        viewModelScope.launch {
            getChildrenByClassUseCase(classId).collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> _state.update { it.copy(isLoading = true) }
                    is UiState.Success -> {
                        val children = uiState.data.items
                        _state.update { it.copy(
                            children = children,
                            attendanceMap = children.associate { it.id to AttendanceStatus.PRESENT },
                            isLoading = false
                        ) }
                    }
                    is UiState.Error -> _state.update { it.copy(isLoading = false, error = uiState.message) }
                }
            }
        }
    }

    private fun updateStatus(childId: String, status: AttendanceStatus) {
        _state.update { it.copy(attendanceMap = it.attendanceMap + (childId to status)) }
    }

    private fun selectAll() {
        _state.update { it.copy(attendanceMap = it.children.associate { it.id to AttendanceStatus.PRESENT }) }
    }

    private fun saveAttendance(classId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val records = _state.value.attendanceMap.map { (id, status) ->
                AttendanceRecordRequest(id, status.name.lowercase())
            }
            // Use current date for attendance
            val currentDate = kotlinx.datetime.Clock.System.now()
                .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
                .date.toString()
            
            val result = createAttendanceUseCase(classId, currentDate, records)
            if (result.isSuccess) {
                _effect.send(AttendanceEffect.ShowMessage("تم حفظ الحضور بنجاح"))
            } else {
                _effect.send(AttendanceEffect.ShowMessage(result.exceptionOrNull()?.message ?: "فشل الحفظ"))
            }
            _state.update { it.copy(isSaving = false) }
        }
    }

    private fun loadChildAttendance(childId: String) {
        viewModelScope.launch {
            getChildAttendanceUseCase(childId).collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> _state.update { it.copy(isLoading = true) }
                    is UiState.Success -> _state.update { it.copy(attendanceRecords = uiState.data.items, isLoading = false) }
                    is UiState.Error -> _state.update { it.copy(isLoading = false, error = uiState.message) }
                }
            }
        }
    }

    private fun loadMonthlyReport(month: String, classId: String?) {
        viewModelScope.launch {
            getMonthlyAttendanceReportUseCase(month, classId).collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> _state.update { it.copy(isLoading = true) }
                    is UiState.Success -> _state.update { it.copy(attendanceRate = uiState.data.presentPct, isLoading = false) }
                    is UiState.Error -> _state.update { it.copy(isLoading = false, error = uiState.message) }
                }
            }
        }
    }
}
