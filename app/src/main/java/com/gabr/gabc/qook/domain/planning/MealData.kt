package com.gabr.gabc.qook.domain.planning

import android.os.Parcelable
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.presentation.shared.Globals
import kotlinx.parcelize.Parcelize

@Parcelize
data class MealData(
    val meal: Recipe,
    val op: String
) : Parcelable {
    companion object {
        val EMPTY_MEAL_DATA = MealData(Recipe.EMPTY_RECIPE, "")

        fun fromMap(map: Map<String, String>): MealData {
            return MealData(Recipe.EMPTY_RECIPE, map[Globals.OBJ_MEAL_DATA_OP]!!)
        }
    }
}

fun MealData.toMap(): Map<String, String> {
    return mapOf(
        Pair(Globals.OBJ_MEAL_DATA_MEAL, meal.id),
        Pair(Globals.OBJ_MEAL_DATA_OP, op),
    )
}