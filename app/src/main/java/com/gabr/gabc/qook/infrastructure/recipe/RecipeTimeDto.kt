package com.gabr.gabc.qook.infrastructure.recipe

import com.gabr.gabc.qook.domain.recipe.RecipeTime

data class RecipeTimeDto(
    val time: Int,
    val measure: String,
)

fun RecipeTimeDto.toDomain(): RecipeTime {
    return RecipeTime(time, measure)
}


