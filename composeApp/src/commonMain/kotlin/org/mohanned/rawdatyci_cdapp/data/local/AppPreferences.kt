package org.mohanned.rawdatyci_cdapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.mohanned.rawdatyci_cdapp.core.network.remote.TokenStorage

class AppPreferences(
    private val dataStore: DataStore<Preferences>,
) : TokenStorage {

    // ── Keys ──────────────────────────────────────────
    private object Keys {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = intPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_ROLE = stringPreferencesKey("user_role")
        val TENANT_SLUG = stringPreferencesKey("tenant_slug")
        val LANGUAGE = stringPreferencesKey("language")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val NOTIFICATIONS_ON = booleanPreferencesKey("notifications_on")
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val FCM_TOKEN = stringPreferencesKey("fcm_token")
    }

    // ── TokenStorage Implementation ───────────────────
    override suspend fun getAccessToken(): String? =
        dataStore.data.first()[Keys.ACCESS_TOKEN]

    override suspend fun getRefreshToken(): String? =
        dataStore.data.first()[Keys.REFRESH_TOKEN]

    override suspend fun saveTokens(
        access: String,
        refresh: String,
    ) {
        dataStore.edit {
            it[Keys.ACCESS_TOKEN] = access
            it[Keys.REFRESH_TOKEN] = refresh
        }
    }

    override suspend fun clearTokens() {
        dataStore.edit {
            it.remove(Keys.ACCESS_TOKEN)
            it.remove(Keys.REFRESH_TOKEN)
        }
    }

    // ── User Info ─────────────────────────────────────
    suspend fun saveUserInfo(
        id: Int,
        name: String,
        email: String,
        role: String,
        tenantSlug: String,
    ) {
        dataStore.edit {
            it[Keys.USER_ID] = id
            it[Keys.USER_NAME] = name
            it[Keys.USER_EMAIL] = email
            it[Keys.USER_ROLE] = role
            it[Keys.TENANT_SLUG] = tenantSlug
        }
    }

    suspend fun getUserId(): Int? =
        dataStore.data.first()[Keys.USER_ID]

    suspend fun getUserName(): String? =
        dataStore.data.first()[Keys.USER_NAME]

    suspend fun getUserEmail(): String? =
        dataStore.data.first()[Keys.USER_EMAIL]

    suspend fun getUserRole(): String? =
        dataStore.data.first()[Keys.USER_ROLE]

    suspend fun getTenantSlug(): String? =
        dataStore.data.first()[Keys.TENANT_SLUG]

    // ── Settings as Flow ──────────────────────────────
    val language: Flow<String> =
        dataStore.data.map { it[Keys.LANGUAGE] ?: "ar" }

    val darkMode: Flow<Boolean> =
        dataStore.data.map { it[Keys.DARK_MODE] ?: false }

    val notificationsOn: Flow<Boolean> =
        dataStore.data.map { it[Keys.NOTIFICATIONS_ON] ?: true }

    val onboardingDone: Flow<Boolean> =
        dataStore.data.map { it[Keys.ONBOARDING_DONE] ?: false }

    // ── Settings Setters ──────────────────────────────
    suspend fun setLanguage(lang: String) =
        dataStore.edit { it[Keys.LANGUAGE] = lang }

    suspend fun setDarkMode(on: Boolean) =
        dataStore.edit { it[Keys.DARK_MODE] = on }

    suspend fun setNotificationsOn(on: Boolean) =
        dataStore.edit { it[Keys.NOTIFICATIONS_ON] = on }

    suspend fun setOnboardingDone() =
        dataStore.edit { it[Keys.ONBOARDING_DONE] = true }

    // ── FCM Token ─────────────────────────────────────
    suspend fun saveFcmToken(token: String) =
        dataStore.edit { it[Keys.FCM_TOKEN] = token }

    suspend fun getFcmToken(): String? =
        dataStore.data.first()[Keys.FCM_TOKEN]

    // ── Clear All (Logout) ────────────────────────────
    suspend fun clearAll() =
        dataStore.edit { it.clear() }
}