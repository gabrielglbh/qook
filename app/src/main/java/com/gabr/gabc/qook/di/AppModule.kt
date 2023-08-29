package com.gabr.gabc.qook.di

import com.gabr.gabc.qook.domain.ingredients.IngredientsRepository
import com.gabr.gabc.qook.domain.planning.PlanningRepository
import com.gabr.gabc.qook.domain.recipe.RecipeRepository
import com.gabr.gabc.qook.domain.tag.TagRepository
import com.gabr.gabc.qook.domain.user.UserRepository
import com.gabr.gabc.qook.infrastructure.ingredient.IngredientRepositoryImpl
import com.gabr.gabc.qook.infrastructure.planning.PlanningRepositoryImpl
import com.gabr.gabc.qook.infrastructure.recipe.RecipeRepositoryImpl
import com.gabr.gabc.qook.infrastructure.tag.TagRepositoryImpl
import com.gabr.gabc.qook.infrastructure.user.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class AppModule {
    @Binds
    @ViewModelScoped
    abstract fun bindTagRepository(repository: TagRepositoryImpl): TagRepository

    @Binds
    @ViewModelScoped
    abstract fun bindRecipeRepository(repository: RecipeRepositoryImpl): RecipeRepository

    @Binds
    @ViewModelScoped
    abstract fun bindUserRepository(repository: UserRepositoryImpl): UserRepository

    @Binds
    @ViewModelScoped
    abstract fun bindPlanningRepository(repository: PlanningRepositoryImpl): PlanningRepository

    @Binds
    @ViewModelScoped
    abstract fun bindIngredientsRepository(repository: IngredientRepositoryImpl): IngredientsRepository
}