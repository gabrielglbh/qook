package com.gabr.gabc.qook.di

import com.gabr.gabc.qook.application.LoginService
import com.gabr.gabc.qook.domain.user.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object LoginModule {
    @Provides
    fun providesLoginService(repository: UserRepository): LoginService = LoginService(repository)
}