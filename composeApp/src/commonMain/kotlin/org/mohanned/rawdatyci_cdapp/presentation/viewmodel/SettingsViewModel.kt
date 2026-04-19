package org.mohanned.rawdatyci_cdapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mohanned.rawdatyci_cdapp.core.util.UiState
import org.mohanned.rawdatyci_cdapp.data.local.AppPreferences
import org.mohanned.rawdatyci_cdapp.domain.model.KindergartenSettings
import org.mohanned.rawdatyci_cdapp.domain.usecase.settings.GetSettingsUseCase
import org.mohanned.rawdatyci_cdapp.domain.usecase.settings.UpdateSettingsUseCase

data class SettingsState(
    val isDarkMode: Boolean = false,
    val notificationsOn: Boolean = true,
    val language: String = "ar",
    val appVersion: String = "1.0.0",
    val settings: KindergartenSettings? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    // Admin Edit Fields
    val kindergartenName: String = "",
    val address: String = "",
    val phone: String = ""
)

sealed class SettingsIntent {
    object Load : SettingsIntent()
    data class ToggleDarkMode(val on: Boolean) : SettingsIntent()
    data class ToggleNotifications(val on: Boolean) : SettingsIntent()
    data class SetLanguage(val lang: String) : SettingsIntent()
    data class KindergartenNameChanged(val v: String) : SettingsIntent()
    data class AddressChanged(val v: String) : SettingsIntent()
    data class PhoneChanged(val v: String) : SettingsIntent()
    object Save : SettingsIntent()
}

sealed class SettingsEffect {
    data class ShowMessage(val message: String) : SettingsEffect()
}

class SettingsViewModel(
    private val prefs: AppPreferences,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private val _effect = Channel<SettingsEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            prefs.darkMode.collect { v -> _state.update { it.copy(isDarkMode = v) } }
        }
        viewModelScope.launch {
            prefs.notificationsOn.collect { v -> _state.update { it.copy(notificationsOn = v) } }
        }
        viewModelScope.launch {
            prefs.language.collect { v -> _state.update { it.copy(language = v) } }
        }
    }

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            SettingsIntent.Load -> loadSettings()
            is SettingsIntent.ToggleDarkMode -> viewModelScope.launch { prefs.setDarkMode(intent.on) }
            is SettingsIntent.ToggleNotifications -> viewModelScope.launch { prefs.setNotifications(intent.on) }
            is SettingsIntent.SetLanguage -> viewModelScope.launch { prefs.setLanguage(intent.lang) }
            is SettingsIntent.KindergartenNameChanged -> _state.update { it.copy(kindergartenName = intent.v) }
            is SettingsIntent.AddressChanged -> _state.update { it.copy(address = intent.v) }
            is SettingsIntent.PhoneChanged -> _state.update { it.copy(phone = intent.v) }
            SettingsIntent.Save -> saveSettings()
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            getSettingsUseCase().collect { uiState ->
                when (uiState) {
                    is UiState.Loading -> _state.update { it.copy(isLoading = true) }
                    is UiState.Success -> {
                        _state.update { it.copy(
                            settings = uiState.data,
                            kindergartenName = uiState.data.kindergartenName,
                            address = uiState.data.address ?: "",
                            phone = uiState.data.phone ?: "",
                            isLoading = false
                        ) }
                    }
                    is UiState.Error -> _state.update { it.copy(error = uiState.message, isLoading = false) }
                }
            }
        }
    }

    private fun saveSettings() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            val current = _state.value
            val updated = KindergartenSettings(
                kindergartenName = current.kindergartenName,
                address = current.address,
                phone = current.phone,
                whatsapp = current.settings?.whatsapp,
                twitter = current.settings?.twitter,
                instagram = current.settings?.instagram,
                mapLat = current.settings?.mapLat,
                mapLng = current.settings?.mapLng,
                academicYear = current.settings?.academicYear ?: ""
            )
            val result = updateSettingsUseCase(updated)
            if (result.isSuccess) {
                _effect.send(SettingsEffect.ShowMessage("تم حفظ الإعدادات بنجاح"))
                loadSettings()
            } else {
                _effect.send(SettingsEffect.ShowMessage(result.exceptionOrNull()?.message ?: "فشل الحفظ"))
            }
            _state.update { it.copy(isSaving = false) }
        }
    }
}
