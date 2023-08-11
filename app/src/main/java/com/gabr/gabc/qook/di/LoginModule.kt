package com.gabr.gabc.qook.di

import com.gabr.gabc.qook.domain.user.UserRepository
import com.gabr.gabc.qook.infrastructure.user.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class LoginModule {
    @Binds
    @ViewModelScoped
    abstract fun bindUserRepository(repository: UserRepositoryImpl): UserRepository
}