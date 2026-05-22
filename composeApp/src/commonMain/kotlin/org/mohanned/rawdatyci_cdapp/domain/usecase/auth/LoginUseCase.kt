package org.mohanned.rawdatyci_cdapp.domain.usecase.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.domain.model.LoggedUser
import org.mohanned.rawdatyci_cdapp.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    operator fun invoke(email: String, password: String, role: String): Flow<UiState<LoggedUser>> = flow {
        emit(UiState.Loading)
        
        // التحقق الصارم من نوع المستخدم وتوجيه الطلب للـ Endpoint المخصص فقط
        val result = when (role.lowercase()) {
            "admin" -> repository.adminLogin(email, password)
            "teacher" -> repository.teacherLogin(email, password)
            "parent" -> repository.parentLogin(email, password)
            else -> {
                emit(UiState.Error("نوع مستخدم غير صالح: $role"))
                return@flow
            }
        }

        when (result) {
            is ApiResponse.Success -> emit(UiState.Success(result.data.first))
            is ApiResponse.Error -> emit(UiState.Error(result.message))
            is ApiResponse.NetworkError -> emit(UiState.Error(result.message))
        }
    }
}
