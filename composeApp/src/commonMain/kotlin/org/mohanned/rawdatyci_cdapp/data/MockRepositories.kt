package org.mohanned.rawdatyci_cdapp.data

import kotlinx.coroutines.delay
import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.model.AppNotification
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceRecord
import org.mohanned.rawdatyci_cdapp.domain.model.AttendanceSummary
import org.mohanned.rawdatyci_cdapp.domain.model.AuthTokens
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.domain.model.Classroom
import org.mohanned.rawdatyci_cdapp.domain.model.Complaint
import org.mohanned.rawdatyci_cdapp.domain.model.ComplaintStatus
import org.mohanned.rawdatyci_cdapp.domain.model.Conversation
import org.mohanned.rawdatyci_cdapp.domain.model.GameQuestion
import org.mohanned.rawdatyci_cdapp.domain.model.GameType
import org.mohanned.rawdatyci_cdapp.domain.model.LoggedUser
import org.mohanned.rawdatyci_cdapp.domain.model.Message
import org.mohanned.rawdatyci_cdapp.domain.model.News
import org.mohanned.rawdatyci_cdapp.domain.model.PageMeta
import org.mohanned.rawdatyci_cdapp.domain.model.PaginatedResult
import org.mohanned.rawdatyci_cdapp.domain.model.User
import org.mohanned.rawdatyci_cdapp.domain.model.UserRole
import org.mohanned.rawdatyci_cdapp.domain.repository.AttendanceRepository
import org.mohanned.rawdatyci_cdapp.domain.repository.AuthRepository
import org.mohanned.rawdatyci_cdapp.domain.repository.ChatRepository
import org.mohanned.rawdatyci_cdapp.domain.repository.ClassesRepository
import org.mohanned.rawdatyci_cdapp.domain.repository.ComplaintsRepository
import org.mohanned.rawdatyci_cdapp.domain.repository.GamesRepository
import org.mohanned.rawdatyci_cdapp.domain.repository.NewsRepository
import org.mohanned.rawdatyci_cdapp.domain.repository.NotificationsRepository
import org.mohanned.rawdatyci_cdapp.domain.repository.UsersRepository

// ══════════════════════════════════════════════════════
//  MOCK DATA
// ══════════════════════════════════════════════════════
object MockData {

    // ── Users ─────────────────────────────────────────
    val adminUser = LoggedUser(
        id = 1,
        name = "أحمد المدير",
        email = "admin@rawdaty.app",
        role = UserRole.ADMIN,
        avatarUrl = null,
        tenantSlug = "demo",
    )

    val teacherUser = LoggedUser(
        id = 2,
        name = "سارة أحمد",
        email = "teacher@rawdaty.app",
        role = UserRole.TEACHER,
        avatarUrl = null,
        tenantSlug = "demo",
    )

    val parentUser = LoggedUser(
        id = 3,
        name = "محمد العلي",
        email = "parent@rawdaty.app",
        role = UserRole.PARENT,
        avatarUrl = null,
        tenantSlug = "demo",
    )

    // ── Credentials ───────────────────────────────────
    val mockCredentials = mapOf(
        "admin@rawdaty.app" to Pair("123456", adminUser),
        "teacher@rawdaty.app" to Pair("123456", teacherUser),
        "parent@rawdaty.app" to Pair("123456", parentUser),
    )

    // ── Classrooms ────────────────────────────────────
    val classrooms = listOf(
        Classroom(1, "فصل الزهور", null, 2, "سارة أحمد", 22, 25, "2024", "2024-01-01"),
        Classroom(2, "فصل النجوم", null, 5, "نورة محمد", 18, 25, "2024", "2024-01-01"),
        Classroom(3, "فصل الشمس", null, 6, "هدى علي", 25, 25, "2024", "2024-01-01"),
        Classroom(4, "فصل القمر", null, null, null, 10, 25, "2024", "2024-01-01"),
    )

