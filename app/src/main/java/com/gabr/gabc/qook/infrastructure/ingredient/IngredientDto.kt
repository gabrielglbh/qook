package com.gabr.gabc.qook.infrastructure.ingredient

import com.gabr.gabc.qook.domain.ingredient.Ingredient

data class IngredientDto(
    val name: String,
    val measure: String
)

fun IngredientDto.toDomain(): Ingredient {
    return Ingredient(name, measure)
}