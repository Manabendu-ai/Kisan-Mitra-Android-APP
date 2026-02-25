package com.riku.kisanmitra.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val name: String,
    val phoneNumber: String,
    val role: UserRole,
    val location: String = "",
    val isLoggedIn: Boolean = false
)