    // ── Children ──────────────────────────────────────
    val children = listOf(
        Child(
            1,
            "أحمد محمد العلي",
            "2019-05-10",
            "male",
            null,
            1,
            "فصل الزهور",
            3,
            "محمد العلي",
            "0501111111",
            "2024-09-01",
            4,
            "طالب مجتهد",
            listOf("الفول السوداني")
        ),
        Child(
            2,
            "سارة خالد",
            "2019-08-15",
            "female",
            null,
            1,
            "فصل الزهور",
            3,
            "خالد سارة",
            "0502222222",
            "2024-09-01",
            5,
            null,
            emptyList()
        ),
        Child(
            3,
            "عمر أحمد ناصر",
            "2019-03-20",
            "male",
            null,
            1,
            "فصل الزهور",
            4,
            "أحمد ناصر",
            "0503333333",
            "2024-09-01",
            3,
            null,
            listOf("الحليب")
        ),
        Child(
            4,
            "لينا محمد",
            "2019-11-05",
            "female",
            null,
            1,
            "فصل الزهور",
            3,
            "محمد لينا",
            "0504444444",
            "2024-09-01",
            5,
            "موهوبة في الرسم",
            emptyList()
        ),
        Child(
            5,
            "يوسف علي",
            "2019-07-12",
            "male",
            null,
            1,
            "فصل الزهور",
            3,
            "علي يوسف",
            "0505555555",
            "2024-09-01",
            4,
            null,
            emptyList()
        ),
        Child(
            6,
            "ريم سالم",
            "2019-09-18",
            "female",
            null,
            2,
            "فصل النجوم",
            5,
            "سالم ريم",
            "0506666666",
            "2024-09-01",
            5,
            null,
            emptyList()
        ),
        Child(
            7,
            "فهد ناصر",
            "2019-04-25",
            "male",
            null,
            2,
            "فصل النجوم",
            5,
            "ناصر فهد",
            "0507777777",
            "2024-09-01",
            3,
            null,
            emptyList()
        ),
        Child(
            8,
            "نورة عبدالله",
            "2019-06-30",
            "female",
            null,
            2,
            "فصل النجوم",
            5,
            "عبدالله نورة",
            "0508888888",
            "2024-09-01",
            4,
            null,
            emptyList()
        ),
    )

    // ── Users List ────────────────────────────────────
    val users = listOf(
        User(
            2,
            "سارة أحمد",
            "teacher@rawdaty.app",
            "0501234567",
            UserRole.TEACHER,
            null,
            true,
            1,
            "فصل الزهور",
            "2024-01-01"
        ),
        User(
            5,
            "نورة محمد",
            "noura@rawdaty.app",
            "0507654321",
            UserRole.TEACHER,
            null,
            true,
            2,
            "فصل النجوم",
            "2024-01-01"
        ),
        User(
            6,
            "هدى علي",
            "huda@rawdaty.app",
            "0509876543",
            UserRole.TEACHER,
            null,
            false,
            3,
            "فصل الشمس",
            "2024-01-01"
        ),
        User(
            3,
            "محمد العلي",
            "parent@rawdaty.app",
            "0501111111",
            UserRole.PARENT,
            null,
            true,
            null,
            null,
            "2024-01-01"
        ),
        User(
            7,
            "خالد سارة",
            "k.sara@rawdaty.app",
            "0502222222",
            UserRole.PARENT,
            null,
            true,
            null,
            null,
            "2024-01-01"
        ),
        User(
            8,
            "أحمد ناصر",
            "a.naser@rawdaty.app",
            "0503333333",
            UserRole.PARENT,
            null,
            true,
            null,
            null,
            "2024-01-01"
        ),
    )

    // ── News ──────────────────────────────────────────
    val news = listOf(
        News(
            1,
            "رحلة مدرسية الأسبوع القادم",
            "ستنطلق الرحلة يوم الخميس القادم إلى حديقة الحيوانات المركزية، يرجى التأكد من ارتداء الطفل للزي الموحد وإحضار وجبة خفيفة.",
            null,
            true,
            "الإدارة",
            "٢٠٢٤/٣/١"
        ),
        News(
            2,
            "اجتماع أولياء الأمور",
            "يُعقد الاجتماع الفصلي لأولياء الأمور يوم الاثنين القادم الساعة السادسة مساءً في قاعة الاجتماعات الرئيسية.",
            null,
            true,
            "المدير",
            "٢٠٢٤/٣/٢"
        ),
        News(
            3,
            "نتائج الفصل الدراسي الأول",
            "يسعدنا إعلامكم بأن نتائج الفصل الدراسي الأول قد رُفعت على المنصة، يمكنكم الاطلاع عليها من خلال ملف طفلكم.",
            null,
            false,
            "الإدارة",
            "٢٠٢٤/٣/٣"
        ),
        News(
            4,
            "إجازة اليوم الوطني",
            "تعلن روضتي عن إجازة رسمية بمناسبة اليوم الوطني يوم الخميس القادم، وستستأنف الدراسة يوم الأحد.",
            null,
            false,
            "المدير",
            "٢٠٢٤/٣/٤"
        ),
        News(
            5,
            "ورشة تطوير المهارات",
            "تُقام ورشة عمل لتطوير المهارات الإبداعية يوم الأربعاء القادم للأطفال من سن 4-6 سنوات، التسجيل مفتوح.",
            null,
            false,
            "الإدارة",
            "٢٠٢٤/٣/٥"
        ),
    )

