package com.riku.kisanmitra.data.repository

import com.riku.kisanmitra.data.local.UserDao
import com.riku.kisanmitra.domain.model.User
import com.riku.kisanmitra.domain.model.UserRole
import com.riku.kisanmitra.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : AuthRepository {

    override fun getLoggedInUser(): Flow<User?> = userDao.getLoggedInUser()

    override suspend fun login(phoneNumber: String, role: UserRole): Result<User> {
        delay(1000) // Simulate network
        // In a real app, we'd check if user exists. 
        // For this demo, we'll return a result so the UI can decide whether to ask for OTP or PIN.
        val user = User(
            id = UUID.randomUUID().toString(),
            name = "Demo User",
            phoneNumber = phoneNumber,
            role = role,
            isLoggedIn = true
        )
        return Result.success(user)
    }

    override suspend fun register(name: String, phoneNumber: String, pin: String, role: UserRole): Result<User> {
        delay(1000) // Simulate network
        val user = User(
            id = UUID.randomUUID().toString(),
            name = name,
            phoneNumber = phoneNumber,
            pin = pin,
            role = role,
            isLoggedIn = true
        )
        userDao.logoutAll()
        userDao.insertUser(user)
        return Result.success(user)
    }

    override suspend fun verifyOtp(phoneNumber: String, otp: String): Result<User> {
        delay(1000) // Simulate network
        if (otp == "123456") {
            val user = User(
                id = UUID.randomUUID().toString(),
                name = "Verified User",
                phoneNumber = phoneNumber,
                role = UserRole.FARMER,
                isLoggedIn = true
            )
            userDao.logoutAll()
            userDao.insertUser(user)
            return Result.success(user)
        } else {
            return Result.failure(Exception("Invalid OTP"))
        }
    }

    override suspend fun verifyPin(phoneNumber: String, pin: String): Result<User> {
        delay(1000)
        // For demo: assume PIN is correct if it matches a stored one or just "1234" for now if not found
        // In a real app, userDao would fetch by phone and check PIN.
        val user = User(
            id = UUID.randomUUID().toString(),
            name = "PIN Verified User",
            phoneNumber = phoneNumber,
            pin = pin,
            role = UserRole.FARMER,
            isLoggedIn = true
        )
        userDao.logoutAll()
        userDao.insertUser(user)
        return Result.success(user)
    }

    override suspend fun logout() {
        userDao.logoutAll()
    }
}
