package com.gabr.gabc.qook.domain.recipe

interface RecipeRepository {
    suspend fun getRecipes(): List<Recipe>
    suspend fun createRecipe(recipe: Recipe)
    suspend fun removeRecipe(id: String)
    suspend fun updateRecipe(recipe: Recipe)
    suspend fun getSearchedRecipes(filters: Map<String, String>): List<Recipe>
    suspend fun getRecipe(id: String): Recipe
}