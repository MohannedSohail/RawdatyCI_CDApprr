package org.mohanned.rawdatyci_cdapp.presentation.navigation

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.SlideTransition
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.mohanned.rawdatyci_cdapp.domain.model.*
import org.mohanned.rawdatyci_cdapp.presentation.screens.auth.*
import org.mohanned.rawdatyci_cdapp.presentation.screens.admin.*
import org.mohanned.rawdatyci_cdapp.presentation.screens.teacher.*
import org.mohanned.rawdatyci_cdapp.presentation.screens.parent.*
import org.mohanned.rawdatyci_cdapp.presentation.screens.parent.studentGames.*
import org.mohanned.rawdatyci_cdapp.presentation.screens.shared.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.*

// ══════════════════════════════════════════════════════
//  ENTRY POINT
// ══════════════════════════════════════════════════════
@Composable
fun AppNavigation() {
    Navigator(SplashVoyagerScreen) { navigator ->
        SlideTransition(navigator)
    }
}

// ══════════════════════════════════════════════════════
//  AUTH SCREENS
// ══════════════════════════════════════════════════════
object SplashVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        SplashScreen {
            navigator.replace(OnboardingVoyagerScreen)
        }
    }
}

object OnboardingVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        OnboardingScreen {
            navigator.replace(UserTypeSelectVoyagerScreen)
        }
    }
}

object UserTypeSelectVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        UserTypeSelectScreen { role ->
            navigator.push(LoginVoyagerScreen(role))
        }
    }
}

data class LoginVoyagerScreen(val role: String = "") : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm = koinViewModel<AuthViewModel>()
        val state by vm.state.collectAsState()

        LaunchedEffect(Unit) {
            vm.effect.collect { effect ->
                when (effect) {
                    is AuthEffect.NavigateToDashboard -> {
                        when (effect.role) {
                            UserRole.ADMIN, UserRole.SUPER_ADMIN ->
                                navigator.replaceAll(AdminDashboardVoyagerScreen)
                            UserRole.TEACHER ->
                                navigator.replaceAll(TeacherHomeVoyagerScreen)
                            UserRole.PARENT ->
                                navigator.replaceAll(ParentHomeVoyagerScreen)
                        }
                    }
                    is AuthEffect.ShowError -> { /* TODO: show snackbar */ }
                }
            }
        }

        LoginScreen(
            identifier       = state.identifier,
            password         = state.password,
            identifierError  = state.identifierError,
            passwordError    = state.passwordError,
            generalError     = state.generalError,
            isLoading        = state.isLoading,
            onIdentifierChange = { vm.onIntent(AuthIntent.IdentifierChanged(it)) },
            onPasswordChange = { vm.onIntent(AuthIntent.PasswordChanged(it)) },
            onSubmit         = { vm.onIntent(AuthIntent.Submit) },
            onForgotPassword = { navigator.push(ForgotPasswordVoyagerScreen) },
        )
    }
}

object ForgotPasswordVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm = koinViewModel<ForgotPasswordViewModel>()
        val state by vm.state.collectAsState()

        LaunchedEffect(Unit) {
            vm.effect.collect { effect ->
                when (effect) {
                    is ForgotPasswordEffect.NavigateToOtp ->
                        navigator.push(OtpVoyagerScreen(effect.email))
                    is ForgotPasswordEffect.ShowError -> { }
                }
            }
        }

        ForgotPasswordScreen(
            email         = state.email,
            emailError    = state.emailError,
            isLoading     = state.isLoading,
            onEmailChange = { vm.onIntent(ForgotPasswordIntent.EmailChanged(it)) },
            onSubmit      = { vm.onIntent(ForgotPasswordIntent.Submit) },
            onBack        = { navigator.pop() },
        )
    }
}

data class OtpVoyagerScreen(val email: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm = koinViewModel<OtpViewModel> { parametersOf(email) }
        val state by vm.state.collectAsState()

        LaunchedEffect(Unit) {
            vm.effect.collect { effect ->
                when (effect) {
                    OtpEffect.NavigateToReset ->
                        navigator.push(ResetPasswordVoyagerScreen(email))
                    is OtpEffect.ShowError -> { }
                }
            }
        }

        // Countdown ticker
        LaunchedEffect(Unit) {
            while (true) {
                kotlinx.coroutines.delay(1000)
                vm.onIntent(OtpIntent.Tick)
            }
        }

        OtpScreen(
            email       = email,
            otp         = state.otp,
            countdown   = state.countdown,
            canResend   = state.canResend,
            isLoading   = state.isLoading,
            error       = state.error,
            onOtpChange = { vm.onIntent(OtpIntent.OtpChanged(it)) },
            onSubmit    = { vm.onIntent(OtpIntent.Submit) },
            onResend    = { vm.onIntent(OtpIntent.Resend) },
            onBack      = { navigator.pop() },
        )
    }
}

