package com.gabr.gabc.qook.infrastructure.ingredient

import com.gabr.gabc.qook.domain.ingredient.Ingredient
import com.google.firebase.firestore.PropertyName

data class IngredientDto(
    @PropertyName("name") val name: String,
    @PropertyName("measure") val measure: String
)

fun IngredientDto.toDomain(): Ingredient {
    return Ingredient(name, measure)
}