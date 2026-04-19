package org.mohanned.rawdatyci_cdapp.di

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module
import org.mohanned.rawdatyci_cdapp.core.network.*
import org.mohanned.rawdatyci_cdapp.data.local.AppPreferences
import org.mohanned.rawdatyci_cdapp.data.remote.api.*
import org.mohanned.rawdatyci_cdapp.data.repository.*
import org.mohanned.rawdatyci_cdapp.domain.repository.*
import org.mohanned.rawdatyci_cdapp.domain.usecase.auth.*
import org.mohanned.rawdatyci_cdapp.domain.usecase.user.*
import org.mohanned.rawdatyci_cdapp.domain.usecase.profile.*
import org.mohanned.rawdatyci_cdapp.domain.usecase.classroom.*
import org.mohanned.rawdatyci_cdapp.domain.usecase.child.*
import org.mohanned.rawdatyci_cdapp.domain.usecase.attendance.*
import org.mohanned.rawdatyci_cdapp.domain.usecase.chat.*
import org.mohanned.rawdatyci_cdapp.domain.usecase.news.*
import org.mohanned.rawdatyci_cdapp.domain.usecase.complaint.*
import org.mohanned.rawdatyci_cdapp.domain.usecase.notification.*
import org.mohanned.rawdatyci_cdapp.domain.usecase.game.*
import org.mohanned.rawdatyci_cdapp.domain.usecase.settings.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.*

fun coreModule() = module {
    single { AppPreferences(get()) }
    single { TokenManager(get()) }
    single<HttpClient> { buildHttpClient(get()) }
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
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
    single<ChildrenRepository> { ChildrenRepositoryImpl(get()) }
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
    factory { ClassroomsViewModel(get(), get(), get(), get(), get(), get()) }
    factory { UsersViewModel(get(), get(), get(), get(), get()) }
    factory { ProfileViewModel(get(), get(), get(), get()) }
    factory { ChildrenViewModel(get(), get(), get()) }
    factory { AttendanceViewModel(get(), get(), get(), get(), get()) }
    factory { ChatViewModel(get(), get(), get(), get()) }
    factory { NewsViewModel(get(), get(), get(), get(), get()) }
    factory { ComplaintsViewModel(get(), get(), get(), get()) }
    factory { NotificationsViewModel(get(), get(), get(), get()) }
    factory { GameViewModel(get(), get(), get()) }
    factory { SettingsViewModel(get(), get(), get()) }
    factory { TeacherHomeViewModel(get(), get()) }
    factory { ParentHomeViewModel(get(), get()) }
    factory { AdminAddEditUserViewModel(get(), get(), get()) }
    factory { AdminAddEditClassroomViewModel(get(), get(), get(), get(), get()) }
    factory { AdminAddEditNewsViewModel(get(), get(), get()) }
}

expect fun platformModule(): Module
