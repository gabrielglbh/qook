package com.gabr.gabc.qook.domain.ingredient

interface IngredientRepository {
    suspend fun getIngredientsOfRecipe(recipeId: String): List<String>
    suspend fun getIngredientsOfShoppingList(): List<String>
    suspend fun createIngredients(recipeId: String, ingredients: List<String>)
    suspend fun removeIngredients(recipeId: String, ingredients: List<String>)
}