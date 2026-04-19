package org.mohanned.rawdatyci_cdapp.core.base

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.core.util.UiState

abstract class BaseUseCase<in P, out R> {
    abstract suspend fun execute(parameters: P): ApiResponse<R>

    operator fun invoke(parameters: P): Flow<UiState<R>> = flow {
        emit(UiState.Loading)
        when (val result = execute(parameters)) {
            is ApiResponse.Success -> emit(UiState.Success(result.data))
            is ApiResponse.Error -> emit(UiState.Error(result.message))
            is ApiResponse.NetworkError -> emit(UiState.Error(result.message))
        }
    }.catch { e ->
        emit(UiState.Error(e.message ?: "Unknown Error"))
    }
}
