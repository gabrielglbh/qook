package com.gabr.gabc.qook.infrastructure.recipe

import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.domain.recipe.RecipeRepository

class RecipeRepositoryImpl : RecipeRepository {
    override suspend fun getRecipes(): List<Recipe> {
        TODO("Not yet implemented")
    }

    override suspend fun createRecipe(recipe: Recipe) {
        TODO("Not yet implemented")
    }

    override suspend fun removeRecipe(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun updateRecipe(recipe: Recipe) {
        TODO("Not yet implemented")
    }

    override suspend fun getSearchedRecipes(filters: Map<String, String>): List<Recipe> {
        TODO("Not yet implemented")
    }

    override suspend fun getRecipe(id: String): Recipe {
        TODO("Not yet implemented")
    }
}