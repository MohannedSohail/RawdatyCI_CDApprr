package org.mohanned.rawdatyci_cdapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Base ──────────────────────────────────────────────
@Serializable
data class BaseResponse<T>(
    val success: Boolean = true,
    val message: String? = null,
    val data: T? = null,
    val error: String? = null,
    val errors: Map<String, List<String>>? = null,
)

@Serializable
data class ApiListDto<T>(
    val data: List<T>,
    val meta: ApiMetaDto? = null,
)

@Serializable
data class ApiMetaDto(
    val page: Int = 1,
    @SerialName("per_page")  val perPage: Int = 15,
    val total: Int = 0,
    @SerialName("last_page") val lastPage: Int = 1,
)

@Serializable
data class ApiMessageDto(
    val message: String = "",
    val success: Boolean = true,
)

// ── Auth ──────────────────────────────────────────────
@Serializable
data class AuthResponseDto(
    @SerialName("access_token")    val accessToken: String,
    @SerialName("refresh_token")   val refreshToken: String,
    @SerialName("expires_in")      val expiresIn: Int,
    val user: AuthUserDto,
    @SerialName("tenant_branding") val tenantBranding: BrandingDto? = null,
)

@Serializable
data class AuthUserDto(
    val id: String,
    val name: String,
    val email: String = "",
    val role: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
)

@Serializable
data class BrandingDto(
    @SerialName("app_name")        val appName: String,
    @SerialName("logo_url")        val logoUrl: String? = null,
    @SerialName("primary_color")   val primaryColor: String = "#1E4C6F",
    @SerialName("show_powered_by") val showPoweredBy: Boolean = true,
    @SerialName("tenant_slug")     val tenantSlug: String = "",
)

@Serializable
data class VerifyOtpResponseDto(
    @SerialName("reset_token") val resetToken: String,
)

// ── User ──────────────────────────────────────────────
@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String = "",
    val phone: String? = null,
    val role: String,
    @SerialName("avatar_url")  val avatarUrl: String? = null,
    @SerialName("is_active")   val isActive: Boolean = true,
    @SerialName("class_id")    val classId: String? = null,
    @SerialName("class_name")  val className: String? = null,
    @SerialName("created_at")  val createdAt: String = "",
)

@Serializable
data class CreateUserRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String,
    val phone: String? = null,
    @SerialName("class_id") val classId: String? = null
)

@Serializable
data class UpdateUserRequest(
    val name: String? = null,
    val phone: String? = null,
    @SerialName("is_active") val isActive: Boolean? = null
)

// ── Class ─────────────────────────────────────────────
@Serializable
data class ClassDto(
    val id: String,
    val name: String,
    val description: String? = null,
    @SerialName("teacher_id")     val teacherId: String? = null,
    @SerialName("teacher_name")   val teacherName: String? = null,
    @SerialName("children_count") val childrenCount: Int = 0,
    val capacity: Int? = null,
    @SerialName("academic_year")  val academicYear: String = "",
    @SerialName("is_active")      val isActive: Boolean = true,
    @SerialName("created_at")     val createdAt: String = "",
)

@Serializable
data class CreateClassRequest(
    val name: String,
    @SerialName("teacher_id") val teacherId: String?,
    val capacity: Int?
)

@Serializable
data class UpdateClassRequest(
    val name: String? = null,
    @SerialName("teacher_id") val teacherId: String? = null,
    @SerialName("is_active") val isActive: Boolean? = null
)

// ── Child ─────────────────────────────────────────────
@Serializable
data class ChildDto(
    val id: String,
    val name: String = "",
    @SerialName("full_name")       val fullName: String = "",
    @SerialName("birth_date")      val birthDate: String? = null,
    @SerialName("date_of_birth")   val dateOfBirth: String? = null,
    val gender: String = "",
    @SerialName("photo_url")       val photoUrl: String? = null,
    @SerialName("class_id")        val classId: String = "",
    @SerialName("class_name")      val className: String = "",
    @SerialName("parent_id")       val parentId: String? = null,
    @SerialName("parent_name")     val parentName: String? = null,
    @SerialName("parent_phone")    val parentPhone: String? = null,
    @SerialName("enrollment_date") val enrollmentDate: String = "",
    val stars: Int = 0,
    val notes: String? = null,
    val allergies: List<String> = emptyList(),
) {
    val displayName: String get() = fullName.ifEmpty { name }
    val dob: String? get() = dateOfBirth ?: birthDate
}

// ── Attendance ────────────────────────────────────────
@Serializable
data class AttendanceRecordDto(
    val id: String,
    @SerialName("child_id")    val childId: String,
    @SerialName("child_name")  val childName: String = "",
    @SerialName("child_photo") val childPhoto: String? = null,
    val status: String,
    val notes: String? = null,
    val date: String = "",
)

@Serializable
data class AttendanceSummaryDto(
    val date: String = "",
    @SerialName("class_id")    val classId: String = "",
    val total: Int = 0,
    val present: Int = 0,
    val absent: Int = 0,
    val late: Int = 0,
    @SerialName("present_pct") val presentPct: Float = 0f,
    val records: List<AttendanceRecordDto> = emptyList(),
)

