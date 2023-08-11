package com.gabr.gabc.qook.domain.ingredient

import com.gabr.gabc.qook.infrastructure.ingredient.IngredientDto

data class Ingredient(
    val name: String,
    val measure: String,
)

fun Ingredient.toDto(): IngredientDto {
    return IngredientDto(
        name,
        measure
    )
}