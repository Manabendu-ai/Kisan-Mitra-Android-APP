package com.riku.kisanmitra.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.riku.kisanmitra.domain.model.User

@Database(entities = [User::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
