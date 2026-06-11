package com.example.appdecontroledepedidoseclientes.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdecontroledepedidoseclientes.data.datastore.SettingsDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsDataStore: SettingsDataStore) : ViewModel() {

    val isDarkMode = settingsDataStore.isDarkMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val userName = settingsDataStore.userName.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ""
    )

    val notificationsEnabled = settingsDataStore.notificationsEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    fun saveDarkMode(isDark: Boolean) = viewModelScope.launch {
        settingsDataStore.saveDarkMode(isDark)
    }

    fun saveUserName(name: String) = viewModelScope.launch {
        settingsDataStore.saveUserName(name)
    }

    fun saveNotifications(enabled: Boolean) = viewModelScope.launch {
        settingsDataStore.saveNotifications(enabled)
    }
}