    // ── Notifications ─────────────────────────────────
    val notifications = listOf(
        AppNotification(
            1,
            "إعلان جديد",
            "تمت إضافة رحلة مدرسية للأسبوع القادم",
            "news",
            false,
            "منذ ٥ دقائق"
        ),
        AppNotification(
            2,
            "تقرير الحضور",
            "تم تسجيل حضور طفلك اليوم الساعة ٧:٤٥ صباحاً",
            "attendance",
            false,
            "١٠:٣٠ ص"
        ),
        AppNotification(
            3,
            "رسالة من المعلمة",
            "أردت إبلاغكم بأن أحمد أظهر تقدماً رائعاً",
            "message",
            false,
            "٨:١٥ ص"
        ),
        AppNotification(
            4,
            "تأكيد الحضور",
            "تم تسجيل حضور طفلك في رحلة الأسبوع القادم",
            "attendance",
            true,
            "أمس ٤:٠٠ م"
        ),
        AppNotification(
            5,
            "تنبيه من الإدارة",
            "الروضة ستكون مغلقة يوم الخميس القادم",
            "news",
            true,
            "أمس ١:٣٠ م"
        ),
    )

    // ── Conversations ─────────────────────────────────
    val conversations = listOf(
        Conversation(1, 3, "محمد العلي", null, "أحمد", "كيف حال ابني اليوم؟", "١٠:٣٠", 2, true),
        Conversation(2, 7, "خالد سارة", null, "سارة", "شكراً على المتابعة", "٩:١٥", 0, false),
        Conversation(3, 8, "أحمد ناصر", null, "عمر", "هل يحضر معه الكتاب غداً؟", "أمس", 1, false),
    )

    // ── Messages ──────────────────────────────────────
    val messages = listOf(
        Message(1, 1, 3, "محمد العلي", "السلام عليكم، كيف حال أحمد اليوم؟", null, true, "١٠:٢٠"),
        Message(
            2,
            1,
            2,
            "سارة أحمد",
            "وعليكم السلام، أحمد بخير وكان نشيطاً جداً اليوم",
            null,
            true,
            "١٠:٢٢"
        ),
        Message(3, 1, 3, "محمد العلي", "الحمد لله، هل شارك في الأنشطة؟", null, true, "١٠:٢٥"),
        Message(
            4,
            1,
            2,
            "سارة أحمد",
            "نعم شارك بشكل ممتاز في حصة الرسم وأبدع فيها ما شاء الله",
            null,
            false,
            "١٠:٢٨"
        ),
    )

    // ── Games ─────────────────────────────────────────
    val letterQuestions = listOf(
        GameQuestion(
            1,
            GameType.LETTERS,
            1,
            "ما هو الشيء الذي يبدأ بحرف (أ)؟",
            "أ",
            "أسد",
            listOf("أسد", "بطة", "قطة", "سمكة"),
            null
        ),
        GameQuestion(
            2,
            GameType.LETTERS,
            1,
            "ما هو الشيء الذي يبدأ بحرف (ب)؟",
            "ب",
            "بيت",
            listOf("بيت", "شمس", "قمر", "نجمة"),
            null
        ),
        GameQuestion(
            3,
            GameType.LETTERS,
            1,
            "ما هو الشيء الذي يبدأ بحرف (ت)؟",
            "ت",
            "تفاحة",
            listOf("تفاحة", "موزة", "برتقال", "عنب"),
            null
        ),
        GameQuestion(
            4,
            GameType.LETTERS,
            1,
            "ما هو الشيء الذي يبدأ بحرف (س)؟",
            "س",
            "سمكة",
            listOf("سمكة", "أسد", "فيل", "زرافة"),
            null
        ),
        GameQuestion(
            5,
            GameType.LETTERS,
            1,
            "ما هو الشيء الذي يبدأ بحرف (ك)؟",
            "ك",
            "كتاب",
            listOf("كتاب", "قلم", "مسطرة", "مقص"),
            null
        ),
    )

