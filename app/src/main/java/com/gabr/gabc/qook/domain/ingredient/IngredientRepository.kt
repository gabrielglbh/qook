package com.gabr.gabc.qook.domain.ingredient

interface IngredientRepository {
    suspend fun getIngredientsOfRecipe(recipeId: String): List<Ingredient>
    suspend fun getIngredientsOfShoppingList(): List<Ingredient>
    suspend fun createIngredients(recipeId: String, ingredients: List<Ingredient>)
    suspend fun removeIngredients(recipeId: String, ingredients: List<Ingredient>)
}