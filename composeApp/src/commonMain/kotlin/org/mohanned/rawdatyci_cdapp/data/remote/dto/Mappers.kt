package org.mohanned.rawdatyci_cdapp.data.remote.dto

import org.mohanned.rawdatyci_cdapp.domain.model.AppNotification
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceRecord
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceStatus
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceSummary
import org.mohanned.rawdatyci_cdapp.domain.model.Branding
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.domain.model.Classroom
import org.mohanned.rawdatyci_cdapp.domain.model.Complaint
import org.mohanned.rawdatyci_cdapp.domain.model.ComplaintStatus
import org.mohanned.rawdatyci_cdapp.domain.model.Conversation
import org.mohanned.rawdatyci_cdapp.domain.model.GameQuestion
import org.mohanned.rawdatyci_cdapp.domain.model.GameType
import org.mohanned.rawdatyci_cdapp.domain.model.KindergartenSettings
import org.mohanned.rawdatyci_cdapp.domain.model.LoggedUser
import org.mohanned.rawdatyci_cdapp.domain.model.Message
import org.mohanned.rawdatyci_cdapp.domain.model.News
import org.mohanned.rawdatyci_cdapp.domain.model.NewsType
import org.mohanned.rawdatyci_cdapp.domain.model.PageMeta
import org.mohanned.rawdatyci_cdapp.domain.model.PaginatedResult
import org.mohanned.rawdatyci_cdapp.domain.model.User
import org.mohanned.rawdatyci_cdapp.domain.model.UserRole

fun AuthUserDto.toLoggedUser(tenantSlug: String) = LoggedUser(
    id = id, name = name, email = email,
    role = role.toUserRole(), avatarUrl = avatarUrl, tenantSlug = tenantSlug,
)

fun BrandingDto.toDomain() = Branding(
    appName = appName, logoUrl = logoUrl,
    primaryColor = primaryColor, showPoweredBy = showPoweredBy,
)

fun UserDto.toDomain() = User(
    id = id, name = name, email = email, phone = phone,
    role = role.toUserRole(), avatarUrl = avatarUrl, isActive = isActive,
    classId = classId, className = className, createdAt = createdAt,
)

fun ClassDto.toDomain() = Classroom(
    id = id, name = name, description = description,
    teacherId = teacherId, teacherName = teacherName,
    childrenCount = childrenCount, capacity = capacity,
    academicYear = academicYear, isActive = isActive, createdAt = createdAt,
)

fun ChildDto.toDomain() = Child(
    id = id, fullName = displayName, dateOfBirth = dob, gender = gender,
    photoUrl = photoUrl, classId = classId, className = className,
    parentId = parentId, parentName = parentName, parentPhone = parentPhone,
    enrollmentDate = enrollmentDate, stars = stars, notes = notes, allergies = allergies,
)

fun AttendanceRecordDto.toDomain() = AttendanceRecord(
    id = id, childId = childId, childName = childName, childPhoto = childPhoto,
    status = when (status) {
        "absent" -> AttendanceStatus.ABSENT
        "late" -> AttendanceStatus.LATE
        "excused" -> AttendanceStatus.EXCUSED
        else -> AttendanceStatus.PRESENT
    },
    notes = notes, date = date,
)

fun AttendanceSummaryDto.toDomain() = AttendanceSummary(
    date = date, classId = classId, total = total,
    present = present, absent = absent, late = late, presentPct = presentPct,
    records = records.map { it.toDomain() },
)

fun NewsDto.toDomain() = News(
    id = id, title = title, body = displayContent, imageUrl = imageUrl,
    isVisible = isVisible,
    type = when (type) {
        "announcement" -> NewsType.ANNOUNCEMENT
        "slider" -> NewsType.SLIDER
        else -> NewsType.NEWS
    },
    authorName = authorName, createdAt = createdAt,
)

fun ComplaintDto.toDomain() = Complaint(
    id = id, type = type, content = displayContent,
    parentId = parentId, parentName = parentName,
    status = when (status) {
        "in_review" -> ComplaintStatus.IN_REVIEW
        "resolved" -> ComplaintStatus.RESOLVED
        else -> ComplaintStatus.PENDING
    },
    reply = displayReply, createdAt = createdAt,
)

fun NotificationDto.toDomain() = AppNotification(
    id = id, title = title, body = body,
    type = type, isRead = isRead, createdAt = createdAt,
)

fun ConversationDto.toDomain() = Conversation(
    id = id, participantId = participantId, participantName = participantName,
    participantAvatar = participantAvatar, childName = childName,
    lastMessage = lastMessage, lastMessageAt = lastMessageAt,
    unreadCount = unreadCount, isOnline = isOnline,
)

fun MessageDto.toDomain() = Message(
    id = id, conversationId = conversationId, senderId = senderId,
    senderName = senderName, content = content, imageUrl = imageUrl,
    isRead = isRead, sentAt = sentAt,
)

fun GameQuestionDto.toDomain() = GameQuestion(
    id = id,
    gameType = when (resolvedType) {
        "numbers" -> GameType.NUMBERS
        "colors" -> GameType.COLORS
        else -> GameType.LETTERS
    },
    level = level, questionText = questionText, questionImage = questionImage,
    correctAnswer = correctAnswer, options = options, audioUrl = audioUrl,
)

fun SettingsDto.toDomain() = KindergartenSettings(
    kindergartenName = kindergartenName, address = address, phone = phone,
    whatsapp = whatsapp, twitter = twitter, instagram = instagram,
    mapLat = mapLat, mapLng = mapLng, academicYear = academicYear,
)

// ── helpers ───────────────────────────────────────────
private fun String.toUserRole() = when (this) {
    "super_admin" -> UserRole.SUPER_ADMIN
    "admin" -> UserRole.ADMIN
    "teacher" -> UserRole.TEACHER
    else -> UserRole.PARENT
}

fun <T, R> ApiListDto<T>.toPaginated(transform: (T) -> R): PaginatedResult<R> {
    val items = data.map(transform)
    return PaginatedResult(
        items = items,
        total = meta?.total ?: items.size,
        page = meta?.page ?: 1,
        lastPage = meta?.lastPage ?: 1,
        hasMore = (meta?.page ?: 1) < (meta?.lastPage ?: 1),
        meta = meta?.let { PageMeta(it.page, it.perPage, it.total, it.lastPage) }
    )
}
