package com.gabr.gabc.qook.domain.recipe

import arrow.core.Either

interface RecipeRepository {
    suspend fun getRecipes(): Either<RecipeFailure, List<Recipe>>
    suspend fun updateRecipe(recipe: Recipe, id: String? = null): Either<RecipeFailure, Recipe>
    suspend fun removeRecipe(id: String): Either<RecipeFailure, Unit>
    suspend fun getSearchedRecipes(filters: Map<String, String>): List<Recipe>
    suspend fun getRecipe(id: String): Either<RecipeFailure, Recipe>
}