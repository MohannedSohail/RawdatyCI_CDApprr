//package org.mohanned.rawdatyci_cdapp.data
//
//import kotlinx.coroutines.delay
//import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiResponse
//import org.mohanned.rawdatyci_cdapp.data.remote.dto.ApiMessageDto
//import org.mohanned.rawdatyci_cdapp.domain.model.*
//import org.mohanned.rawdatyci_cdapp.domain.repository.*
//
//// ══════════════════════════════════════════════════════
////  MOCK DATA
//// ══════════════════════════════════════════════════════
//object MockData {
//
//    // ── Users ─────────────────────────────────────────
//    val adminUser = LoggedUser(
//        id = "1",
//        name = "أحمد المدير",
//        email = "admin@rawdaty.app",
//        role = UserRole.ADMIN,
//        avatarUrl = null,
//        tenantSlug = "demo",
//    )
//
//    val teacherUser = LoggedUser(
//        id = "2",
//        name = "سارة أحمد",
//        email = "teacher@rawdaty.app",
//        role = UserRole.TEACHER,
//        avatarUrl = null,
//        tenantSlug = "demo",
//    )
//
//    val parentUser = LoggedUser(
//        id = "3",
//        name = "محمد العلي",
//        email = "parent@rawdaty.app",
//        role = UserRole.PARENT,
//        avatarUrl = null,
//        tenantSlug = "demo",
//    )
//
//    // ── Credentials ───────────────────────────────────
//    val mockCredentials = mapOf(
//        "admin@rawdaty.app" to Pair("123456", adminUser),
//        "teacher@rawdaty.app" to Pair("123456", teacherUser),
//        "parent@rawdaty.app" to Pair("123456", parentUser),
//    )
//
//    // ── Classrooms ────────────────────────────────────
//    val classrooms = listOf(
//        Classroom("1", "فصل الزهور", null, "2", "سارة أحمد", 22, 25, "2024", "2024-01-01"),
//        Classroom("2", "فصل النجوم", null, "5", "نورة محمد", 18, 25, "2024", "2024-01-01"),
//        Classroom("3", "فصل الشمس", null, "6", "هدى علي", 25, 25, "2024", "2024-01-01"),
//        Classroom("4", "فصل القمر", null, null, null, 10, 25, "2024", "2024-01-01"),
//    )
//
//    // ── Children ──────────────────────────────────────
//    val children = listOf(
//        Child("1", "أحمد محمد العلي", "2019-05-10", "male", null, "1", "فصل الزهور", "3", "محمد العلي", "0501111111", "2024-09-01", 4, "طالب مجتهد", listOf("الفول السوداني")),
//        Child("2", "سارة خالد", "2019-08-15", "female", null, "1", "فصل الزهور", "3", "خالد سارة", "0502222222", "2024-09-01", 5, null, emptyList()),
//        Child("3", "عمر أحمد ناصر", "2019-03-20", "male", null, "1", "فصل الزهور", "4", "أحمد ناصر", "0503333333", "2024-09-01", 3, null, listOf("الحليب")),
//        Child("4", "لينا محمد", "2019-11-05", "female", null, "1", "فصل الزهور", "3", "محمد لينا", "0504444444", "2024-09-01", 5, "موهوبة في الرسم", emptyList()),
//        Child("5", "يوسف علي", "2019-07-12", "male", null, "1", "فصل الزهور", "3", "علي يوسف", "0505555555", "2024-09-01", 4, null, emptyList()),
//        Child("6", "ريم سالم", "2019-09-18", "female", null, "2", "فصل النجوم", "5", "سالم ريم", "0506666666", "2024-09-01", 5, null, emptyList()),
//        Child("7", "فهد ناصر", "2019-04-25", "male", null, "2", "فصل النجوم", "5", "ناصر فهد", "0507777777", "2024-09-01", 3, null, emptyList()),
//        Child("8", "نورة عبدالله", "2019-06-30", "female", null, "2", "فصل النجوم", "5", "عبدالله نورة", "0508888888", "2024-09-01", 4, null, emptyList()),
//    )
//
//    // ── Users List ────────────────────────────────────
//    val users = listOf(
//        User("2", "سارة أحمد", "teacher@rawdaty.app", "0501234567", UserRole.TEACHER, null, true, "1", "فصل الزهور", "2024-01-01"),
//        User("5", "نورة محمد", "noura@rawdaty.app", "0507654321", UserRole.TEACHER, null, true, "2", "فصل النجوم", "2024-01-01"),
//        User("6", "هدى علي", "huda@rawdaty.app", "0509876543", UserRole.TEACHER, null, true, "3", "فصل الشمس", "2024-01-01"),
//        User("3", "محمد العلي", "parent@rawdaty.app", "0501111111", UserRole.PARENT, null, true, null, null, "2024-01-01"),
//        User("7", "خالد سارة", "k.sara@rawdaty.app", "0502222222", UserRole.PARENT, null, true, null, null, "2024-01-01"),
//        User("8", "أحمد ناصر", "a.naser@rawdaty.app", "0503333333", UserRole.PARENT, null, true, null, null, "2024-01-01"),
//    )
//
//    // ── News ──────────────────────────────────────────
//    val news = listOf(
//        News("1", "رحلة مدرسية الأسبوع القادم", "محتوى الخبر...", null, true, "الإدارة", "٢٠٢٤/٣/١"),
//        News("2", "اجتماع أولياء الأمور", "محتوى الخبر...", null, true, "المدير", "٢٠٢٤/٣/٢"),
//        News("3", "نتائج الفصل الدراسي الأول", "محتوى الخبر...", null, false, "الإدارة", "٢٠٢٤/٣/٣"),
//    )
//
//    // ── Notifications ─────────────────────────────────
//    val notifications = listOf(
//        AppNotification("1", "إعلان جديد", "تمت إضافة رحلة...", "news", false, "منذ ٥ دقائق"),
//        AppNotification("2", "تقرير الحضور", "تم تسجيل حضور...", "attendance", false, "١٠:٣٠ ص"),
//    )
//
//    // ── Conversations ─────────────────────────────────
//    val conversations = listOf(
//        Conversation("1", "3", "محمد العلي", null, "أحمد", "كيف حال ابني؟", "١٠:٣٠", 2, true),
//        Conversation("2", "7", "خالد سارة", null, "سارة", "شكراً", "٩:١٥", 0, false),
//    )
//
//    val messages = listOf(
//        Message("1", "1", "3", "محمد العلي", "السلام عليكم", null, true, "١٠:٢٠"),
//        Message("2", "1", "2", "سارة أحمد", "وعليكم السلام", null, true, "١٠:22"),
//    )
//}
//
//// ── Mock Repositories Implementation ──────────────────
//
//class MockAuthRepository : AuthRepository {
//    override suspend fun login(identifier: String, password: String): ApiResponse<Pair<LoggedUser, AuthTokens>> {
//        delay(1000)
//        val creds = MockData.mockCredentials[identifier]
//        return if (creds != null && creds.first == password) {
//            ApiResponse.Success(Pair(creds.second, AuthTokens("access", "refresh", 3600)))
//        } else {
//            ApiResponse.Error(401, "خطأ في البريد أو كلمة المرور")
//        }
//    }
//
//    override suspend fun logout(): ApiResponse<Unit> = ApiResponse.Success(Unit)
//    override suspend fun forgotPassword(email: String): ApiResponse<Unit> = ApiResponse.Success(Unit)
//    override suspend fun verifyOtp(email: String, otp: String): ApiResponse<String> = ApiResponse.Success("token_123")
//    override suspend fun resetPassword(resetToken: String, newPassword: String, confirmPassword: String): ApiResponse<Unit> = ApiResponse.Success(Unit)
//}
//
//class MockUsersRepository : UsersRepository {
//    override suspend fun getUsers(role: String?, classId: String?, search: String?, page: Int): ApiResponse<PaginatedResult<User>> {
//        delay(500)
//        return ApiResponse.Success(PaginatedResult(MockData.users, PageMeta(1, 10, MockData.users.size, 1)))
//    }
//
//    override suspend fun getUser(id: String): ApiResponse<User> {
//        val user = MockData.users.find { it.id == id }
//        return if (user != null) ApiResponse.Success(user) else ApiResponse.Error(404, "User not found")
//    }
//
//    override suspend fun createUser(name: String, email: String, password: String, role: String, phone: String?, classId: String?): ApiResponse<User> {
//        val newUser = User("${MockData.users.size + 1}", name, email, phone, UserRole.TEACHER, null, true, classId, null, "2024-01-01")
//        return ApiResponse.Success(newUser)
//    }
//
//    override suspend fun updateUser(id: String, name: String?, phone: String?, isActive: Boolean?): ApiResponse<User> {
//        val user = MockData.users.find { it.id == id } ?: return ApiResponse.Error(404, "Not found")
//        return ApiResponse.Success(user.copy(name = name ?: user.name, phone = phone ?: user.phone, isActive = isActive ?: user.isActive))
//    }
//
//    override suspend fun deleteUser(id: String): ApiResponse<Unit> = ApiResponse.Success(Unit)
//    override suspend fun getProfile(): ApiResponse<User> = ApiResponse.Success(MockData.users.first())
//    override suspend fun updateProfile(name: String, phone: String?): ApiResponse<User> = ApiResponse.Success(MockData.users.first().copy(name = name, phone = phone))
//    override suspend fun changePassword(current: String, newPass: String, confirm: String): ApiResponse<Unit> = ApiResponse.Success(Unit)
//}
//
//class MockClassesRepository : ClassesRepository {
//    override suspend fun getClasses(search: String?, page: Int): ApiResponse<PaginatedResult<Classroom>> =
//        ApiResponse.Success(PaginatedResult(MockData.classrooms, PageMeta(1, 10, MockData.classrooms.size, 1)))
//
//    override suspend fun getClass(id: String): ApiResponse<Classroom> {
//        val c = MockData.classrooms.find { it.id == id }
//        return if (c != null) ApiResponse.Success(c) else ApiResponse.Error(404, "Not found")
//    }
//
//    override suspend fun createClass(name: String, teacherId: String?, capacity: Int?): ApiResponse<Classroom> =
//        ApiResponse.Success(MockData.classrooms.first())
//
//    override suspend fun updateClass(id: String, name: String?, teacherId: String?): ApiResponse<Classroom> =
//        ApiResponse.Success(MockData.classrooms.first())
//
//    override suspend fun deleteClass(id: String): ApiResponse<Unit> = ApiResponse.Success(Unit)
//
//    override suspend fun getChildrenByClass(classId: String, page: Int): ApiResponse<PaginatedResult<Child>> =
//        ApiResponse.Success(PaginatedResult(MockData.children, PageMeta(1, 10, MockData.children.size, 1)))
//
//    override suspend fun getMyChildren(): ApiResponse<PaginatedResult<Child>> =
//        ApiResponse.Success(PaginatedResult(MockData.children, PageMeta(1, 10, MockData.children.size, 1)))
//}
//
//class MockAttendanceRepository : AttendanceRepository {
//    override suspend fun getAttendanceReport(classId: String, date: String): ApiResponse<AttendanceSummary> =
//        ApiResponse.Success(AttendanceSummary(date, classId, 20, 18, 2, 0, 0.9f))
//
//    override suspend fun recordAttendance(classId: String, date: String, records: Map<String, AttendanceStatus>): ApiResponse<Unit> =
//        ApiResponse.Success(Unit)
//
//    override suspend fun getChildAttendance(childId: String): ApiResponse<List<AttendanceRecord>> =
//        ApiResponse.Success(emptyList())
//}
//
//class MockNewsRepository : NewsRepository {
//    override suspend fun getNews(search: String?, page: Int): ApiResponse<PaginatedResult<News>> =
//        ApiResponse.Success(PaginatedResult(MockData.news, PageMeta(1, 10, MockData.news.size, 1)))
//    override suspend fun getNewsItem(id: String): ApiResponse<News> = ApiResponse.Success(MockData.news.first())
//    override suspend fun createNews(title: String, body: String, isPinned: Boolean): ApiResponse<News> = ApiResponse.Success(MockData.news.first())
//    override suspend fun updateNews(id: String, title: String?, body: String?): ApiResponse<News> = ApiResponse.Success(MockData.news.first())
//    override suspend fun deleteNews(id: String): ApiResponse<Unit> = ApiResponse.Success(Unit)
//}
//
//class MockComplaintsRepository : ComplaintsRepository {
//    override suspend fun getComplaints(status: String?, page: Int): ApiResponse<PaginatedResult<Complaint>> =
//        ApiResponse.Success(PaginatedResult(emptyList(), PageMeta(1, 10, 0, 1)))
//    override suspend fun getComplaint(id: String): ApiResponse<Complaint> = ApiResponse.Error(404, "Not found")
//    override suspend fun createComplaint(title: String, body: String): ApiResponse<Complaint> =
//        ApiResponse.Success(Complaint("1", title, body, "3", "محمد العلي", ComplaintStatus.PENDING, null, "2024-01-01"))
//    override suspend fun replyToComplaint(id: String, reply: String, status: String): ApiResponse<Complaint> =
//        ApiResponse.Success(Complaint(id, "Title", "Body", "3", "Parent", ComplaintStatus.RESOLVED, reply, "2024-01-01"))
//}
//
//class MockNotificationsRepository : NotificationsRepository {
//    override suspend fun getNotifications(page: Int): ApiResponse<PaginatedResult<AppNotification>> =
//        ApiResponse.Success(PaginatedResult(MockData.notifications, PageMeta(1, 10, MockData.notifications.size, 1)))
//    override suspend fun markRead(id: String): ApiResponse<Unit> = ApiResponse.Success(Unit)
//    override suspend fun markAllRead(): ApiResponse<Unit> = ApiResponse.Success(Unit)
//    override suspend fun sendNotification(title: String, body: String, target: String, classId: String?): ApiResponse<ApiMessageDto> =
//        ApiResponse.Success(ApiMessageDto("تم الإرسال بنجاح", true))
//}
//
//class MockChatRepository : ChatRepository {
//    override suspend fun getConversations(page: Int): ApiResponse<PaginatedResult<Conversation>> =
//        ApiResponse.Success(PaginatedResult(MockData.conversations, PageMeta(1, 10, MockData.conversations.size, 1)))
//    override suspend fun getMessages(conversationId: String, page: Int): ApiResponse<PaginatedResult<Message>> =
//        ApiResponse.Success(PaginatedResult(MockData.messages, PageMeta(1, 10, MockData.messages.size, 1)))
//    override suspend fun sendMessage(conversationId: String, content: String): ApiResponse<Message> = ApiResponse.Success(MockData.messages.first())
//    override suspend fun startConversation(participantId: String, childId: String?): ApiResponse<Conversation> = ApiResponse.Success(MockData.conversations.first())
//}
//
//class MockGamesRepository : GamesRepository {
//    override suspend fun getQuestions(gameType: String, level: Int): ApiResponse<List<GameQuestion>> = ApiResponse.Success(emptyList())
//    override suspend fun saveResult(childId: String, gameType: String, score: Int, totalQuestions: Int, durationSeconds: Int): ApiResponse<Unit> = ApiResponse.Success(Unit)
//}
