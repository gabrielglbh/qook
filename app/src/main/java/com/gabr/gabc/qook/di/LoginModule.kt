package com.gabr.gabc.qook.di

import com.gabr.gabc.qook.domain.user.UserRepository
import com.gabr.gabc.qook.infrastructure.user.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LoginModule {
    @Binds
    @Singleton
    abstract fun bindUserRepository(repository: UserRepositoryImpl): UserRepository
}