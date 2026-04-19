package org.mohanned.rawdatyci_cdapp.presentation.navigation

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.SlideTransition
import org.koin.compose.viewmodel.koinViewModel
import org.mohanned.rawdatyci_cdapp.domain.model.*
import org.mohanned.rawdatyci_cdapp.presentation.screens.admin.*
import org.mohanned.rawdatyci_cdapp.presentation.screens.auth.*
import org.mohanned.rawdatyci_cdapp.presentation.screens.parent.studentGames.*
import org.mohanned.rawdatyci_cdapp.presentation.screens.parent.*
import org.mohanned.rawdatyci_cdapp.presentation.screens.shared.*
import org.mohanned.rawdatyci_cdapp.presentation.screens.teacher.*
import org.mohanned.rawdatyci_cdapp.presentation.viewmodel.*

@Composable
fun AppNavigation() {
    Navigator(SplashScreen) { SlideTransition(it) }
}

fun roleToHome(role: UserRole): Screen = when (role) {
    UserRole.ADMIN, UserRole.SUPER_ADMIN -> AdminDashboardScreen
    UserRole.TEACHER -> TeacherHomeScreen
    UserRole.PARENT -> ParentHomeScreen
}
