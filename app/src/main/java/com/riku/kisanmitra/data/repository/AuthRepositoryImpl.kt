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
        val user = User(
            id = UUID.randomUUID().toString(),
            name = "Demo User",
            phoneNumber = phoneNumber,
            role = role,
            isLoggedIn = true
        )
        userDao.logoutAll()
        userDao.insertUser(user)
        return Result.success(user)
    }

    override suspend fun register(name: String, phoneNumber: String, role: UserRole): Result<User> {
        delay(1000) // Simulate network
        val user = User(
            id = UUID.randomUUID().toString(),
            name = name,
            phoneNumber = phoneNumber,
            role = role,
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