@Serializable
data class AttendanceRecordRequest(
    @SerialName("child_id") val childId: String,
    val status: String,
    val notes: String? = null,
)

@Serializable
data class CreateAttendanceRequest(
    @SerialName("class_id") val classId: String,
    val date: String,
    val records: List<AttendanceRecordRequest>,
)

// ── News ──────────────────────────────────────────────
@Serializable
data class NewsDto(
    val id: String,
    val title: String,
    val content: String = "",
    val body: String = "",
    @SerialName("image_url")    val imageUrl: String? = null,
    @SerialName("is_visible")   val isVisible: Boolean = true,
    @SerialName("is_pinned")    val isPinned: Boolean = false,
    val type: String = "news",
    @SerialName("author_name")  val authorName: String = "",
    @SerialName("created_at")   val createdAt: String = "",
) {
    val displayContent: String get() = content.ifEmpty { body }
}

@Serializable
data class CreateNewsRequest(
    val title: String,
    val content: String,
    val type: String,
    @SerialName("is_visible") val isVisible: Boolean
)

@Serializable
data class UpdateNewsRequest(
    val title: String? = null,
    val content: String? = null,
    val type: String? = null,
    @SerialName("is_visible") val isVisible: Boolean? = null
)

// ── Complaint ─────────────────────────────────────────
@Serializable
data class ComplaintDto(
    val id: String,
    val type: String = "complaint",
    val content: String = "",
    val body: String = "",
    @SerialName("parent_id")   val parentId: String = "",
    @SerialName("parent_name") val parentName: String = "",
    val status: String = "pending",
    val reply: String? = null,
    @SerialName("admin_reply") val adminReply: String? = null,
    @SerialName("created_at")  val createdAt: String = "",
) {
    val displayContent: String get() = content.ifEmpty { body }
    val displayReply: String? get() = reply ?: adminReply
}

// ── Notification ──────────────────────────────────────
@Serializable
data class NotificationDto(
    val id: String,
    val title: String,
    val body: String,
    val type: String = "general",
    @SerialName("is_read")    val isRead: Boolean = false,
    @SerialName("created_at") val createdAt: String = "",
)

// ── Chat ──────────────────────────────────────────────
@Serializable
data class ConversationDto(
    val id: String,
    @SerialName("participant_id")     val participantId: String,
    @SerialName("participant_name")   val participantName: String,
    @SerialName("participant_avatar") val participantAvatar: String? = null,
    @SerialName("child_name")         val childName: String? = null,
    @SerialName("last_message")       val lastMessage: String? = null,
    @SerialName("last_message_at")    val lastMessageAt: String? = null,
    @SerialName("unread_count")       val unreadCount: Int = 0,
    @SerialName("is_online")          val isOnline: Boolean = false,
)

@Serializable
data class MessageDto(
    val id: String,
    @SerialName("conversation_id") val conversationId: String,
    @SerialName("sender_id")       val senderId: String,
    @SerialName("sender_name")     val senderName: String = "",
    val content: String,
    @SerialName("image_url")       val imageUrl: String? = null,
    @SerialName("is_read")         val isRead: Boolean = false,
    @SerialName("sent_at")         val sentAt: String = "",
)

// ── Games ─────────────────────────────────────────────
@Serializable
data class GameQuestionDto(
    val id: String,
    val type: String = "",
    @SerialName("game_type")      val gameType: String = "",
    val level: Int = 1,
    @SerialName("question_text")  val questionText: String = "",
    @SerialName("question_image") val questionImage: String? = null,
    @SerialName("correct_answer") val correctAnswer: String = "",
    val options: List<String> = emptyList(),
    @SerialName("audio_url")      val audioUrl: String? = null,
    @SerialName("is_active")      val isActive: Boolean = true,
) {
    val resolvedType: String get() = gameType.ifEmpty { type }
}

@Serializable
data class GameAnswerDto(
    @SerialName("question_id")     val questionId: String,
    @SerialName("selected_answer") val selectedAnswer: String,
)

@Serializable
data class GameResultRequest(
    @SerialName("child_id")  val childId: String,
    @SerialName("game_type") val gameType: String,
    val score: Int,
    val answers: List<GameAnswerDto> = emptyList(),
)

// ── Settings ──────────────────────────────────────────
@Serializable
data class SettingsDto(
    @SerialName("kindergarten_name")    val kindergartenName: String = "",
    @SerialName("kindergarten_address") val address: String? = null,
    @SerialName("kindergarten_phone")   val phone: String? = null,
    @SerialName("social_whatsapp")      val whatsapp: String? = null,
    @SerialName("social_twitter")       val twitter: String? = null,
    @SerialName("social_instagram")     val instagram: String? = null,
    @SerialName("map_lat")              val mapLat: Double? = null,
    @SerialName("map_lng")              val mapLng: Double? = null,
    @SerialName("academic_year")        val academicYear: String = "",
)