data class ResetPasswordVoyagerScreen(val email: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var newPass    by remember { mutableStateOf("") }
        var confirm    by remember { mutableStateOf("") }
        var error      by remember { mutableStateOf<String?>(null) }
        val isLoading  by remember { mutableStateOf(false) }

        ResetPasswordScreen(
            newPassword         = newPass,
            confirmPassword     = confirm,
            isLoading           = isLoading,
            error               = error,
            onNewPasswordChange = { newPass = it; error = null },
            onConfirmChange     = { confirm = it; error = null },
            onSubmit = {
                if (newPass.length < 8) {
                    error = "كلمة المرور 8 أحرف على الأقل"
                    return@ResetPasswordScreen
                }
                if (newPass != confirm) {
                    error = "كلمتا المرور غير متطابقتين"
                    return@ResetPasswordScreen
                }
                navigator.replaceAll(LoginVoyagerScreen())
            },
            onBack = { navigator.pop() },
        )
    }
}

// ══════════════════════════════════════════════════════
//  ADMIN SCREENS
// ══════════════════════════════════════════════════════
object AdminDashboardVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm = koinViewModel<DashboardViewModel>()
        val state by vm.state.collectAsState()

        LaunchedEffect(Unit) { vm.onIntent(DashboardIntent.Load) }

        AdminDashboardScreen(
            totalParents  = state.totalParents,
            totalTeachers = state.totalTeachers,
            totalClasses  = state.totalClasses,
            recentNews    = state.recentNews,
            isLoading     = state.isLoading,
            navIndex      = 0,
            onNavSelect   = { idx: Int ->
                when (idx) {
                    1 -> navigator.push(AdminClassroomsVoyagerScreen)
                    2 -> navigator.push(AdminUsersVoyagerScreen)
                    3 -> navigator.push(SettingsVoyagerScreen)
                }
            },
            onQuickAction = { action: String ->
                when (action) {
                    "الفصول"     -> navigator.push(AdminClassroomsVoyagerScreen)
                    "المستخدمون" -> navigator.push(AdminUsersVoyagerScreen)
                    "التعميمات"  -> navigator.push(AdminNewsVoyagerScreen)
                    "إضافة فصل"  -> navigator.push(AdminAddClassroomVoyagerScreen())
                    "الشكاوى"    -> navigator.push(AdminComplaintsVoyagerScreen)
                }
            },
        )
    }
}

object AdminClassroomsVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm = koinViewModel<ClassroomsViewModel>()
        val state by vm.state.collectAsState()

        LaunchedEffect(Unit) { vm.onIntent(ClassroomsIntent.Load) }

        AdminClassroomsScreen(
            classrooms       = state.classrooms,
            query            = state.query,
            isLoading        = state.isLoading,
            isLoadingMore    = state.isLoadingMore,
            canLoadMore      = state.canLoadMore,
            showDeleteDialog = state.showDeleteDialog,
            onSearch         = { vm.onIntent(ClassroomsIntent.Search(it)) },
            onLoadMore       = { vm.onIntent(ClassroomsIntent.LoadMore) },
            onAdd            = { navigator.push(AdminAddClassroomVoyagerScreen()) },
            onClassClick     = { navigator.push(AdminAddClassroomVoyagerScreen(it)) },
            onDeleteRequest  = { vm.onIntent(ClassroomsIntent.DeleteRequest(it)) },
            onConfirmDelete  = { vm.onIntent(ClassroomsIntent.ConfirmDelete) },
            onDismissDelete  = { vm.onIntent(ClassroomsIntent.DismissDelete) },
            onBack           = { navigator.pop() },
        )
    }
}