    val numberQuestions = listOf(
        GameQuestion(
            6,
            GameType.NUMBERS,
            1,
            "كم عدد الأصابع في يد واحدة؟",
            "٥",
            "خمسة",
            listOf("ثلاثة", "أربعة", "خمسة", "ستة"),
            null
        ),
        GameQuestion(
            7,
            GameType.NUMBERS,
            1,
            "ما هو الرقم الذي يأتي بعد ٣؟",
            "٣",
            "أربعة",
            listOf("اثنان", "ثلاثة", "أربعة", "خمسة"),
            null
        ),
        GameQuestion(
            8,
            GameType.NUMBERS,
            1,
            "كم عدد أضلاع المثلث؟",
            "△",
            "ثلاثة",
            listOf("اثنان", "ثلاثة", "أربعة", "خمسة"),
            null
        ),
        GameQuestion(
            9,
            GameType.NUMBERS,
            1,
            "ما هو الرقم الذي يأتي قبل ٥؟",
            "٥",
            "أربعة",
            listOf("ستة", "أربعة", "ثلاثة", "اثنان"),
            null
        ),
        GameQuestion(
            10,
            GameType.NUMBERS,
            1,
            "كم عدد عجلات السيارة؟",
            "🚗",
            "أربعة",
            listOf("ثلاثة", "أربعة", "خمسة", "ستة"),
            null
        ),
    )

    val colorQuestions = listOf(
        GameQuestion(
            11,
            GameType.COLORS,
            1,
            "ما لون السماء؟",
            "🌤",
            "أزرق",
            listOf("أحمر", "أزرق", "أخضر", "أصفر"),
            null
        ),
        GameQuestion(
            12,
            GameType.COLORS,
            1,
            "ما لون العشب؟",
            "🌿",
            "أخضر",
            listOf("أزرق", "أخضر", "أصفر", "بنفسجي"),
            null
        ),
        GameQuestion(
            13,
            GameType.COLORS,
            1,
            "ما لون الشمس؟",
            "☀️",
            "أصفر",
            listOf("أحمر", "أزرق", "أصفر", "أخضر"),
            null
        ),
        GameQuestion(
            14,
            GameType.COLORS,
            1,
            "ما لون التفاحة الحمراء؟",
            "🍎",
            "أحمر",
            listOf("أحمر", "أخضر", "برتقالي", "أصفر"),
            null
        ),
        GameQuestion(
            15,
            GameType.COLORS,
            1,
            "ما لون الموز؟",
            "🍌",
            "أصفر",
            listOf("أخضر", "أحمر", "أصفر", "أزرق"),
            null
        ),
    )

    // ── Complaints ────────────────────────────────────
    val complaints = listOf(
        Complaint(
            1,
            "تأخر في الاستجابة",
            "أرسلت رسالة للمعلمة منذ أسبوع",
            3,
            "محمد العلي",
            ComplaintStatus.PENDING,
            null,
            "٢٠٢٤/٣/١"
        ),
        Complaint(
            2,
            "مقترح تحسين المرافق",
            "أقترح إضافة ألعاب جديدة في الساحة",
            7,
            "خالد سارة",
            ComplaintStatus.IN_REVIEW,
            "شكراً لمقترحك، سنأخذه بعين الاعتبار",
            "٢٠٢٤/٣/٢"
        ),
        Complaint(
            3,
            "وجبة الإفطار",
            "الوجبة لا تناسب الاحتياجات الغذائية للأطفال",
            8,
            "أحمد ناصر",
            ComplaintStatus.RESOLVED,
            "تم تحديث قائمة الوجبات",
            "٢٠٢٤/٣/٣"
        ),
    )
}

// ══════════════════════════════════════════════════════
//  MOCK AUTH REPOSITORY
// ══════════════════════════════════════════════════════
class MockAuthRepository : AuthRepository {

    override suspend fun login(
        email: String,
        password: String,
    ): ApiResponse<Pair<LoggedUser, AuthTokens>> {
        delay(800) // simulate network

        val entry = MockData.mockCredentials[email.trim().lowercase()]
        return if (entry != null && entry.first == password) {
            ApiResponse.Success(
                Pair(
                    entry.second,
                    AuthTokens("mock_access_token", "mock_refresh_token", 3600),
                )
            )
        } else {
            ApiResponse.Error(401, "البريد الإلكتروني أو كلمة المرور غير صحيحة")
        }
    }

