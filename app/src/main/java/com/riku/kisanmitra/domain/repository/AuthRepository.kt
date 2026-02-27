package com.riku.kisanmitra.domain.repository

import com.riku.kisanmitra.domain.model.User
import com.riku.kisanmitra.domain.model.UserRole
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getLoggedInUser(): Flow<User?>
    suspend fun login(phoneNumber: String, role: UserRole): Result<User>
    suspend fun register(name: String, phoneNumber: String, pin: String, role: UserRole): Result<User>
    suspend fun verifyOtp(phoneNumber: String, otp: String): Result<User>
    suspend fun verifyPin(phoneNumber: String, pin: String): Result<User>
    suspend fun logout()
}