data class AdminAddClassroomVoyagerScreen(
    val classroom: Classroom? = null,
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var name     by remember { mutableStateOf(classroom?.name ?: "") }
        var teacher  by remember { mutableStateOf(classroom?.teacherName ?: "") }
        var capacity by remember { mutableStateOf(classroom?.capacity?.toString() ?: "") }

        AdminAddClassroomScreen(
            name            = name,
            teacherName     = teacher,
            capacity        = capacity,
            isEdit          = classroom != null,
            isLoading       = false,
            teachers        = emptyList(),
            onNameChange    = { name = it },
            onTeacherChange = { teacher = it },
            onCapacityChange= { capacity = it },
            onSave          = { navigator.pop() },
            onBack          = { navigator.pop() },
        )
    }
}

object AdminUsersVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm = koinViewModel<UsersViewModel>()
        val state by vm.state.collectAsState()

        LaunchedEffect(Unit) { vm.onIntent(UsersIntent.Load) }

        AdminUsersScreen(
            users         = state.users,
            query         = state.query,
            selectedTab   = state.selectedTab,
            isLoading     = state.isLoading,
            isLoadingMore = state.isLoadingMore,
            canLoadMore   = state.canLoadMore,
            onSearch      = { vm.onIntent(UsersIntent.Search(it)) },
            onLoadMore    = { vm.onIntent(UsersIntent.LoadMore) },
            onTabChange   = { vm.onIntent(UsersIntent.TabChanged(it)) },
            onUserClick   = { },
            onAdd         = { navigator.push(AdminAddUserVoyagerScreen) },
            onBack        = { navigator.pop() },
        )
    }
}

object AdminAddUserVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var name     by remember { mutableStateOf("") }
        var email    by remember { mutableStateOf("") }
        var phone    by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var role     by remember { mutableStateOf("") }

        AdminAddUserScreen(
            name            = name,
            email           = email,
            phone           = phone,
            password        = password,
            role            = role,
            isLoading       = false,
            nameError       = null,
            emailError      = null,
            passwordError   = null,
            onNameChange    = { name = it },
            onEmailChange   = { email = it },
            onPhoneChange   = { phone = it },
            onPasswordChange= { password = it },
            onRoleChange    = { role = it },
            onSave          = { navigator.pop() },
            onBack          = { navigator.pop() },
        )
    }
}

object AdminNewsVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm = koinViewModel<NewsViewModel>()
        val state by vm.state.collectAsState()

        LaunchedEffect(Unit) { vm.onIntent(NewsIntent.Load) }

        AdminNewsScreen(
            news          = state.news,
            query         = state.query,
            isLoading     = state.isLoading,
            isLoadingMore = state.isLoadingMore,
            canLoadMore   = state.canLoadMore,
            onSearch      = { vm.onIntent(NewsIntent.Search(it)) },
            onLoadMore    = { vm.onIntent(NewsIntent.LoadMore) },
            onAdd         = { },
            onEdit        = { },
            onDelete      = { vm.onIntent(NewsIntent.Delete(it)) },
            onBack        = { navigator.pop() },
        )
    }
}

object AdminComplaintsVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm = koinViewModel<ComplaintsViewModel>()
        val state by vm.state.collectAsState()

        LaunchedEffect(Unit) { vm.onIntent(ComplaintsIntent.Load) }

        AdminComplaintsScreen(
            complaints        = state.complaints,
            selectedTab       = state.selectedTab,
            isLoading         = state.isLoading,
            isLoadingMore     = state.isLoadingMore,
            canLoadMore       = state.canLoadMore,
            replyText         = state.replyText,
            showReplyDialog   = state.showReplyDialog,
            selectedComplaint = state.selectedComplaint,
            onTabChange       = { vm.onIntent(ComplaintsIntent.SelectTab(it)) },
            onLoadMore        = { vm.onIntent(ComplaintsIntent.LoadMore) },
            onReplyTextChange = { vm.onIntent(ComplaintsIntent.ReplyChanged(it)) },
            onOpenReply       = { vm.onIntent(ComplaintsIntent.OpenReply(it)) },
            onSubmitReply     = { vm.onIntent(ComplaintsIntent.SubmitReply) },
            onDismissReply    = { vm.onIntent(ComplaintsIntent.DismissReply) },
            onBack            = { navigator.pop() },
        )
    }
}

object AdminSendNotificationVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var title  by remember { mutableStateOf("") }
        var body   by remember { mutableStateOf("") }
        var target by remember { mutableStateOf("all") }

        AdminSendNotificationScreen(
            title         = title,
            body          = body,
            target        = target,
            isLoading     = false,
            titleError    = null,
            bodyError     = null,
            onTitleChange = { title = it },
            onBodyChange  = { body = it },
            onTargetChange= { target = it },
            onSend        = { navigator.pop() },
            onBack        = { navigator.pop() },
        )
    }
}

// ══════════════════════════════════════════════════════
//  TEACHER SCREENS
// ══════════════════════════════════════════════════════
object TeacherHomeVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm = koinViewModel<TeacherHomeViewModel>()
        val state by vm.state.collectAsState()

        LaunchedEffect(Unit) { vm.onIntent(TeacherHomeIntent.Load) }

        TeacherHomeScreen(
            teacherName       = state.teacherName,
            attendanceCount   = state.attendanceSummary,
            attendancePercent = state.attendancePercent,
            taskProgress      = state.taskProgress,
            navIndex          = 0,
            onNavSelect       = { idx: Int ->
                when (idx) {
                    1 -> navigator.push(TeacherAttendanceVoyagerScreen)
                    2 -> navigator.push(TeacherConversationsVoyagerScreen)
                    3 -> navigator.push(ProfileVoyagerScreen)
                }
            },
            onAttendanceClick   = { navigator.push(TeacherAttendanceVoyagerScreen) },
            onMessagesClick     = { navigator.push(TeacherConversationsVoyagerScreen) },
            onClassClick        = { },
        )
    }
}

object TeacherAttendanceVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm = koinViewModel<AttendanceViewModel>()
        val state by vm.state.collectAsState()

        LaunchedEffect(Unit) {
            vm.onIntent(AttendanceIntent.Load(classId = 1, date = "2024-03-01"))
        }

        LaunchedEffect(Unit) {
            vm.effect.collect { effect ->
                when (effect) {
                    is AttendanceEffect.ShowSuccess -> navigator.pop()
                    is AttendanceEffect.ShowError   -> { }
                }
            }
        }

        TeacherAttendanceScreen(
            children      = state.children,
            attendance    = state.attendance,
            classroomName = "فصل الزهور",
            date          = state.date,
            isSaving      = state.isSaving,
            isOffline     = state.isOffline,
            onToggle      = { vm.onIntent(AttendanceIntent.Toggle(it)) },
            onSelectAll   = { vm.onIntent(AttendanceIntent.SelectAll) },
            onSave        = { vm.onIntent(AttendanceIntent.Save) },
            onBack        = { navigator.pop() },
        )
    }
}

object TeacherConversationsVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm = koinViewModel<ChatViewModel>()
        val state by vm.state.collectAsState()

        LaunchedEffect(Unit) {
            vm.onIntent(ChatIntent.LoadConversations)
        }

        LaunchedEffect(Unit) {
            vm.effect.collect { effect ->
                when (effect) {
                    is ChatEffect.NavigateToChat ->
                        navigator.push(ChatRoomVoyagerScreen(effect.id, "", ""))
                    else -> { }
                }
            }
        }

        TeacherConversationsScreen(
            conversations       = state.conversations,
            query               = "",
            isLoading           = state.isLoading,
            isLoadingMore       = state.isLoadingMore,
            canLoadMore         = state.canLoadMore,
            onSearch            = { },
            onLoadMore          = { vm.onIntent(ChatIntent.LoadMoreConversations) },
            onConversationClick = { conv: Conversation ->
                vm.onIntent(ChatIntent.OpenConversation(conv.id))
            },
            onBack              = { navigator.pop() },
        )
    }
}

data class ChatRoomVoyagerScreen(
    val conversationId: Int,
    val recipientName: String,
    val childName: String,
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm = koinViewModel<ChatViewModel>()
        val state by vm.state.collectAsState()

        LaunchedEffect(Unit) {
            vm.onIntent(ChatIntent.LoadMessages(conversationId))
        }

        ChatRoomScreen(
            recipientName = recipientName,
            childName     = childName,
            messages      = state.messages,
            currentUserId = 1, // Current logged user ID
            messageText   = state.messageText,
            isSending     = state.isSending,
            isOnline      = false,
            isLoading     = state.isLoading,
            isLoadingMore = state.isLoadingMore,
            canLoadMore   = state.canLoadMore,
            onTextChange  = { vm.onIntent(ChatIntent.MessageChanged(it)) },
            onSend        = { vm.onIntent(ChatIntent.SendMessage) },
            onLoadMore    = { vm.onIntent(ChatIntent.LoadMoreMessages) },
            onBack        = { navigator.pop() },
        )
    }
}