    override suspend fun logout(): ApiResponse<Unit> = ApiResponse.Success(Unit)

    override suspend fun forgotPassword(email: String): ApiResponse<Unit> {
        delay(600)
        return ApiResponse.Success(Unit)
    }

    override suspend fun verifyOtp(email: String, otp: String): ApiResponse<Unit> {
        delay(600)
        return if (otp == "1234") ApiResponse.Success(Unit)
        else ApiResponse.Error(400, "الرمز غير صحيح")
    }

    override suspend fun resetPassword(
        email: String, otp: String,
        newPassword: String, confirmPassword: String,
    ): ApiResponse<Unit> {
        delay(600)
        return ApiResponse.Success(Unit)
    }
}

// ══════════════════════════════════════════════════════
//  MOCK CLASSES REPOSITORY
// ══════════════════════════════════════════════════════
class MockClassesRepository : ClassesRepository {

    override suspend fun getClasses(
        search: String?,
        page: Int
    ): ApiResponse<PaginatedResult<Classroom>> {
        delay(500)
        var list = MockData.classrooms
        if (search != null) list = list.filter { it.name.contains(search) }
        return ApiResponse.Success(
            PaginatedResult(list, PageMeta(1, 15, list.size, 1))
        )
    }

    override suspend fun getMyChildren(): ApiResponse<PaginatedResult<Child>> {
        delay(500)
        return ApiResponse.Success(
            PaginatedResult(MockData.children.take(2), PageMeta(1, 15, 2, 1))
        )
    }

    override suspend fun getClass(id: Int): ApiResponse<Classroom> {
        delay(300)
        val classroom = MockData.classrooms.find { it.id == id }
        return if (classroom != null) ApiResponse.Success(classroom)
        else ApiResponse.Error(404, "الفصل غير موجود")
    }

    override suspend fun createClass(
        name: String, teacherId: Int?, capacity: Int?,
    ): ApiResponse<Classroom> {
        delay(500)
        return ApiResponse.Success(
            Classroom(99, name, null, teacherId, null, 0, capacity, "2024", "2024-03-01")
        )
    }

    override suspend fun updateClass(
        id: Int, name: String?, teacherId: Int?,
    ): ApiResponse<Classroom> {
        delay(500)
        val classroom = MockData.classrooms.find { it.id == id }
            ?: return ApiResponse.Error(404, "الفصل غير موجود")
        return ApiResponse.Success(classroom.copy(name = name ?: classroom.name))
    }

    override suspend fun deleteClass(id: Int): ApiResponse<Unit> {
        delay(500)
        return ApiResponse.Success(Unit)
    }

    override suspend fun getChildrenByClass(
        classId: Int, page: Int,
    ): ApiResponse<PaginatedResult<Child>> {
        delay(500)
        val children = MockData.children.filter { it.classId == classId }
        return ApiResponse.Success(
            PaginatedResult(children, PageMeta(1, 15, children.size, 1))
        )
    }
}

// ══════════════════════════════════════════════════════
//  MOCK USERS REPOSITORY
// ══════════════════════════════════════════════════════
class MockUsersRepository : UsersRepository {

    override suspend fun getUsers(
        role: String?, classId: Int?, search: String?, page: Int,
    ): ApiResponse<PaginatedResult<User>> {
        delay(500)
        var list = MockData.users
        if (role != null) list = list.filter { it.role.name.equals(role, ignoreCase = true) }
        if (search != null) list = list.filter { it.name.contains(search) }
        return ApiResponse.Success(PaginatedResult(list, PageMeta(1, 15, list.size, 1)))
    }

    override suspend fun getUser(id: Int): ApiResponse<User> {
        val user = MockData.users.find { it.id == id }
        return if (user != null) ApiResponse.Success(user)
        else ApiResponse.Error(404, "المستخدم غير موجود")
    }

    override suspend fun createUser(
        name: String, email: String, password: String,
        role: String, phone: String?, classId: Int?,
    ): ApiResponse<User> {
        delay(600)
        return ApiResponse.Success(
            User(
                99,
                name,
                email,
                phone,
                UserRole.valueOf(role.uppercase()),
                null,
                true,
                classId,
                null,
                "2024-03-01"
            )
        )
    }

