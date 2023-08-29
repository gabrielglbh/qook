package com.gabr.gabc.qook.domain.ingredients

import arrow.core.Either

interface IngredientsRepository {
    suspend fun getIngredientsOfShoppingList(): Either<IngredientsFailure, Ingredients>
    suspend fun removeIngredient(ingredients: Ingredients): Either<IngredientsFailure, Unit>
    suspend fun updateIngredient(ingredients: Ingredients): Either<IngredientsFailure, Unit>
    suspend fun resetIngredients(): Either<IngredientsFailure, Unit>
}