package com.riku.kisanmitra.di

import android.content.Context
import androidx.room.Room
import com.riku.kisanmitra.data.local.AppDatabase
import com.riku.kisanmitra.data.local.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "kisan_mitra_db"
        )
        .fallbackToDestructiveMigration() // Allow destructive migration for demo purposes
        .build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
}
