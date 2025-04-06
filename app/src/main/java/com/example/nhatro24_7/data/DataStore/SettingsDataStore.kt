package com.example.nhatro24_7.data.DataStore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

object SettingsKeys {
    val DARK_MODE = booleanPreferencesKey("dark_mode")
    val LANGUAGE = stringPreferencesKey("language")
}

class SettingsDataStore(private val context: Context) {

    val isDarkMode = context.dataStore.data.map { prefs ->
        prefs[SettingsKeys.DARK_MODE] ?: false
    }

    val selectedLanguage = context.dataStore.data.map { prefs ->
        prefs[SettingsKeys.LANGUAGE] ?: "Tiếng Việt"
    }

    suspend fun saveDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[SettingsKeys.DARK_MODE] = enabled
        }
    }

    suspend fun saveLanguage(language: String) {
        context.dataStore.edit { prefs ->
            prefs[SettingsKeys.LANGUAGE] = language
        }
    }
}
