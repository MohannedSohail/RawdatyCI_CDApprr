package org.mohanned.rawdatyci_cdapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Auth ──────────────────────────────────────────────
@Serializable
data class AuthResponseDto(
    val user: UserDto,
    @SerialName("access_token")  val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_in")    val expiresIn: Int,
)

@Serializable
data class BrandingDto(
    val name: String,
    @SerialName("logo_url")      val logoUrl: String? = null,
    @SerialName("primary_color") val primaryColor: String = "#1E4C6F",
    @SerialName("tenant_slug")   val tenantSlug: String = "",
)

// ── User ──────────────────────────────────────────────
@Serializable
data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String? = null,
    val role: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("is_active")  val isActive: Boolean = true,
    @SerialName("class_id")   val classId: Int? = null,
    @SerialName("class_name") val className: String? = null,
    @SerialName("created_at") val createdAt: String = "",
)

// ── Classroom ─────────────────────────────────────────
@Serializable
data class ClassDto(
    val id: Int,
    val name: String,
    val description: String? = null,
    @SerialName("teacher_id")     val teacherId: Int? = null,
    @SerialName("teacher_name")   val teacherName: String? = null,
    @SerialName("children_count") val childrenCount: Int = 0,
    val capacity: Int? = null,
    @SerialName("academic_year")  val academicYear: String = "",
    @SerialName("created_at")     val createdAt: String = "",
)

// ── Child ─────────────────────────────────────────────
@Serializable
data class ChildDto(
    val id: Int,
    @SerialName("full_name")       val fullName: String,
    @SerialName("date_of_birth")   val dateOfBirth: String? = null,
    val gender: String = "",
    @SerialName("photo_url")       val photoUrl: String? = null,
    @SerialName("class_id")        val classId: Int,
    @SerialName("class_name")      val className: String = "",
    @SerialName("parent_id")       val parentId: Int? = null,
    @SerialName("parent_name")     val parentName: String? = null,
    @SerialName("parent_phone")    val parentPhone: String? = null,
    @SerialName("enrollment_date") val enrollmentDate: String = "",
    val stars: Int = 0,
    val notes: String? = null,
    val allergies: List<String> = emptyList(),
)

// ── Attendance ────────────────────────────────────────
@Serializable
data class AttendanceRecordDto(
    val id: Int,
    @SerialName("child_id")    val childId: Int,
    @SerialName("child_name")  val childName: String = "",
    @SerialName("child_photo") val childPhoto: String? = null,
    val status: String,
    val notes: String? = null,
    val date: String = "",
)

@Serializable
data class AttendanceSummaryDto(
    val date: String,
    @SerialName("class_id")    val classId: Int,
    val total: Int,
    val present: Int,
    val absent: Int,
    val late: Int,
    @SerialName("present_pct") val presentPct: Float = 0f,
)

// ── News ──────────────────────────────────────────────
@Serializable
data class NewsDto(
    val id: Int,
    val title: String,
    val body: String = "",
    @SerialName("image_url")   val imageUrl: String? = null,
    @SerialName("is_pinned")   val isPinned: Boolean = false,
    @SerialName("author_name") val authorName: String = "",
    @SerialName("created_at")  val createdAt: String,
)

// ── Complaint ─────────────────────────────────────────
@Serializable
data class ComplaintDto(
    val id: Int,
    val title: String,
    val body: String,
    @SerialName("parent_id")   val parentId: Int,
    @SerialName("parent_name") val parentName: String = "",
    val status: String = "pending",
    @SerialName("admin_reply") val adminReply: String? = null,
    @SerialName("created_at")  val createdAt: String,
)

// ── Notification ──────────────────────────────────────
@Serializable
data class NotificationDto(
    val id: Int,
    val title: String,
    val body: String,
    val type: String = "general",
    @SerialName("is_read")    val isRead: Boolean = false,
    @SerialName("created_at") val createdAt: String,
)

// ── Chat ──────────────────────────────────────────────
@Serializable
data class ConversationDto(
    val id: Int,
    @SerialName("participant_id")     val participantId: Int,
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
    val id: Int,
    @SerialName("conversation_id") val conversationId: Int,
    @SerialName("sender_id")       val senderId: Int,
    @SerialName("sender_name")     val senderName: String = "",
    val content: String,
    @SerialName("image_url")       val imageUrl: String? = null,
    @SerialName("is_read")         val isRead: Boolean = false,
    @SerialName("sent_at")         val sentAt: String,
)

// ── Games ─────────────────────────────────────────────
@Serializable
data class GameQuestionDto(
    val id: Int,
    val type: String,
    val level: Int = 1,
    @SerialName("question_text")  val questionText: String,
    @SerialName("question_image") val questionImage: String? = null,
    @SerialName("correct_answer") val correctAnswer: String,
    val options: List<String>,
    @SerialName("audio_url")      val audioUrl: String? = null,
)

// ── Generic ───────────────────────────────────────────
@Serializable
data class BaseResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val error: String? = null,
    val errors: Map<String, List<String>>? = null,
    val meta: ApiMetaDto? = null,
)

@Serializable
data class ApiMessageDto(
    val message: String,
    val success: Boolean = true,
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