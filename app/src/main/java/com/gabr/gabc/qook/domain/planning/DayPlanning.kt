package com.gabr.gabc.qook.domain.planning

import android.os.Parcelable
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.infrastructure.planning.DayPlanningDto
import com.gabr.gabc.qook.presentation.shared.Globals
import kotlinx.parcelize.Parcelize

@Parcelize
data class DayPlanning constructor(
    val id: String,
    val dayIndex: Int,
    val lunch: Recipe,
    val dinner: Recipe
) : Parcelable {
    companion object {
        val EMPTY_DAY_PLANNING = DayPlanning("", 0, Recipe.EMPTY_RECIPE, Recipe.EMPTY_RECIPE)
        val EMPTY_PLANNING = listOf(
            EMPTY_DAY_PLANNING.copy(id = Globals.OBJ_PLANNING_FIRST_DAY, dayIndex = 0),
            EMPTY_DAY_PLANNING.copy(id = Globals.OBJ_PLANNING_SECOND_DAY, dayIndex = 1),
            EMPTY_DAY_PLANNING.copy(id = Globals.OBJ_PLANNING_THIRD_DAY, dayIndex = 2),
            EMPTY_DAY_PLANNING.copy(id = Globals.OBJ_PLANNING_FOURTH_DAY, dayIndex = 3),
            EMPTY_DAY_PLANNING.copy(id = Globals.OBJ_PLANNING_FIFTH_DAY, dayIndex = 4),
            EMPTY_DAY_PLANNING.copy(id = Globals.OBJ_PLANNING_SIXTH_DAY, dayIndex = 5),
            EMPTY_DAY_PLANNING.copy(id = Globals.OBJ_PLANNING_SEVENTH_DAY, dayIndex = 6)
        )
    }
}

fun DayPlanning.toDto(): DayPlanningDto {
    return DayPlanningDto(
        id,
        dayIndex,
        lunch.id,
        dinner.id
    )
}