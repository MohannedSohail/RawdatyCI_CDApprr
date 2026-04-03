package org.mohanned.rawdatyci_cdapp.domain.model


// ── Enums ─────────────────────────────────────────────
enum class UserRole { SUPER_ADMIN, ADMIN, TEACHER, PARENT }
enum class AttendanceStatus { PRESENT, ABSENT, LATE, EXCUSED }
enum class GameType { LETTERS, NUMBERS, COLORS }
enum class ComplaintStatus { PENDING, IN_REVIEW, RESOLVED }

// ── Auth ──────────────────────────────────────────────
data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int,
)

data class LoggedUser(
    val id: Int,
    val name: String,
    val email: String,
    val role: UserRole,
    val avatarUrl: String?,
    val tenantSlug: String?,
)

// ── User ──────────────────────────────────────────────
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String?,
    val role: UserRole,
    val avatarUrl: String?,
    val isActive: Boolean,
    val classId: Int?,
    val className: String?,
    val createdAt: String,
)

// ── Classroom ─────────────────────────────────────────
data class Classroom(
    val id: Int,
    val name: String,
    val description: String?,
    val teacherId: Int?,
    val teacherName: String?,
    val childrenCount: Int,
    val capacity: Int?,
    val academicYear: String,
    val createdAt: String,
)

// ── Child ─────────────────────────────────────────────
data class Child(
    val id: Int,
    val fullName: String,
    val dateOfBirth: String?,
    val gender: String,
    val photoUrl: String?,
    val classId: Int,
    val className: String,
    val parentId: Int?,
    val parentName: String?,
    val parentPhone: String?,
    val enrollmentDate: String,
    val stars: Int,
    val notes: String?,
    val allergies: List<String> = emptyList(),
)

// ── Attendance ────────────────────────────────────────
data class AttendanceRecord(
    val id: Int,
    val childId: Int,
    val childName: String,
    val childPhoto: String?,
    val status: AttendanceStatus,
    val notes: String?,
    val date: String,
)

data class AttendanceSummary(
    val date: String,
    val classId: Int,
    val total: Int,
    val present: Int,
    val absent: Int,
    val late: Int,
    val presentPct: Float,
    val records: List<AttendanceRecord> = emptyList(),
)

// ── News ──────────────────────────────────────────────
data class News(
    val id: Int,
    val title: String,
    val body: String,
    val imageUrl: String?,
    val isPinned: Boolean,
    val authorName: String,
    val createdAt: String,
)

// ── Complaint ─────────────────────────────────────────
data class Complaint(
    val id: Int,
    val title: String,
    val body: String,
    val parentId: Int,
    val parentName: String,
    val status: ComplaintStatus,
    val adminReply: String?,
    val createdAt: String,
)

// ── Notification ──────────────────────────────────────
data class AppNotification(
    val id: Int,
    val title: String,
    val body: String,
    val type: String,
    val isRead: Boolean,
    val createdAt: String,
)

// ── Conversation ──────────────────────────────────────
data class Conversation(
    val id: Int,
    val participantId: Int,
    val participantName: String,
    val participantAvatar: String?,
    val childName: String?,
    val lastMessage: String?,
    val lastMessageAt: String?,
    val unreadCount: Int,
    val isOnline: Boolean = false,
)

// ── Message ───────────────────────────────────────────
data class Message(
    val id: Int,
    val conversationId: Int,
    val senderId: Int,
    val senderName: String,
    val content: String,
    val imageUrl: String?,
    val isRead: Boolean,
    val sentAt: String,
)

// ── Game ──────────────────────────────────────────────
data class GameQuestion(
    val id: Int,
    val gameType: GameType,
    val level: Int,
    val questionText: String,
    val questionImage: String?,
    val correctAnswer: String,
    val options: List<String>,
    val audioUrl: String?,
)

data class GameResult(
    val id: Int,
    val childId: Int,
    val gameType: GameType,
    val score: Int,
    val totalQuestions: Int,
    val stars: Int,
    val durationSeconds: Int,
    val playedAt: String,
)

// ── Settings ──────────────────────────────────────────
data class KindergartenSettings(
    val name: String,
    val logo: String?,
    val phone: String?,
    val email: String?,
    val address: String?,
    val about: String?,
    val primaryColor: String,
)

// ── Pagination ────────────────────────────────────────
data class PageMeta(
    val page: Int,
    val perPage: Int,
    val total: Int,
    val lastPage: Int,
)

data class PaginatedResult<T>(
    val data: List<T>,
    val meta: PageMeta,
)