    override suspend fun updateUser(
        id: Int, name: String?, phone: String?, isActive: Boolean?,
    ): ApiResponse<User> {
        val user = MockData.users.find { it.id == id }
            ?: return ApiResponse.Error(404, "المستخدم غير موجود")
        return ApiResponse.Success(user.copy(name = name ?: user.name))
    }

    override suspend fun deleteUser(id: Int): ApiResponse<Unit> =
        ApiResponse.Success(Unit)

    override suspend fun getProfile(): ApiResponse<User> {
        delay(400)
        return ApiResponse.Success(MockData.users.first())
    }

    override suspend fun updateProfile(name: String, phone: String?): ApiResponse<User> {
        delay(500)
        return ApiResponse.Success(MockData.users.first().copy(name = name, phone = phone))
    }

    override suspend fun changePassword(
        current: String, newPass: String, confirm: String,
    ): ApiResponse<Unit> = ApiResponse.Success(Unit)
}

// ══════════════════════════════════════════════════════
//  MOCK NEWS REPOSITORY
// ══════════════════════════════════════════════════════
class MockNewsRepository : NewsRepository {

    override suspend fun getNews(
        search: String?,
        page: Int
    ): ApiResponse<PaginatedResult<News>> {
        delay(400)
        var list = MockData.news
        if (search != null) list = list.filter { it.title.contains(search) || it.body.contains(search) }
        return ApiResponse.Success(
            PaginatedResult(list, PageMeta(1, 15, list.size, 1))
        )
    }

    override suspend fun getNewsItem(id: Int): ApiResponse<News> {
        val news = MockData.news.find { it.id == id }
        return if (news != null) ApiResponse.Success(news)
        else ApiResponse.Error(404, "الخبر غير موجود")
    }

    override suspend fun createNews(
        title: String, body: String, isPinned: Boolean,
    ): ApiResponse<News> {
        delay(500)
        return ApiResponse.Success(News(99, title, body, null, isPinned, "الإدارة", "٢٠٢٤/٣/٦"))
    }

    override suspend fun updateNews(
        id: Int, title: String?, body: String?,
    ): ApiResponse<News> {
        val news = MockData.news.find { it.id == id }
            ?: return ApiResponse.Error(404, "الخبر غير موجود")
        return ApiResponse.Success(news.copy(title = title ?: news.title))
    }

    override suspend fun deleteNews(id: Int): ApiResponse<Unit> =
        ApiResponse.Success(Unit)
}

// ══════════════════════════════════════════════════════
//  MOCK NOTIFICATIONS REPOSITORY
// ══════════════════════════════════════════════════════
class MockNotificationsRepository : NotificationsRepository {

    private val _notifications = MockData.notifications.toMutableList()

    override suspend fun getNotifications(page: Int): ApiResponse<PaginatedResult<AppNotification>> {
        delay(400)
        return ApiResponse.Success(
            PaginatedResult(_notifications, PageMeta(1, 15, _notifications.size, 1))
        )
    }

    override suspend fun markRead(id: Int): ApiResponse<Unit> {
        val i = _notifications.indexOfFirst { it.id == id }
        if (i >= 0) _notifications[i] = _notifications[i].copy(isRead = true)
        return ApiResponse.Success(Unit)
    }

    override suspend fun markAllRead(): ApiResponse<Unit> {
        for (i in _notifications.indices) {
            _notifications[i] = _notifications[i].copy(isRead = true)
        }
        return ApiResponse.Success(Unit)
    }

    override suspend fun sendNotification(
        title: String, body: String, target: String, classId: Int?,
    ): ApiResponse<Unit> = ApiResponse.Success(Unit)
}

// ══════════════════════════════════════════════════════
//  MOCK CHAT REPOSITORY
// ══════════════════════════════════════════════════════
class MockChatRepository : ChatRepository {

    override suspend fun getConversations(page: Int): ApiResponse<PaginatedResult<Conversation>> {
        delay(400)
        return ApiResponse.Success(
            PaginatedResult(MockData.conversations, PageMeta(1, 15, MockData.conversations.size, 1))
        )
    }

