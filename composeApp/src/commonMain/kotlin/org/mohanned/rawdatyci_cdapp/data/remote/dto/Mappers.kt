package org.mohanned.rawdatyci_cdapp.data.remote.dto

import org.mohanned.rawdatyci_cdapp.domain.model.AppNotification
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceRecord
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceStatus
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceSummary
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.domain.model.Classroom
import org.mohanned.rawdatyci_cdapp.domain.model.Complaint
import org.mohanned.rawdatyci_cdapp.domain.model.ComplaintStatus
import org.mohanned.rawdatyci_cdapp.domain.model.Conversation
import org.mohanned.rawdatyci_cdapp.domain.model.GameQuestion
import org.mohanned.rawdatyci_cdapp.domain.model.GameType
import org.mohanned.rawdatyci_cdapp.domain.model.Message
import org.mohanned.rawdatyci_cdapp.domain.model.News
import org.mohanned.rawdatyci_cdapp.domain.model.User
import org.mohanned.rawdatyci_cdapp.domain.model.UserRole


// ── User Mapper ───────────────────────────────────────
fun UserDto.toDomain() = User(
    id = id,
    name = name,
    email = email,
    phone = phone,
    role = when (role) {
        "super_admin" -> UserRole.SUPER_ADMIN
        "admin" -> UserRole.ADMIN
        "teacher" -> UserRole.TEACHER
        else -> UserRole.PARENT
    },
    avatarUrl = avatarUrl,
    isActive = isActive,
    classId = classId,
    className = className,
    createdAt = createdAt,
)

// ── Class Mapper ──────────────────────────────────────
fun ClassDto.toDomain() = Classroom(
    id = id,
    name = name,
    description = description,
    teacherId = teacherId,
    teacherName = teacherName,
    childrenCount = childrenCount,
    capacity = capacity,
    academicYear = academicYear,
    createdAt = createdAt,
)

// ── Child Mapper ──────────────────────────────────────
fun ChildDto.toDomain() = Child(
    id = id,
    fullName = fullName,
    dateOfBirth = dateOfBirth,
    gender = gender,
    photoUrl = photoUrl,
    classId = classId,
    className = className,
    parentId = parentId,
    parentName = parentName,
    parentPhone = parentPhone,
    enrollmentDate = enrollmentDate,
    stars = stars,
    notes = notes,
    allergies = allergies,
)

// ── Attendance Mapper ─────────────────────────────────
fun AttendanceRecordDto.toDomain() = AttendanceRecord(
    id = id,
    childId = childId,
    childName = childName,
    childPhoto = childPhoto,
    status = when (status) {
        "absent" -> AttendanceStatus.ABSENT
        "late" -> AttendanceStatus.LATE
        "excused" -> AttendanceStatus.EXCUSED
        else -> AttendanceStatus.PRESENT
    },
    notes = notes,
    date = date,
)

fun AttendanceSummaryDto.toDomain() = AttendanceSummary(
    date = date,
    classId = classId,
    total = total,
    present = present,
    absent = absent,
    late = late,
    presentPct = presentPct,
)

// ── News Mapper ───────────────────────────────────────
fun NewsDto.toDomain() = News(
    id = id,
    title = title,
    body = body,
    imageUrl = imageUrl,
    isPinned = isPinned,
    authorName = authorName,
    createdAt = createdAt,
)

// ── Complaint Mapper ──────────────────────────────────
fun ComplaintDto.toDomain() = Complaint(
    id = id,
    title = title,
    body = body,
    parentId = parentId,
    parentName = parentName,
    status = when (status) {
        "in_review" -> ComplaintStatus.IN_REVIEW
        "resolved" -> ComplaintStatus.RESOLVED
        else -> ComplaintStatus.PENDING
    },
    adminReply = adminReply,
    createdAt = createdAt,
)

// ── Notification Mapper ───────────────────────────────
fun NotificationDto.toDomain() = AppNotification(
    id = id,
    title = title,
    body = body,
    type = type,
    isRead = isRead,
    createdAt = createdAt,
)

// ── Conversation Mapper ───────────────────────────────
fun ConversationDto.toDomain() = Conversation(
    id = id,
    participantId = participantId,
    participantName = participantName,
    participantAvatar = participantAvatar,
    childName = childName,
    lastMessage = lastMessage,
    lastMessageAt = lastMessageAt,
    unreadCount = unreadCount,
    isOnline = isOnline,
)

// ── Message Mapper ────────────────────────────────────
fun MessageDto.toDomain() = Message(
    id = id,
    conversationId = conversationId,
    senderId = senderId,
    senderName = senderName,
    content = content,
    imageUrl = imageUrl,
    isRead = isRead,
    sentAt = sentAt,
)

// ── Game Mapper ───────────────────────────────────────
fun GameQuestionDto.toDomain() = GameQuestion(
    id = id,
    gameType = when (type) {
        "numbers" -> GameType.NUMBERS
        "colors" -> GameType.COLORS
        else -> GameType.LETTERS
    },
    level = level,
    questionText = questionText,
    questionImage = questionImage,
    correctAnswer = correctAnswer,
    options = options,
    audioUrl = audioUrl,
)