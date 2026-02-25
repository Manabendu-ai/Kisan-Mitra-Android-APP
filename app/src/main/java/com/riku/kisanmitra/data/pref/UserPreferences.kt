package com.riku.kisanmitra.data.pref

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.riku.kisanmitra.domain.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val LANGUAGE_KEY = stringPreferencesKey("language")
        val ROLE_KEY = stringPreferencesKey("role")
    }

    val language: Flow<String> = dataStore.data.map { it[LANGUAGE_KEY] ?: "English" }
    val role: Flow<UserRole> = dataStore.data.map { 
        val roleStr = it[ROLE_KEY] ?: UserRole.NONE.name
        UserRole.valueOf(roleStr)
    }

    suspend fun saveLanguage(language: String) {
        dataStore.edit { it[LANGUAGE_KEY] = language }
    }

    suspend fun saveRole(role: UserRole) {
        dataStore.edit { it[ROLE_KEY] = role.name }
    }
}