    override suspend fun getMessages(
        conversationId: Int, page: Int,
    ): ApiResponse<PaginatedResult<Message>> {
        delay(300)
        val msgs = MockData.messages.filter { it.conversationId == conversationId }
        return ApiResponse.Success(PaginatedResult(msgs, PageMeta(1, 50, msgs.size, 1)))
    }

    override suspend fun sendMessage(
        conversationId: Int, content: String,
    ): ApiResponse<Message> {
        delay(300)
        return ApiResponse.Success(
            Message(99, conversationId, 2, "سارة أحمد", content, null, false, "الآن")
        )
    }

    override suspend fun startConversation(
        participantId: Int, childId: Int?,
    ): ApiResponse<Conversation> {
        delay(400)
        return ApiResponse.Success(
            Conversation(99, participantId, "محادثة جديدة", null, null, null, null, 0)
        )
    }
}

// ══════════════════════════════════════════════════════
//  MOCK ATTENDANCE REPOSITORY
// ══════════════════════════════════════════════════════
class MockAttendanceRepository : AttendanceRepository {

    override suspend fun createAttendance(
        classId: Int, date: String, records: List<Pair<Int, String>>,
    ): ApiResponse<AttendanceSummary> {
        delay(600)
        val presentCount = records.count { it.second == "present" }
        return ApiResponse.Success(
            AttendanceSummary(
                date = date,
                classId = classId,
                total = records.size,
                present = presentCount,
                absent = records.size - presentCount,
                late = 0,
                presentPct = if (records.isNotEmpty()) presentCount * 100f / records.size else 0f,
            )
        )
    }

    override suspend fun getClassAttendance(
        classId: Int, date: String?, page: Int,
    ): ApiResponse<PaginatedResult<AttendanceSummary>> {
        delay(400)
        return ApiResponse.Success(
            PaginatedResult(emptyList(), PageMeta(1, 15, 0, 1))
        )
    }

    override suspend fun getChildAttendance(
        childId: Int, page: Int,
    ): ApiResponse<PaginatedResult<AttendanceRecord>> {
        delay(400)
        return ApiResponse.Success(
            PaginatedResult(emptyList(), PageMeta(1, 15, 0, 1))
        )
    }
}

// ══════════════════════════════════════════════════════
//  MOCK COMPLAINTS REPOSITORY
// ══════════════════════════════════════════════════════
class MockComplaintsRepository : ComplaintsRepository {

    override suspend fun getComplaints(
        status: String?, page: Int,
    ): ApiResponse<PaginatedResult<Complaint>> {
        delay(400)
        var list = MockData.complaints
        if (status != null) list = list.filter { it.status.name.equals(status, ignoreCase = true) }
        return ApiResponse.Success(PaginatedResult(list, PageMeta(1, 15, list.size, 1)))
    }

    override suspend fun getComplaint(id: Int): ApiResponse<Complaint> {
        val c = MockData.complaints.find { it.id == id }
        return if (c != null) ApiResponse.Success(c)
        else ApiResponse.Error(404, "الشكوى غير موجودة")
    }

    override suspend fun createComplaint(
        title: String, body: String,
    ): ApiResponse<Complaint> {
        delay(500)
        return ApiResponse.Success(
            Complaint(99, title, body, 3, "محمد العلي", ComplaintStatus.PENDING, null, "٢٠٢٤/٣/٦")
        )
    }

    override suspend fun replyToComplaint(
        id: Int, reply: String, status: String,
    ): ApiResponse<Complaint> {
        delay(500)
        val c = MockData.complaints.find { it.id == id }
            ?: return ApiResponse.Error(404, "الشكوى غير موجودة")
        return ApiResponse.Success(c.copy(adminReply = reply))
    }
}

// ══════════════════════════════════════════════════════
//  MOCK GAMES REPOSITORY
// ══════════════════════════════════════════════════════
class MockGamesRepository : GamesRepository {

    override suspend fun getQuestions(
        gameType: String, level: Int,
    ): ApiResponse<List<GameQuestion>> {
        delay(400)
        val questions = when (gameType) {
            "letters" -> MockData.letterQuestions
            "numbers" -> MockData.numberQuestions
            "colors" -> MockData.colorQuestions
            else -> MockData.letterQuestions
        }
        return ApiResponse.Success(questions)
    }

    override suspend fun saveResult(
        childId: Int, gameType: String,
        score: Int, totalQuestions: Int, durationSeconds: Int,
    ): ApiResponse<Unit> = ApiResponse.Success(Unit)
}
