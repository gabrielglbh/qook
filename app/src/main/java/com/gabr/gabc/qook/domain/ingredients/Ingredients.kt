package com.gabr.gabc.qook.domain.ingredients

import com.gabr.gabc.qook.infrastructure.ingredient.IngredientsDto

data class Ingredients(
    val list: Map<String, Boolean>
)

fun Ingredients.toDto(): IngredientsDto {
    return IngredientsDto(list)
}