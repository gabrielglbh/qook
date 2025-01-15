package com.gabr.gabc.qook.infrastructure.ingredient

import com.gabr.gabc.qook.domain.ingredients.Ingredients
import com.gabr.gabc.qook.presentation.shared.Globals
import com.google.firebase.firestore.PropertyName

data class IngredientsDto constructor(
    @PropertyName(Globals.OBJ_SHOPPING_LIST) val list: Map<String, Boolean> = mapOf(),
)

fun IngredientsDto.toDomain(): Ingredients {
    return Ingredients(list)
}