package org.mohanned.rawdatyci_cdapp.presentation.navigation

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.koin.compose.koinInject
import org.mohanned.rawdatyci_cdapp.core.network.TokenManager
import org.mohanned.rawdatyci_cdapp.domain.model.*
import org.mohanned.rawdatyci_cdapp.presentation.screens.admin.*
import org.mohanned.rawdatyci_cdapp.presentation.screens.auth.*
import org.mohanned.rawdatyci_cdapp.presentation.screens.parent.*
import org.mohanned.rawdatyci_cdapp.presentation.screens.shared.*
import org.mohanned.rawdatyci_cdapp.presentation.screens.teacher.*

@Composable
fun AppNavigation() {
    val tokenManager: TokenManager = koinInject()
    val isLoggedIn by tokenManager.isLoggedInFlow.collectAsState()

    // فحص حالة الجلسة الأولية عند بدء التطبيق
    LaunchedEffect(Unit) {
        tokenManager.checkInitialAuth()
    }

    Navigator(SplashScreen) { navigator ->
        // مراقبة حالة تسجيل الدخول: إذا أصبحت false، نعود فوراً لشاشة اختيار نوع المستخدم
        LaunchedEffect(isLoggedIn) {
            if (isLoggedIn == false) {
                val currentScreen = navigator.lastItem
                // نمنع إعادة التوجيه إذا كنا بالفعل في شاشات المصادقة
                if (currentScreen !is LoginScreen && 
                    currentScreen !is OnboardingScreen && 
                    currentScreen !is UserTypeSelectScreen && 
                    currentScreen !is SplashScreen) {
                    navigator.replaceAll(UserTypeSelectScreen)
                }
            }
        }
        SlideTransition(navigator)
    }
}

fun roleToHome(role: UserRole): Screen = when (role) {
    UserRole.ADMIN, UserRole.SUPER_ADMIN -> AdminDashboardScreen
    UserRole.TEACHER -> TeacherHomeScreen
    UserRole.PARENT -> ParentHomeScreen
}
