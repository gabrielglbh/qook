package com.gabr.gabc.qook.domain.recipe

import com.gabr.gabc.qook.infrastructure.recipe.RecipeTimeDto

data class RecipeTime(
    val time: Int,
    val measure: String,
)

fun RecipeTime.toDto(): RecipeTimeDto {
    return RecipeTimeDto(
        time, measure
    )
}
