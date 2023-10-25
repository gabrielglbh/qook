package com.gabr.gabc.qook.domain.planning

import android.os.Parcelable
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.infrastructure.planning.MealDataDto
import kotlinx.parcelize.Parcelize

@Parcelize
data class MealData(
    val meal: Recipe,
    val op: String
) : Parcelable {
    companion object {
        val EMPTY = MealData(Recipe.EMPTY, "")
    }
}

fun MealData.toDto(): MealDataDto {
    return MealDataDto(
        meal.id,
        op,
    )
}