// ══════════════════════════════════════════════════════
//  PARENT SCREENS
// ══════════════════════════════════════════════════════
object ParentHomeVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm = koinViewModel<ParentHomeViewModel>()
        val state by vm.state.collectAsState()

        LaunchedEffect(Unit) { vm.onIntent(ParentHomeIntent.Load) }

        ParentHomeScreen(
            parentName      = state.parentName,
            childName       = state.children.firstOrNull()?.fullName ?: "طفلي",
            unreadMessages  = state.unreadMessages,
            navIndex        = 0,
            onNavSelect     = { idx: Int ->
                when (idx) {
                    1 -> navigator.push(TeacherConversationsVoyagerScreen)
                    3 -> navigator.push(ProfileVoyagerScreen)
                }
            },
            onChildClick    = { navigator.push(ParentChildDetailVoyagerScreen(state.children.firstOrNull())) },
            onMessagesClick = { navigator.push(TeacherConversationsVoyagerScreen) },
            onEventsClick   = { },
        )
    }
}

data class ParentChildDetailVoyagerScreen(
    val child: Child? = null,
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm = koinViewModel<AttendanceViewModel>()
        val state by vm.state.collectAsState()

        LaunchedEffect(child) {
            child?.let { vm.onIntent(AttendanceIntent.Load(it.classId, "")) }
        }

        ParentChildDetailScreen(
            child            = child ?: Child(0, "...", null, "male", null, 0, "...", null, "...", "...", "...", 0, null),
            attendanceRate   = 92,
            onBack           = { navigator.pop() },
            onAttendanceClick= { },
            onMessageTeacher = {
                navigator.push(ChatRoomVoyagerScreen(1, "المعلم", child?.fullName ?: ""))
            },
        )
    }
}

object ParentComplaintVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm = koinViewModel<ComplaintsViewModel>()
        var title by remember { mutableStateOf("") }
        var body  by remember { mutableStateOf("") }

        LaunchedEffect(Unit) {
            vm.effect.collect { effect ->
                when (effect) {
                    is ComplaintsEffect.ShowSuccess -> navigator.pop()
                    else -> { }
                }
            }
        }

        ParentComplaintScreen(
            title        = title,
            body         = body,
            isLoading    = false,
            titleError   = null,
            bodyError    = null,
            onTitleChange= { title = it },
            onBodyChange = { body = it },
            onSend       = { vm.onIntent(ComplaintsIntent.Submit(title, body)) },
            onBack       = { navigator.pop() },
        )
    }
}

// ══════════════════════════════════════════════════════
//  GAMES SCREENS
// ══════════════════════════════════════════════════════
object GamesHubVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        GamesHubScreen(
            onGameSelect = { type: GameType -> navigator.push(GamePlayVoyagerScreen(type)) },
            onBack       = { navigator.pop() },
        )
    }
}

data class GamePlayVoyagerScreen(val gameType: GameType) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm = koinViewModel<GameViewModel>()
        val state by vm.state.collectAsState()

        LaunchedEffect(Unit) {
            vm.onIntent(GameIntent.Start(gameType))
        }

        LaunchedEffect(Unit) {
            vm.effect.collect { effect ->
                when (effect) {
                    is GameEffect.ShowResult -> navigator.replace(
                        GameResultVoyagerScreen(
                            gameType       = gameType,
                            score          = effect.score,
                            total          = effect.total,
                            stars          = effect.stars,
                            elapsedSeconds = effect.elapsedSecs,
                        )
                    )
                    else -> { }
                }
            }
        }

        GamePlayScreen(
            gameType       = gameType,
            currentQ       = state.currentIndex + 1,
            totalQ         = state.questions.size.coerceAtLeast(1),
            question       = state.currentQuestion,
            selectedOption = state.selectedOption,
            isAnswered     = state.isAnswered,
            isCorrect      = state.isCorrect,
            isLoading      = state.isLoading,
            onSelect       = { vm.onIntent(GameIntent.SelectOption(it)) },
            onCheck        = { vm.onIntent(GameIntent.CheckAnswer) },
            onNext         = { vm.onIntent(GameIntent.NextQuestion) },
            onBack         = { navigator.pop() },
        )
    }
}

