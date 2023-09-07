package com.gabr.gabc.qook.infrastructure.planning

import com.gabr.gabc.qook.domain.planning.DayPlanning
import com.gabr.gabc.qook.domain.planning.MealData
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.presentation.shared.Globals
import com.google.firebase.firestore.PropertyName

data class DayPlanningDto constructor(
    @PropertyName("id") val id: String = "",
    @PropertyName(Globals.OBJ_PLANNING_DAY_INDEX) val dayIndex: Int = 0,
    @PropertyName("lunch") val lunch: Map<String, String> = mapOf(),
    @PropertyName("dinner") val dinner: Map<String, String> = mapOf(),
)

fun DayPlanningDto.toDomain(): DayPlanning {
    return DayPlanning(
        id,
        dayIndex,
        MealData(
            Recipe.EMPTY_RECIPE,
            ""
        ),
        MealData(
            Recipe.EMPTY_RECIPE,
            ""
        ),
    )
}

fun DayPlanningDto.toMap(): Map<String, Any?> {
    return mapOf(
        Pair("id", id),
        Pair(Globals.OBJ_PLANNING_DAY_INDEX, dayIndex),
        Pair("lunch", lunch),
        Pair("dinner", dinner),
    )
}