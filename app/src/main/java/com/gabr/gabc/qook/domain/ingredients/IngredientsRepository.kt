package com.gabr.gabc.qook.domain.ingredients

import arrow.core.Either

interface IngredientsRepository {
    suspend fun getIngredientsOfShoppingList(): Either<IngredientsFailure, Ingredients>
    suspend fun removeIngredient(ingredient: Pair<String, Boolean>): Either<IngredientsFailure, Unit>
    suspend fun removeIngredients(ingredients: Ingredients): Either<IngredientsFailure, Unit>
    suspend fun updateIngredient(ingredient: Pair<String, Boolean>): Either<IngredientsFailure, Unit>
    suspend fun updateIngredients(ingredients: Ingredients): Either<IngredientsFailure, Unit>
    suspend fun resetIngredients(): Either<IngredientsFailure, Unit>
}