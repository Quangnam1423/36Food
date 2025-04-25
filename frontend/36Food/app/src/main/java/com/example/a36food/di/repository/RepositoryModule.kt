package com.example.a36food.di.repository

import com.example.a36food.data.local.UserPreferences
import com.example.a36food.data.network.services.UserService
import com.example.a36food.data.repository.UserRepository
import com.example.a36food.data.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
}