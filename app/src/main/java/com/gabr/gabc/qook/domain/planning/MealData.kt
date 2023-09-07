package com.gabr.gabc.qook.domain.planning

import android.os.Parcelable
import com.gabr.gabc.qook.domain.recipe.Recipe
import kotlinx.parcelize.Parcelize

@Parcelize
data class MealData(
    val meal: Recipe,
    val op: String
) : Parcelable {
    companion object {
        val EMPTY_MEAL_DATA = MealData(Recipe.EMPTY_RECIPE, "")
    }
}

fun MealData.toMap(): Map<String, String> {
    return mapOf(
        Pair("meal", meal.id),
        Pair("op", op),
    )
}