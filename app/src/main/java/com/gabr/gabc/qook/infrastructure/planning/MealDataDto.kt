package com.gabr.gabc.qook.infrastructure.planning

import com.gabr.gabc.qook.domain.planning.MealData
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.google.firebase.firestore.PropertyName

data class MealDataDto constructor(
    @PropertyName("meal") val meal: String = "",
    @PropertyName("op") val op: String = "",
) {
    companion object {
        val EMPTY = MealDataDto("", "")
    }
}

fun MealDataDto.toDomain(): MealData {
    return MealData(Recipe.EMPTY, op)
}

fun MealDataDto.toMap(): Map<String, Any?> {
    return mapOf(
        Pair("meal", meal),
        Pair("op", op),
    )
}