data class GameResultVoyagerScreen(
    val gameType: GameType,
    val score: Int,
    val total: Int,
    val stars: Int,
    val elapsedSeconds: Int,
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        GameResultScreen(
            gameType       = gameType,
            score          = score,
            total          = total,
            stars          = stars,
            elapsedSeconds = elapsedSeconds,
            onPlayAgain    = { navigator.replace(GamePlayVoyagerScreen(gameType)) },
            onHome         = { navigator.popUntilRoot() },
        )
    }
}

// ══════════════════════════════════════════════════════
//  SHARED SCREENS
// ══════════════════════════════════════════════════════
object SettingsVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm = koinViewModel<SettingsViewModel>()
        val state by vm.state.collectAsState()
        val profileVm = koinViewModel<ProfileViewModel>()
        val profileState by profileVm.state.collectAsState()

        LaunchedEffect(Unit) {
            vm.onIntent(SettingsIntent.Load)
            profileVm.onIntent(ProfileIntent.Load)
        }

        SettingsScreen(
            userName        = profileState.user?.name ?: "...",
            userRole        = profileState.user?.role?.name?.lowercase() ?: "...",
            isDarkMode      = state.isDarkMode,
            notificationsOn = state.notificationsOn,
            language        = state.language,
            appVersion      = "2.0.0",
            onBack          = { navigator.pop() },
            onEditProfile   = { navigator.push(ProfileVoyagerScreen) },
            onDarkModeToggle= { vm.onIntent(SettingsIntent.ToggleDarkMode(it)) },
            onNotifToggle   = { vm.onIntent(SettingsIntent.ToggleNotifications(it)) },
            onLanguageClick = { /* Show sub-dialog if needed */ },
            onPrivacyClick  = { },
            onHelpClick     = { },
            onLogout        = { profileVm.onIntent(ProfileIntent.Logout) },
        )
    }
}

object ProfileVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm = koinViewModel<ProfileViewModel>()
        val state by vm.state.collectAsState()

        LaunchedEffect(Unit) {
            vm.onIntent(ProfileIntent.Load)
        }

        LaunchedEffect(Unit) {
            vm.effect.collect { effect ->
                when (effect) {
                    ProfileEffect.NavigateToLogin ->
                        navigator.replaceAll(LoginVoyagerScreen())
                    is ProfileEffect.ShowSuccess -> { }
                    is ProfileEffect.ShowError   -> { }
                }
            }
        }

        ProfileScreen(
            user           = state.user,
            name           = state.name,
            phone          = state.phone,
            isLoading      = state.isLoading,
            isSaving       = state.isSaving,
            onBack         = { navigator.pop() },
            onSettingsClick= { navigator.push(SettingsVoyagerScreen) },
            onNameChange   = { vm.onIntent(ProfileIntent.NameChanged(it)) },
            onPhoneChange  = { vm.onIntent(ProfileIntent.PhoneChanged(it)) },
            onSave         = { vm.onIntent(ProfileIntent.Save) },
            onLogout       = { vm.onIntent(ProfileIntent.Logout) },
        )
    }
}

object NotificationsVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm = koinViewModel<NotificationsViewModel>()
        val state by vm.state.collectAsState()

        LaunchedEffect(Unit) { vm.onIntent(NotificationsIntent.Load) }

        NotificationsScreen(
            notifications = state.notifications,
            unreadCount   = state.unreadCount,
            isLoading     = state.isLoading,
            isLoadingMore = state.isLoadingMore,
            canLoadMore   = state.canLoadMore,
            selectedTab   = 0,
            onTabChange   = { },
            onLoadMore    = { vm.onIntent(NotificationsIntent.LoadMore) },
            onMarkRead    = { vm.onIntent(NotificationsIntent.MarkRead(it)) },
            onMarkAllRead = { vm.onIntent(NotificationsIntent.MarkAllRead) },
            onBack        = { navigator.pop() },
        )
    }
}

data class NewsDetailVoyagerScreen(val news: News) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        NewsDetailScreen(
            news       = news,
            onBack     = { navigator.pop() },
            onShare    = { },
            onBookmark = { },
        )
    }
}

object KindergartenInfoVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        KindergartenInfoScreen(
            settings    = null,
            isLoading   = false,
            onBack      = { navigator.pop() },
            onCallClick = { },
            onEmailClick= { },
            onMapClick  = { },
        )
    }
}
