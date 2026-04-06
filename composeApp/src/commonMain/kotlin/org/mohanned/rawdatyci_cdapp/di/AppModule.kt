@file:JvmName("CommonAppModuleKt")
package org.mohanned.rawdatyci_cdapp.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module
import org.mohanned.rawdatyci_cdapp.core.network.remote.DriverFactory
import org.mohanned.rawdatyci_cdapp.core.network.remote.TokenStorage
import org.mohanned.rawdatyci_cdapp.core.network.remote.buildHttpClient
import org.mohanned.rawdatyci_cdapp.data.local.AppPreferences
import com.rawdaty.db.RawdatyDatabase
import org.mohanned.rawdatyci_cdapp.data.MockAttendanceRepository
import org.mohanned.rawdatyci_cdapp.data.MockAuthRepository
import org.mohanned.rawdatyci_cdapp.data.MockChatRepository
import org.mohanned.rawdatyci_cdapp.data.MockClassesRepository
import org.mohanned.rawdatyci_cdapp.data.MockComplaintsRepository
import org.mohanned.rawdatyci_cdapp.data.MockGamesRepository
import org.mohanned.rawdatyci_cdapp.data.MockNewsRepository
import org.mohanned.rawdatyci_cdapp.data.MockNotificationsRepository
import org.mohanned.rawdatyci_cdapp.data.MockUsersRepository
import org.mohanned.rawdatyci_cdapp.data.local.createDataStore
import org.mohanned.rawdatyci_cdapp.data.remote.api.*
import org.mohanned.rawdatyci_cdapp.domain.repository.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.*
import kotlin.jvm.JvmName

fun commonModule() = module {
    single<Json> {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
        }
    }

    single<TokenStorage> { get<AppPreferences>() }

    single<DataStore<Preferences>> { createDataStore() }
    single<AppPreferences> { AppPreferences(get()) }

    single<HttpClient> { buildHttpClient(get()) }
    
    // Database
    single<RawdatyDatabase> { 
        val driverFactory: DriverFactory = get()
        RawdatyDatabase(driverFactory.createDriver())
    }

    // API Services
    single<AuthApiService> { AuthApiService(get()) }
    single<ClassesApiService> { ClassesApiService(get()) }
    single<AttendanceApiService> { AttendanceApiService(get()) }
    single<ChatApiService> { ChatApiService(get()) }
    single<NewsApiService> { NewsApiService(get()) }
    single<ComplaintsApiService> { ComplaintsApiService(get()) }
    single<NotificationsApiService> { NotificationsApiService(get()) }
    single<GamesApiService> { GamesApiService(get()) }
    single<UsersApiService> { UsersApiService(get()) }
}


val repositoryModule = module {
    single<AuthRepository>          { MockAuthRepository() }
    single<UsersRepository>         { MockUsersRepository() }
    single<ClassesRepository>       { MockClassesRepository() }
    single<AttendanceRepository>    { MockAttendanceRepository() }
    single<ChatRepository>          { MockChatRepository() }
    single<NewsRepository>          { MockNewsRepository() }
    single<ComplaintsRepository>    { MockComplaintsRepository() }
    single<NotificationsRepository> { MockNotificationsRepository() }
    single<GamesRepository>         { MockGamesRepository() }
}

//val repositoryModule = module {
//    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
//    single<ClassesRepository> { ClassesRepositoryImpl(get(), get()) }
//    single<AttendanceRepository> { AttendanceRepositoryImpl(get(), get()) }
//    single<ChatRepository> { ChatRepositoryImpl(get(), get()) }
//    single<NewsRepository> { NewsRepositoryImpl(get(), get()) }
//    single<ComplaintsRepository> { ComplaintsRepositoryImpl(get(), get()) }
//    single<NotificationsRepository> { NotificationsRepositoryImpl(get(), get()) }
//    single<GamesRepository> { GamesRepositoryImpl(get()) }
//    single<UsersRepository> { UsersRepositoryImpl(get(), get()) }
//    single<SyncRepository> { SyncRepository(get(), get(), get(), get()) }
//    single<SyncManager> { SyncManager(get(), get()) }
//}

val viewModelModule = module {
    factory<AuthViewModel> { AuthViewModel(get(), get()) }
    factory<ForgotPasswordViewModel> { ForgotPasswordViewModel(get()) }
    factory<OtpViewModel> { (email: String) -> OtpViewModel(get(), email) }
    factory<DashboardViewModel> { DashboardViewModel(get(), get(), get(), get()) }
    factory<ClassroomsViewModel> { ClassroomsViewModel(get()) }
    factory<UsersViewModel> { UsersViewModel(get()) }
    factory<NewsViewModel> { NewsViewModel(get()) }
    factory<ComplaintsViewModel> { ComplaintsViewModel(get()) }
    factory<AttendanceViewModel> { AttendanceViewModel(get(), get()) }
    factory<ChatViewModel> { ChatViewModel(get()) }
    factory<ProfileViewModel> { ProfileViewModel(get(), get()) }
    factory<NotificationsViewModel> { NotificationsViewModel(get()) }
    factory<TeacherHomeViewModel> { TeacherHomeViewModel(get(), get(), get()) }
    factory<ParentHomeViewModel> { ParentHomeViewModel(get(), get(), get()) }
    factory<SettingsViewModel> { SettingsViewModel(get()) }
    factory<GameViewModel> { GameViewModel(get()) }
}

expect fun platformModule(): Module
