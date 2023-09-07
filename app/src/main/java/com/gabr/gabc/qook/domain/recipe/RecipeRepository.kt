package com.gabr.gabc.qook.domain.recipe

import arrow.core.Either
import com.gabr.gabc.qook.presentation.shared.Globals

interface RecipeRepository {
    suspend fun getRecipes(
        orderBy: String = Globals.OBJ_RECIPE_CREATION,
        query: String? = null,
        tagId: String? = null,
    ): Either<RecipeFailure, List<Recipe>>

    suspend fun updateRecipe(recipe: Recipe, id: String? = null): Either<RecipeFailure, Recipe>
    suspend fun removeRecipe(recipe: Recipe): Either<RecipeFailure, Unit>
    suspend fun getRecipe(recipeId: String, userId: String): Either<RecipeFailure, Recipe>
}