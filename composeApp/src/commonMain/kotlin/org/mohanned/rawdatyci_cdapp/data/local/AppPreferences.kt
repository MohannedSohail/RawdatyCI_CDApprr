package org.mohanned.rawdatyci_cdapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AppPreferences(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val KEY_TENANT_SLUG = stringPreferencesKey("tenant_slug")
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        private val KEY_USER_ROLE = stringPreferencesKey("user_role")
        private val KEY_AVATAR_URL = stringPreferencesKey("avatar_url")
        private val KEY_DARK_MODE = stringPreferencesKey("dark_mode")
        private val KEY_NOTIFICATIONS = stringPreferencesKey("notifications")
        private val KEY_LANGUAGE = stringPreferencesKey("language")
    }

    suspend fun getAccessToken(): String? = dataStore.data.map { it[KEY_ACCESS_TOKEN] }.first()
    suspend fun getRefreshToken(): String? = dataStore.data.map { it[KEY_REFRESH_TOKEN] }.first()

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        dataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = accessToken
            prefs[KEY_REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun clearTokens() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_ACCESS_TOKEN)
            prefs.remove(KEY_REFRESH_TOKEN)
        }
    }

    suspend fun getTenantSlug(): String? = dataStore.data.map { it[KEY_TENANT_SLUG] }.first()
    suspend fun getUserId(): String? = dataStore.data.map { it[KEY_USER_ID] }.first()
    suspend fun getUserName(): String? = dataStore.data.map { it[KEY_USER_NAME] }.first()
    suspend fun getUserEmail(): String? = dataStore.data.map { it[KEY_USER_EMAIL] }.first()
    suspend fun getUserRole(): String? = dataStore.data.map { it[KEY_USER_ROLE] }.first()
    suspend fun getAvatarUrl(): String? = dataStore.data.map { it[KEY_AVATAR_URL] }.first()

    suspend fun saveUserInfo(
        userId: String,
        name: String,
        email: String,
        role: String,
        avatarUrl: String?,
        tenantSlug: String
    ) {
        dataStore.edit { prefs ->
            prefs[KEY_USER_ID] = userId
            prefs[KEY_USER_NAME] = name
            prefs[KEY_USER_EMAIL] = email
            prefs[KEY_USER_ROLE] = role
            prefs[KEY_AVATAR_URL] = avatarUrl ?: ""
            prefs[KEY_TENANT_SLUG] = tenantSlug
        }
    }

    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }

    val darkMode: Flow<Boolean> = dataStore.data.map { it[KEY_DARK_MODE]?.toBoolean() ?: false }
    val notificationsOn: Flow<Boolean> = dataStore.data.map { it[KEY_NOTIFICATIONS]?.toBoolean() ?: true }
    val language: Flow<String> = dataStore.data.map { it[KEY_LANGUAGE] ?: "ar" }

    suspend fun setDarkMode(on: Boolean) {
        dataStore.edit { it[KEY_DARK_MODE] = on.toString() }
    }

    suspend fun setNotifications(on: Boolean) {
        dataStore.edit { it[KEY_NOTIFICATIONS] = on.toString() }
    }

    suspend fun setLanguage(lang: String) {
        dataStore.edit { it[KEY_LANGUAGE] = lang }
    }
}
