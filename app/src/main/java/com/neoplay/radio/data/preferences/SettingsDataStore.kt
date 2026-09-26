package com.neoplay.radio.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.neoplay.radio.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "neo_radio_settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val apiBaseUrlKey = stringPreferencesKey("api_base_url")
    private val themeKey = stringPreferencesKey("theme")
    private val languageKey = stringPreferencesKey("language")
    private val favoritesKey = stringSetPreferencesKey("favorites_set")

    val apiBaseUrl: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[apiBaseUrlKey] ?: BuildConfig.DEFAULT_API_BASE_URL
    }

    val theme: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[themeKey] ?: "dark"
    }

    val language: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[languageKey] ?: "az"
    }

    val favoriteUuids: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[favoritesKey] ?: emptySet()
    }

    suspend fun setApiBaseUrl(url: String) {
        context.dataStore.edit { prefs ->
            prefs[apiBaseUrlKey] = url
        }
    }

    suspend fun setTheme(themeValue: String) {
        context.dataStore.edit { prefs ->
            prefs[themeKey] = themeValue
        }
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { prefs ->
            prefs[languageKey] = lang
        }
    }

    suspend fun toggleFavorite(uuid: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[favoritesKey]?.toMutableSet() ?: mutableSetOf()
            if (current.contains(uuid)) {
                current.remove(uuid)
            } else {
                current.add(uuid)
            }
            prefs[favoritesKey] = current
        }
    }

    suspend fun clearCache() {
        context.dataStore.edit { prefs ->
            prefs.remove(favoritesKey)
        }
    }
}
