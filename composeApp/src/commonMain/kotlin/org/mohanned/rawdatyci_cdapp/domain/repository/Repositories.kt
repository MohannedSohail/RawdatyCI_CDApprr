package org.mohanned.rawdatyci_cdapp.domain.repository

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.data.remote.dto.ApiMessageDto
import org.mohanned.rawdatyci_cdapp.domain.model.AppNotification
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceRecord
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceSummary
import org.mohanned.rawdatyci_cdapp.domain.model.AuthTokens
import org.mohanned.rawdatyci_cdapp.domain.model.Branding
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.domain.model.Classroom
import org.mohanned.rawdatyci_cdapp.domain.model.Complaint
import org.mohanned.rawdatyci_cdapp.domain.model.Conversation
import org.mohanned.rawdatyci_cdapp.domain.model.GameAnswer
import org.mohanned.rawdatyci_cdapp.domain.model.GameQuestion
import org.mohanned.rawdatyci_cdapp.domain.model.KindergartenSettings
import org.mohanned.rawdatyci_cdapp.domain.model.LoggedUser
import org.mohanned.rawdatyci_cdapp.domain.model.Message
import org.mohanned.rawdatyci_cdapp.domain.model.News
import org.mohanned.rawdatyci_cdapp.domain.model.PaginatedResult
import org.mohanned.rawdatyci_cdapp.domain.model.User
import org.mohanned.rawdatyci_cdapp.data.remote.dto.AttendanceRecordRequest

interface AuthRepository {
    suspend fun getBranding(): ApiResponse<Branding>
    suspend fun login(email: String, password: String): ApiResponse<Pair<LoggedUser, AuthTokens>>
    suspend fun logout(): ApiResponse<Unit>
    suspend fun forgotPassword(email: String): ApiResponse<ApiMessageDto>
    suspend fun verifyOtp(email: String, otp: String): ApiResponse<String>
    suspend fun resetPassword(
        resetToken: String,
        newPassword: String,
        confirm: String
    ): ApiResponse<ApiMessageDto>

    suspend fun getLoggedUser(): LoggedUser?
}

interface UsersRepository {
    suspend fun getUsers(
        role: String? = null,
        classId: String? = null,
        search: String? = null,
        page: Int = 1
    ): ApiResponse<PaginatedResult<User>>

    suspend fun getUser(id: String): ApiResponse<User>
    suspend fun createUser(
        name: String,
        email: String,
        password: String,
        role: String,
        phone: String? = null,
        classId: String? = null
    ): ApiResponse<User>

    suspend fun updateUser(
        id: String,
        name: String? = null,
        phone: String? = null,
        isActive: Boolean? = null,
        classId: String? = null
    ): ApiResponse<User>

    suspend fun deleteUser(id: String): ApiResponse<Unit>
    suspend fun getProfile(): ApiResponse<User>
    suspend fun updateProfile(
        name: String,
        phone: String? = null,
        address: String? = null
    ): ApiResponse<User>

    suspend fun changePassword(current: String, newPass: String, confirm: String): ApiResponse<Unit>
    suspend fun saveFcmToken(token: String, deviceType: String = "android"): ApiResponse<Unit>
}

interface ClassesRepository {
    suspend fun getClasses(
        search: String? = null,
        page: Int = 1
    ): ApiResponse<PaginatedResult<Classroom>>

    suspend fun getClass(id: String): ApiResponse<Classroom>
    suspend fun createClass(
        name: String,
        description: String? = null,
        teacherId: String? = null,
        capacity: Int? = null
    ): ApiResponse<Classroom>

    suspend fun updateClass(
        id: String,
        name: String? = null,
        teacherId: String? = null,
        isActive: Boolean? = null
    ): ApiResponse<Classroom>

    suspend fun deleteClass(id: String): ApiResponse<Unit>
}

interface ChildrenRepository {
    suspend fun getChildrenByClass(
        classId: String,
        page: Int = 1
    ): ApiResponse<PaginatedResult<Child>>

    suspend fun getMyChildren(page: Int = 1): ApiResponse<PaginatedResult<Child>>
    suspend fun createChild(
        name: String,
        parentId: String,
        classId: String? = null,
        birthDate: String? = null,
        gender: String
    ): ApiResponse<Child>

