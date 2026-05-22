package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.domain.model.Classroom
import org.mohanned.rawdatyci_cdapp.domain.repository.AuthRepository
import org.mohanned.rawdatyci_cdapp.domain.usecase.child.GetChildrenByClassUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.classroom.GetClassesUseCase

data class TeacherActivity(
    val title: String,
    val time: String,
    val type: String // "attendance", "message"
)

data class TeacherHomeState(
    val teacherName: String = "",
    val classes: List<Classroom> = emptyList(),
    val recentStudents: List<Child> = emptyList(),
    val activities: List<TeacherActivity> = emptyList(),
    val totalStudentsCount: Int = 0,
    val attendanceSummary: String = "0/0",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class TeacherHomeIntent {
    object Load : TeacherHomeIntent()
}

class TeacherHomeViewModel(
    private val authRepository: AuthRepository,
    private val getClassesUseCase: GetClassesUseCase,
    private val getChildrenByClassUseCase: GetChildrenByClassUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(TeacherHomeState())
    val state = _state.asStateFlow()

    fun onIntent(intent: TeacherHomeIntent) {
        when (intent) {
            TeacherHomeIntent.Load -> loadDashboard()
        }
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val user = authRepository.getLoggedUser()

            getClassesUseCase().collect { uiState ->
                when (uiState) {
                    is UiState.Success -> {
                        val classes = if (uiState.data.items.isEmpty()) getDummyClasses() else uiState.data.items
                        val totalStudents = classes.sumOf { it.childrenCount }
                        _state.update { it.copy(
                            teacherName = user?.name ?: "المعلمة",
                            classes = classes,
                            totalStudentsCount = totalStudents,
                            attendanceSummary = "${(totalStudents * 0.9).toInt()}/$totalStudents",
                            activities = getDummyActivities(),
                            isLoading = false
                        ) }
                        if (classes.isNotEmpty()) loadRecentStudents(classes.first().id)
                    }
                    is UiState.Error -> {
                        val dummyClasses = getDummyClasses()
                        _state.update { it.copy(
                            teacherName = user?.name ?: "المعلمة",
                            classes = dummyClasses,
                            totalStudentsCount = 27,
                            attendanceSummary = "24/27",
                            activities = getDummyActivities(),
                            isLoading = false
                        ) }
                        loadRecentStudents("c1")
                    }
                    else -> {}
                }
            }
        }
    }

    private fun loadRecentStudents(classId: String) {
        viewModelScope.launch {
            getChildrenByClassUseCase(classId).collect { uiState ->
                if (uiState is UiState.Success) {
                    val students = if (uiState.data.items.isEmpty()) getDummyStudents() else uiState.data.items
                    _state.update { it.copy(recentStudents = students) }
                } else if (uiState is UiState.Error) {
                    _state.update { it.copy(recentStudents = getDummyStudents()) }
                }
            }
        }
    }

    private fun getDummyClasses() = listOf(
        Classroom("c1", "فصل البراعم (أ)", "المستوى الأول", "t1", "سارة", 15, 20, "2024", true, "2024-04-21"),
        Classroom("c2", "فصل النجوم (ب)", "المستوى الثاني", "t1", "سارة", 12, 20, "2024", true, "2024-04-21")
    )

    private fun getDummyStudents() = listOf(
        Child("d1", "أحمد محمد علي", "فصل البراعم", "male", null, "c1", "2023-09-01", null, "2019-05-12", "p1", "محمد علي", 3, ""),
        Child("d2", "سارة خالد", "فصل البراعم", "female", null, "c1", "2023-09-01", null, "2019-08-20", "p2", "خالد محمود", 5, ""),
        Child("d3", "ياسين يوسف", "فصل البراعم", "male", null, "c1", "2023-09-01", null, "2019-02-15", "p3", "يوسف أحمد", 4, "")
    )

    private fun getDummyActivities() = listOf(
        TeacherActivity("تسجيل حضور طلاب 'البراعم'", "قبل ساعة", "attendance"),
        TeacherActivity("رسالة جديدة من والدة 'أحمد'", "قبل ساعتين", "message")
    )
}
