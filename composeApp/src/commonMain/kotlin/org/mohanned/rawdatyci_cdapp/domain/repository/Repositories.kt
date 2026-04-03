package org.mohanned.rawdatyci_cdapp.domain.repository

import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.model.AppNotification
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceRecord
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceSummary
import org.mohanned.rawdatyci_cdapp.domain.model.AuthTokens
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.domain.model.Classroom
import org.mohanned.rawdatyci_cdapp.domain.model.Complaint
import org.mohanned.rawdatyci_cdapp.domain.model.Conversation
import org.mohanned.rawdatyci_cdapp.domain.model.GameQuestion
import org.mohanned.rawdatyci_cdapp.domain.model.LoggedUser
import org.mohanned.rawdatyci_cdapp.domain.model.Message
import org.mohanned.rawdatyci_cdapp.domain.model.News
import org.mohanned.rawdatyci_cdapp.domain.model.PaginatedResult
import org.mohanned.rawdatyci_cdapp.domain.model.User

// ── Auth ──────────────────────────────────────────────
interface AuthRepository {
    suspend fun login(
        identifier: String,
        password: String,
    ): ApiResponse<Pair<LoggedUser, AuthTokens>>

    suspend fun logout(): ApiResponse<Unit>

    suspend fun forgotPassword(
        email: String,
    ): ApiResponse<Unit>

    suspend fun verifyOtp(
        email: String,
        otp: String,
    ): ApiResponse<Unit>

    suspend fun resetPassword(
        email: String,
        otp: String,
        newPassword: String,
        confirmPassword: String,
    ): ApiResponse<Unit>
}

// ── Users ─────────────────────────────────────────────
interface UsersRepository {
    suspend fun getUsers(
        role: String? = null,
        classId: Int? = null,
        search: String? = null,
        page: Int = 1,
    ): ApiResponse<PaginatedResult<User>>

    suspend fun getUser(id: Int): ApiResponse<User>

    suspend fun createUser(
        name: String,
        email: String,
        password: String,
        role: String,
        phone: String?,
        classId: Int?,
    ): ApiResponse<User>

    suspend fun updateUser(
        id: Int,
        name: String?,
        phone: String?,
        isActive: Boolean?,
    ): ApiResponse<User>

    suspend fun deleteUser(id: Int): ApiResponse<Unit>

    suspend fun getProfile(): ApiResponse<User>

    suspend fun updateProfile(
        name: String,
        phone: String?,
    ): ApiResponse<User>

    suspend fun changePassword(
        current: String,
        newPass: String,
        confirm: String,
    ): ApiResponse<Unit>
}

// ── Classes ───────────────────────────────────────────
interface ClassesRepository {
    suspend fun getClasses(
        search: String? = null,
        page: Int = 1,
    ): ApiResponse<PaginatedResult<Classroom>>

    suspend fun getClass(id: Int): ApiResponse<Classroom>

    suspend fun createClass(
        name: String,
        teacherId: Int?,
        capacity: Int?,
    ): ApiResponse<Classroom>

    suspend fun updateClass(
        id: Int,
        name: String?,
        teacherId: Int?,
    ): ApiResponse<Classroom>

    suspend fun deleteClass(id: Int): ApiResponse<Unit>

    suspend fun getChildrenByClass(
        classId: Int,
        page: Int = 1,
    ): ApiResponse<PaginatedResult<Child>>

    suspend fun getMyChildren(): ApiResponse<PaginatedResult<Child>>
}

// ── Attendance ────────────────────────────────────────
interface AttendanceRepository {
    suspend fun createAttendance(
        classId: Int,
        date: String,
        records: List<Pair<Int, String>>,
    ): ApiResponse<AttendanceSummary>

    suspend fun getClassAttendance(
        classId: Int,
        date: String?,
        page: Int = 1,
    ): ApiResponse<PaginatedResult<AttendanceSummary>>

    suspend fun getChildAttendance(
        childId: Int,
        page: Int = 1,
    ): ApiResponse<PaginatedResult<AttendanceRecord>>
}

// ── Chat ──────────────────────────────────────────────
interface ChatRepository {
    suspend fun getConversations(
        page: Int = 1,
    ): ApiResponse<PaginatedResult<Conversation>>

    suspend fun getMessages(
        conversationId: Int,
        page: Int = 1,
    ): ApiResponse<PaginatedResult<Message>>

    suspend fun sendMessage(
        conversationId: Int,
        content: String,
    ): ApiResponse<Message>

    suspend fun startConversation(
        participantId: Int,
        childId: Int?,
    ): ApiResponse<Conversation>
}

// ── News ──────────────────────────────────────────────
interface NewsRepository {
    suspend fun getNews(
        search: String? = null,
        page: Int = 1,
    ): ApiResponse<PaginatedResult<News>>

    suspend fun getNewsItem(id: Int): ApiResponse<News>

    suspend fun createNews(
        title: String,
        body: String,
        isPinned: Boolean,
    ): ApiResponse<News>

    suspend fun updateNews(
        id: Int,
        title: String?,
        body: String?,
    ): ApiResponse<News>

    suspend fun deleteNews(id: Int): ApiResponse<Unit>
}

// ── Complaints ────────────────────────────────────────
interface ComplaintsRepository {
    suspend fun getComplaints(
        status: String?,
        page: Int = 1,
    ): ApiResponse<PaginatedResult<Complaint>>

    suspend fun getComplaint(id: Int): ApiResponse<Complaint>

    suspend fun createComplaint(
        title: String,
        body: String,
    ): ApiResponse<Complaint>

    suspend fun replyToComplaint(
        id: Int,
        reply: String,
        status: String,
    ): ApiResponse<Complaint>
}

// ── Notifications ─────────────────────────────────────
interface NotificationsRepository {
    suspend fun getNotifications(
        page: Int = 1,
    ): ApiResponse<PaginatedResult<AppNotification>>

    suspend fun markRead(id: Int): ApiResponse<Unit>

    suspend fun markAllRead(): ApiResponse<Unit>

    suspend fun sendNotification(
        title: String,
        body: String,
        target: String,
        classId: Int?,
    ): ApiResponse<Unit>
}

// ── Games ─────────────────────────────────────────────
interface GamesRepository {
    suspend fun getQuestions(
        gameType: String,
        level: Int = 1,
    ): ApiResponse<List<GameQuestion>>

    suspend fun saveResult(
        childId: Int,
        gameType: String,
        score: Int,
        totalQuestions: Int,
        durationSeconds: Int,
    ): ApiResponse<Unit>
}