    suspend fun updateChild(
        id: String,
        classId: String? = null,
        notes: String? = null,
        rating: Int? = null
    ): ApiResponse<Child>
}

interface AttendanceRepository {
    suspend fun createAttendance(
        classId: String,
        date: String,
        records: List<AttendanceRecordRequest>
    ): ApiResponse<AttendanceSummary>

    suspend fun updateRecord(
        sessionId: String,
        childId: String,
        status: String,
        notes: String? = null
    ): ApiResponse<AttendanceRecord>

    suspend fun getClassAttendance(
        classId: String,
        fromDate: String? = null,
        toDate: String? = null,
        page: Int = 1
    ): ApiResponse<PaginatedResult<AttendanceSummary>>

    suspend fun getChildAttendance(
        childId: String,
        fromDate: String? = null,
        page: Int = 1
    ): ApiResponse<PaginatedResult<AttendanceRecord>>

    suspend fun getMonthlyReport(
        month: String,
        classId: String? = null
    ): ApiResponse<AttendanceSummary>
}

interface NewsRepository {
    suspend fun getNews(
        search: String? = null,
        type: String? = null,
        page: Int = 1
    ): ApiResponse<PaginatedResult<News>>

    suspend fun getSlider(): ApiResponse<List<News>>
    suspend fun getNewsItem(id: String): ApiResponse<News>
    suspend fun createNews(
        title: String,
        content: String,
        type: String = "news",
        isVisible: Boolean = true,
        imageUrl: String? = null,
        notifyUsers: Boolean = true
    ): ApiResponse<News>

    suspend fun updateNews(
        id: String,
        title: String? = null,
        content: String? = null,
        isVisible: Boolean? = null
    ): ApiResponse<News>

    suspend fun deleteNews(id: String): ApiResponse<Unit>
}

interface ComplaintsRepository {
    suspend fun getComplaints(
        status: String? = null,
        type: String? = null,
        page: Int = 1
    ): ApiResponse<PaginatedResult<Complaint>>

    suspend fun getComplaint(id: String): ApiResponse<Complaint>
    suspend fun createComplaint(content: String, type: String = "complaint"): ApiResponse<Complaint>
    suspend fun replyToComplaint(id: String, reply: String): ApiResponse<Complaint>
}

interface NotificationsRepository {
    suspend fun getNotifications(
        page: Int = 1,
        isRead: Boolean? = null
    ): ApiResponse<PaginatedResult<AppNotification>>

    suspend fun markRead(id: String): ApiResponse<Unit>
    suspend fun markAllRead(): ApiResponse<Unit>
    suspend fun sendNotification(
        title: String,
        body: String,
        target: String,
        classId: String? = null
    ): ApiResponse<Unit>

    suspend fun registerFcmToken(
        fcmToken: String,
        deviceType: String = "android"
    ): ApiResponse<Unit>
}

interface ChatRepository {
    suspend fun getConversations(page: Int = 1): ApiResponse<PaginatedResult<Conversation>>
    suspend fun getMessages(
        conversationId: String,
        page: Int = 1
    ): ApiResponse<PaginatedResult<Message>>

    suspend fun sendMessage(conversationId: String, content: String): ApiResponse<Message>
    suspend fun startConversation(
        participantId: String,
        childId: String? = null
    ): ApiResponse<Conversation>
}

interface GamesRepository {
    suspend fun getQuestions(
        gameType: String,
        childId: String? = null
    ): ApiResponse<List<GameQuestion>>

    suspend fun saveResult(
        childId: String,
        gameType: String,
        score: Int,
        answers: List<GameAnswer> = emptyList()
    ): ApiResponse<Unit>

    suspend fun getChildResults(
        childId: String,
        gameType: String? = null
    ): ApiResponse<List<GameQuestion>>
    
    suspend fun updateQuestion(
        id: String, 
        text: String? = null, 
        options: List<String>? = null, 
        correctAnswer: String? = null
    ): ApiResponse<GameQuestion>
}

interface SettingsRepository {
    suspend fun getSettings(): ApiResponse<KindergartenSettings>
    suspend fun updateSettings(settings: KindergartenSettings): ApiResponse<KindergartenSettings>
}
