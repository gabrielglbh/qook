package com.gabr.gabc.qook.infrastructure.recipe

import com.gabr.gabc.qook.domain.recipe.RecipeTime
import com.google.firebase.firestore.PropertyName

data class RecipeTimeDto(
    @PropertyName("time") val time: Int = 0,
    @PropertyName("time") val measure: String = "",
)

fun RecipeTimeDto.toDomain(): RecipeTime {
    return RecipeTime(time, measure)
}


