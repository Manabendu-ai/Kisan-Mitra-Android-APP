package com.riku.kisanmitra.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riku.kisanmitra.data.pref.UserPreferences
import com.riku.kisanmitra.domain.model.User
import com.riku.kisanmitra.domain.model.UserRole
import com.riku.kisanmitra.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val authRepository: AuthRepository
) : ViewModel() {

    val selectedLanguage: StateFlow<String> = userPreferences.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "English")

    val selectedRole: StateFlow<UserRole> = userPreferences.role
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserRole.NONE)

    val loggedInUser: StateFlow<User?> = authRepository.getLoggedInUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun selectLanguage(language: String) {
        viewModelScope.launch {
            userPreferences.saveLanguage(language)
        }
    }

    fun selectRole(role: UserRole) {
        viewModelScope.launch {
            userPreferences.saveRole(role)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            userPreferences.saveRole(UserRole.NONE)
        }
    }
}
