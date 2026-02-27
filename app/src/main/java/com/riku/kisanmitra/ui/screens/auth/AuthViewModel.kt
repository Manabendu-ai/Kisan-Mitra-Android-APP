package com.riku.kisanmitra.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riku.kisanmitra.domain.model.User
import com.riku.kisanmitra.domain.model.UserRole
import com.riku.kisanmitra.domain.repository.AuthRepository
import com.riku.kisanmitra.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<UiState<User>?>(null)
    val authState: StateFlow<UiState<User>?> = _authState

    fun login(phoneNumber: String, role: UserRole) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            authRepository.login(phoneNumber, role)
                .onSuccess { _authState.value = UiState.Success(it) }
                .onFailure { _authState.value = UiState.Error(it.message ?: "Login failed") }
        }
    }

    fun register(name: String, phoneNumber: String, pin: String, role: UserRole) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            authRepository.register(name, phoneNumber, pin, role)
                .onSuccess { _authState.value = UiState.Success(it) }
                .onFailure { _authState.value = UiState.Error(it.message ?: "Registration failed") }
        }
    }

    fun verifyOtp(phoneNumber: String, otp: String) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            authRepository.verifyOtp(phoneNumber, otp)
                .onSuccess { _authState.value = UiState.Success(it) }
                .onFailure { _authState.value = UiState.Error(it.message ?: "Invalid OTP") }
        }
    }

    fun verifyPin(phoneNumber: String, pin: String) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            authRepository.verifyPin(phoneNumber, pin)
                .onSuccess { _authState.value = UiState.Success(it) }
                .onFailure { _authState.value = UiState.Error(it.message ?: "Invalid PIN") }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _authState.value = null
        }
    }
}
