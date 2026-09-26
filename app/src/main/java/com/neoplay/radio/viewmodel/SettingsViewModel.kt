package com.neoplay.radio.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neoplay.radio.data.preferences.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val apiBaseUrl: StateFlow<String> = settingsDataStore.apiBaseUrl.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )

    val theme: StateFlow<String> = settingsDataStore.theme.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "dark"
    )

    val language: StateFlow<String> = settingsDataStore.language.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "az"
    )

    fun setApiBaseUrl(url: String) {
        viewModelScope.launch {
            settingsDataStore.setApiBaseUrl(url)
        }
    }

    fun setTheme(themeValue: String) {
        viewModelScope.launch {
            settingsDataStore.setTheme(themeValue)
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            settingsDataStore.setLanguage(lang)
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            settingsDataStore.clearCache()
        }
    }
}
