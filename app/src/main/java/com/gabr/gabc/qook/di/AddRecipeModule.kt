package com.gabr.gabc.qook.di

import com.gabr.gabc.qook.domain.recipe.RecipeRepository
import com.gabr.gabc.qook.domain.tag.TagRepository
import com.gabr.gabc.qook.infrastructure.recipe.RecipeRepositoryImpl
import com.gabr.gabc.qook.infrastructure.tag.TagRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class AddRecipeModule {
    @Binds
    @ViewModelScoped
    abstract fun bindTagRepository(repository: TagRepositoryImpl): TagRepository

    @Binds
    @ViewModelScoped
    abstract fun bindRecipeRepository(repository: RecipeRepositoryImpl): RecipeRepository
}