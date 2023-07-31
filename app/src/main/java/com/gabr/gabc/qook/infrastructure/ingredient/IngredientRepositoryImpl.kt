package com.gabr.gabc.qook.infrastructure.ingredient

import com.gabr.gabc.qook.domain.ingredient.Ingredient
import com.gabr.gabc.qook.domain.ingredient.IngredientRepository

class IngredientRepositoryImpl : IngredientRepository {
    override suspend fun getIngredientsOfRecipe(recipeId: String): List<Ingredient> {
        TODO("Not yet implemented")
    }

    override suspend fun getIngredientsOfShoppingList(): List<Ingredient> {
        TODO("Not yet implemented")
    }

    override suspend fun createIngredients(recipeId: String, ingredients: List<Ingredient>) {
        TODO("Not yet implemented")
    }

    override suspend fun removeIngredients(recipeId: String, ingredients: List<Ingredient>) {
        TODO("Not yet implemented")
    }
}