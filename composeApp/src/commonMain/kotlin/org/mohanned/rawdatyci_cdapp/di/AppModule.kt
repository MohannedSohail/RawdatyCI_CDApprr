package org.mohanned.rawdatyci_cdapp.di

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module
import org.mohanned.rawdatyci_cdapp.core.network.TokenManager
import org.mohanned.rawdatyci_cdapp.core.network.buildHttpClient
import org.mohanned.rawdatyci_cdapp.data.local.AppPreferences
import org.mohanned.rawdatyci_cdapp.data.remote.api.AttendanceApiService
import org.mohanned.rawdatyci_cdapp.data.remote.api.AttendanceApiServiceImpl
import org.mohanned.rawdatyci_cdapp.data.remote.api.AuthApiService
import org.mohanned.rawdatyci_cdapp.data.remote.api.AuthApiServiceImpl
import org.mohanned.rawdatyci_cdapp.data.remote.api.ChatApiService
import org.mohanned.rawdatyci_cdapp.data.remote.api.ChatApiServiceImpl
import org.mohanned.rawdatyci_cdapp.data.remote.api.ChildrenApiService
import org.mohanned.rawdatyci_cdapp.data.remote.api.ChildrenApiServiceImpl
import org.mohanned.rawdatyci_cdapp.data.remote.api.ClassesApiService
import org.mohanned.rawdatyci_cdapp.data.remote.api.ClassesApiServiceImpl
import org.mohanned.rawdatyci_cdapp.data.remote.api.ComplaintsApiService
import org.mohanned.rawdatyci_cdapp.data.remote.api.ComplaintsApiServiceImpl
import org.mohanned.rawdatyci_cdapp.data.remote.api.GamesApiService
import org.mohanned.rawdatyci_cdapp.data.remote.api.GamesApiServiceImpl
import org.mohanned.rawdatyci_cdapp.data.remote.api.NewsApiService
import org.mohanned.rawdatyci_cdapp.data.remote.api.NewsApiServiceImpl
import org.mohanned.rawdatyci_cdapp.data.remote.api.NotificationsApiService
import org.mohanned.rawdatyci_cdapp.data.remote.api.NotificationsApiServiceImpl
import org.mohanned.rawdatyci_cdapp.data.remote.api.SettingsApiService
import org.mohanned.rawdatyci_cdapp.data.remote.api.SettingsApiServiceImpl
import org.mohanned.rawdatyci_cdapp.data.remote.api.UsersApiService
import org.mohanned.rawdatyci_cdapp.data.remote.api.UsersApiServiceImpl
import org.mohanned.rawdatyci_cdapp.data.repository.AttendanceRepositoryImpl
import org.mohanned.rawdatyci_cdapp.data.repository.AuthRepositoryImpl
import org.mohanned.rawdatyci_cdapp.data.repository.ChatRepositoryImpl
import org.mohanned.rawdatyci_cdapp.data.repository.ChildrenRepositoryImpl
import org.mohanned.rawdatyci_cdapp.data.repository.ClassesRepositoryImpl
import org.mohanned.rawdatyci_cdapp.data.repository.ComplaintsRepositoryImpl
import org.mohanned.rawdatyci_cdapp.data.repository.GamesRepositoryImpl
import org.mohanned.rawdatyci_cdapp.data.repository.NewsRepositoryImpl
import org.mohanned.rawdatyci_cdapp.data.repository.NotificationsRepositoryImpl
import org.mohanned.rawdatyci_cdapp.data.repository.SettingsRepositoryImpl
import org.mohanned.rawdatyci_cdapp.data.repository.UsersRepositoryImpl
import org.mohanned.rawdatyci_cdapp.domain.repository.AttendanceRepository
import org.mohanned.rawdatyci_cdapp.domain.repository.AuthRepository
import org.mohanned.rawdatyci_cdapp.domain.repository.ChatRepository
import org.mohanned.rawdatyci_cdapp.domain.repository.ChildrenRepository
import org.mohanned.rawdatyci_cdapp.domain.repository.ClassesRepository
import org.mohanned.rawdatyci_cdapp.domain.repository.ComplaintsRepository
import org.mohanned.rawdatyci_cdapp.domain.repository.GamesRepository
import org.mohanned.rawdatyci_cdapp.domain.repository.NewsRepository
import org.mohanned.rawdatyci_cdapp.domain.repository.NotificationsRepository
import org.mohanned.rawdatyci_cdapp.domain.repository.SettingsRepository
import org.mohanned.rawdatyci_cdapp.domain.repository.UsersRepository
import org.mohanned.rawdatyci_cdapp.domain.usecase.attendance.CreateAttendanceUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.attendance.GetChildAttendanceUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.attendance.GetClassAttendanceUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.attendance.GetMonthlyAttendanceReportUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.attendance.UpdateAttendanceRecordUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.auth.ForgotPasswordUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.auth.LoginUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.auth.LogoutUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.auth.ResetPasswordUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.auth.VerifyOtpUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.chat.GetConversationsUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.chat.GetMessagesUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.chat.SendMessageUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.chat.StartConversationUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.child.CreateChildUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.child.GetChildrenByClassUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.child.GetMyChildrenUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.child.UpdateChildUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.classroom.CreateClassUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.classroom.DeleteClassUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.classroom.GetClassByIdUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.classroom.GetClassesUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.classroom.UpdateClassUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.complaint.CreateComplaintUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.complaint.GetComplaintByIdUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.complaint.GetComplaintsUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.complaint.ReplyToComplaintUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.game.GetChildGameHistoryUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.game.GetGameQuestionsUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.game.SaveGameResultUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.game.UpdateGameQuestionUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.news.CreateNewsUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.news.DeleteNewsUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.news.GetNewsByIdUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.news.GetNewsUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.news.UpdateNewsUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.notification.GetNotificationsUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.notification.MarkAllNotificationsReadUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.notification.MarkNotificationReadUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.notification.SendNotificationUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.profile.ChangePasswordUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.profile.GetProfileUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.profile.UpdateProfileUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.settings.GetSettingsUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.settings.UpdateSettingsUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.user.CreateUserUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.user.DeleteUserUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.user.GetUserByIdUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.user.GetUsersUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.user.UpdateUserUseCase
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AdminAddEditClassroomViewModel
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AdminAddEditNewsViewModel
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AdminAddEditUserViewModel
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AttendanceViewModel
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.AuthViewModel
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ChatViewModel
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ChildrenViewModel
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ClassroomsViewModel
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ComplaintsViewModel
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.DashboardViewModel
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ForgotPasswordViewModel
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.GameViewModel
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.NewsViewModel
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.NotificationsViewModel
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.OtpViewModel
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ParentHomeViewModel
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.ProfileViewModel
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.SettingsViewModel
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.TeacherHomeViewModel
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.UsersViewModel

