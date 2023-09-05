package com.gabr.gabc.qook.domain.ingredients

import arrow.core.Either

interface IngredientsRepository {
    suspend fun getIngredientsOfShoppingList(groupId: String? = null): Either<IngredientsFailure, Ingredients>
    suspend fun removeIngredient(
        ingredient: Pair<String, Boolean>,
        groupId: String? = null
    ): Either<IngredientsFailure, Unit>

    suspend fun removeIngredients(
        ingredients: Ingredients,
        groupId: String? = null
    ): Either<IngredientsFailure, Unit>

    suspend fun updateIngredient(
        ingredient: Pair<String, Boolean>,
        groupId: String? = null
    ): Either<IngredientsFailure, Unit>

    suspend fun updateIngredients(
        ingredients: Ingredients,
        groupId: String? = null
    ): Either<IngredientsFailure, Unit>

    suspend fun resetIngredients(groupId: String? = null): Either<IngredientsFailure, Unit>
}