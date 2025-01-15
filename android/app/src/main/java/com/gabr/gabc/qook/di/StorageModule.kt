package com.gabr.gabc.qook.di

import com.gabr.gabc.qook.domain.storage.StorageRepository
import com.gabr.gabc.qook.infrastructure.storage.StorageRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {
    @Binds
    abstract fun bindStorageRepository(repository: StorageRepositoryImpl): StorageRepository
}