fun coreModule() = module {
    single { AppPreferences(get()) }
    single { TokenManager(get()) }
    single<HttpClient> { buildHttpClient(get()) }
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults =
                false
        }
    }
}

val repositoryModule = module {
    single<AuthApiService> { AuthApiServiceImpl(get()) }
    single<UsersApiService> { UsersApiServiceImpl(get()) }
    single<ClassesApiService> { ClassesApiServiceImpl(get()) }
    single<ChildrenApiService> { ChildrenApiServiceImpl(get()) }
    single<AttendanceApiService> { AttendanceApiServiceImpl(get()) }
    single<ChatApiService> { ChatApiServiceImpl(get()) }
    single<NewsApiService> { NewsApiServiceImpl(get()) }
    single<ComplaintsApiService> { ComplaintsApiServiceImpl(get()) }
    single<NotificationsApiService> { NotificationsApiServiceImpl(get()) }
    single<GamesApiService> { GamesApiServiceImpl(get()) }
    single<SettingsApiService> { SettingsApiServiceImpl(get()) }

    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<UsersRepository> { UsersRepositoryImpl(get(), get()) }
    single<ClassesRepository> { ClassesRepositoryImpl(get()) }
    single<ChildrenRepository> { ChildrenRepositoryImpl(get(), get()) }
    single<AttendanceRepository> { AttendanceRepositoryImpl(get()) }
    single<ChatRepository> { ChatRepositoryImpl(get()) }
    single<NewsRepository> { NewsRepositoryImpl(get()) }
    single<ComplaintsRepository> { ComplaintsRepositoryImpl(get()) }
    single<NotificationsRepository> { NotificationsRepositoryImpl(get()) }
    single<GamesRepository> { GamesRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
}

val viewModelModule = module {
    // UseCases
    single { LoginUseCase(get()) }
    single { LogoutUseCase(get()) }
    single { ForgotPasswordUseCase(get()) }
    single { VerifyOtpUseCase(get()) }
    single { ResetPasswordUseCase(get()) }
    single { GetUsersUseCase(get()) }
    single { GetUserByIdUseCase(get()) }
    single { CreateUserUseCase(get()) }
    single { UpdateUserUseCase(get()) }
    single { DeleteUserUseCase(get()) }
    single { GetProfileUseCase(get()) }
    single { UpdateProfileUseCase(get()) }
    single { ChangePasswordUseCase(get()) }
    single { GetClassesUseCase(get()) }
    single { GetClassByIdUseCase(get()) }
    single { CreateClassUseCase(get()) }
    single { UpdateClassUseCase(get()) }
    single { DeleteClassUseCase(get()) }
    single { GetChildrenByClassUseCase(get()) }
    single { GetMyChildrenUseCase(get()) }
    single { CreateChildUseCase(get()) }
    single { UpdateChildUseCase(get()) }
    single { CreateAttendanceUseCase(get()) }
    single { UpdateAttendanceRecordUseCase(get()) }
    single { GetClassAttendanceUseCase(get()) }
    single { GetChildAttendanceUseCase(get()) }
    single { GetMonthlyAttendanceReportUseCase(get()) }
    single { GetConversationsUseCase(get()) }
    single { StartConversationUseCase(get()) }
    single { GetMessagesUseCase(get()) }
    single { SendMessageUseCase(get()) }
    single { GetNewsUseCase(get()) }
    single { GetNewsByIdUseCase(get()) }
    single { CreateNewsUseCase(get()) }
    single { UpdateNewsUseCase(get()) }
    single { DeleteNewsUseCase(get()) }
    single { GetComplaintsUseCase(get()) }
    single { GetComplaintByIdUseCase(get()) }
    single { CreateComplaintUseCase(get()) }
    single { ReplyToComplaintUseCase(get()) }
    single { GetNotificationsUseCase(get()) }
    single { MarkNotificationReadUseCase(get()) }
    single { MarkAllNotificationsReadUseCase(get()) }
    single { SendNotificationUseCase(get()) }
    single { GetGameQuestionsUseCase(get()) }
    single { SaveGameResultUseCase(get()) }
    single { GetChildGameHistoryUseCase(get()) }
    single { UpdateGameQuestionUseCase(get()) }
    single { GetSettingsUseCase(get()) }
    single { UpdateSettingsUseCase(get()) }

    // ViewModels
    factory { AuthViewModel(get(), get(), get()) }
    factory { ForgotPasswordViewModel(get(), get()) }
    factory { (email: String) -> OtpViewModel(get(), get(), email) }
    factory { DashboardViewModel(get(), get(), get(), get(), get(), get()) }
    factory { ClassroomsViewModel(get(), get(), get(), get(), get(), get(), get()) }
    factory { UsersViewModel(get(), get(), get(), get(), get()) }
    factory { ProfileViewModel(get(), get(), get(), get()) }
    factory { ChildrenViewModel(get(), get(), get()) }
    factory { AttendanceViewModel(
        get(), get(), get(), get(), get(), get()
    ) }
    factory { ChatViewModel(get(), get(), get(), get()) }
    factory { NewsViewModel(get(), get(), get(), get(), get()) }
    factory { ComplaintsViewModel(get(), get(), get(), get()) }
    factory { NotificationsViewModel(get(), get(), get(), get()) }
    factory { GameViewModel(get(), get(), get()) }
    factory { SettingsViewModel(get(), get(), get()) }
    factory { TeacherHomeViewModel(get(), get(), get()) }
    factory { ParentHomeViewModel(get(), get()) }
    factory { AdminAddEditUserViewModel(get(), get(), get(), get()) }
    factory { AdminAddEditClassroomViewModel(get(), get(), get(), get(), get()) }
    factory { AdminAddEditNewsViewModel(get(), get(), get()) }
}

expect fun platformModule(): Module
