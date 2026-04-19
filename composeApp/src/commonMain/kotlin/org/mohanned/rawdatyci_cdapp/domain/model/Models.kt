package org.mohanned.rawdatyci_cdapp.domain.model

enum class UserRole { SUPER_ADMIN, ADMIN, TEACHER, PARENT }
enum class AttendanceStatus { PRESENT, ABSENT, LATE, EXCUSED }
enum class GameType { LETTERS, NUMBERS, COLORS }
enum class ComplaintStatus { PENDING, IN_REVIEW, RESOLVED }
enum class NewsType { NEWS, ANNOUNCEMENT, SLIDER }
enum class SyncStatus { SYNCING, SYNCED, PENDING }

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int,
)

data class LoggedUser(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val avatarUrl: String?,
    val tenantSlug: String,
)

data class Branding(
    val appName: String,
    val logoUrl: String?,
    val primaryColor: String,
    val showPoweredBy: Boolean,
)


data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String?,
    val role: UserRole,
    val avatarUrl: String?,
    val isActive: Boolean,
    val classId: String?,
    val className: String?,
    val createdAt: String,
)

data class Classroom(
    val id: String,
    val name: String,
    val description: String?,
    val teacherId: String?,
    val teacherName: String?,
    val childrenCount: Int,
    val capacity: Int?,
    val academicYear: String,
    val isActive: Boolean,
    val createdAt: String,
)

data class Child(
    val id: String,
    val fullName: String,
    val dateOfBirth: String?,
    val gender: String,
    val photoUrl: String?,
    val classId: String,
    val className: String,
    val parentId: String?,
    val parentName: String?,
    val parentPhone: String?,
    val enrollmentDate: String,
    val stars: Int,
    val notes: String?,
    val allergies: List<String> = emptyList(),
)

data class AttendanceRecord(
    val id: String,
    val childId: String,
    val childName: String,
    val childPhoto: String? = null,
    val status: AttendanceStatus,
    val notes: String?,
    val date: String = "",
)

data class AttendanceSummary(
    val date: String,
    val classId: String,
    val total: Int,
    val present: Int,
    val absent: Int,
    val late: Int,
    val presentPct: Float,
    val records: List<AttendanceRecord> = emptyList(),
)

data class News(
    val id: String,
    val title: String,
    val body: String,
    val imageUrl: String?,
    val isVisible: Boolean,
    val type: NewsType,
    val authorName: String,
    val createdAt: String,
    val syncStatus: SyncStatus = SyncStatus.SYNCED
)

data class Complaint(
    val id: String,
    val type: String,
    val content: String,
    val parentId: String,
    val parentName: String,
    val status: ComplaintStatus,
    val reply: String?,
    val createdAt: String,
    val syncStatus: SyncStatus = SyncStatus.SYNCED
)

data class AppNotification(
    val id: String,
    val title: String,
    val body: String,
    val type: String,
    val isRead: Boolean,
    val createdAt: String,
)

data class Conversation(
    val id: String,
    val participantId: String,
    val participantName: String,
    val participantAvatar: String?,
    val childName: String?,
    val lastMessage: String?,
    val lastMessageAt: String?,
    val unreadCount: Int,
    val isOnline: Boolean = false,
)

data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val imageUrl: String?,
    val isRead: Boolean,
    val sentAt: String,
    val syncStatus: SyncStatus = SyncStatus.SYNCED
)

data class GameQuestion(
    val id: String,
    val gameType: GameType,
    val level: Int,
    val questionText: String,
    val questionImage: String?,
    val correctAnswer: String,
    val options: List<String>,
    val audioUrl: String?,
)

data class GameAnswer(
    val questionId: String,
    val selectedAnswer: String,
)

data class KindergartenSettings(
    val kindergartenName: String,
    val address: String?,
    val phone: String?,
    val whatsapp: String?,
    val twitter: String?,
    val instagram: String?,
    val mapLat: Double?,
    val mapLng: Double?,
    val academicYear: String,
)

data class PageMeta(val page: Int, val perPage: Int, val total: Int, val lastPage: Int)
data class PaginatedResult<T>(
    val items: List<T>,
    val total: Int,
    val page: Int,
    val lastPage: Int,
    val hasMore: Boolean,
    val meta: PageMeta? = null
)
