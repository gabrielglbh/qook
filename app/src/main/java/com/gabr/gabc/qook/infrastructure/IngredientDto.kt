package com.gabr.gabc.qook.infrastructure

import com.gabr.gabc.qook.domain.Ingredient

data class IngredientDto(
    val name: String,
    val measure: String
)

fun IngredientDto.toDomain(): Ingredient {
    return Ingredient(name, measure)
}