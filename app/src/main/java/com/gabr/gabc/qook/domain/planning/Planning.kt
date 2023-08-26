package com.gabr.gabc.qook.domain.planning

import android.os.Parcelable
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.infrastructure.planning.DayPlanningDto
import com.gabr.gabc.qook.infrastructure.planning.PlanningDto
import com.gabr.gabc.qook.presentation.shared.Globals
import kotlinx.parcelize.Parcelize

data class Planning constructor(
    val firstDay: DayPlanning,
    val secondDay: DayPlanning,
    val thirdDay: DayPlanning,
    val fourthDay: DayPlanning,
    val fifthDay: DayPlanning,
    val sixthDay: DayPlanning,
    val seventhDay: DayPlanning,
) {
    companion object {
        val EMPTY_PLANNING = Planning(
            DayPlanning(
                Globals.OBJ_PLANNING_FIRST_DAY,
                0,
                Recipe.EMPTY_RECIPE,
                Recipe.EMPTY_RECIPE
            ),
            DayPlanning(
                Globals.OBJ_PLANNING_SECOND_DAY,
                1,
                Recipe.EMPTY_RECIPE,
                Recipe.EMPTY_RECIPE
            ),
            DayPlanning(
                Globals.OBJ_PLANNING_THIRD_DAY,
                2,
                Recipe.EMPTY_RECIPE,
                Recipe.EMPTY_RECIPE
            ),
            DayPlanning(
                Globals.OBJ_PLANNING_FOURTH_DAY,
                3,
                Recipe.EMPTY_RECIPE,
                Recipe.EMPTY_RECIPE
            ),
            DayPlanning(
                Globals.OBJ_PLANNING_FIFTH_DAY,
                4,
                Recipe.EMPTY_RECIPE,
                Recipe.EMPTY_RECIPE
            ),
            DayPlanning(
                Globals.OBJ_PLANNING_SIXTH_DAY,
                5,
                Recipe.EMPTY_RECIPE,
                Recipe.EMPTY_RECIPE
            ),
            DayPlanning(
                Globals.OBJ_PLANNING_SEVENTH_DAY,
                6,
                Recipe.EMPTY_RECIPE,
                Recipe.EMPTY_RECIPE
            ),
        )
    }
}

fun Planning.toDto(): PlanningDto {
    return PlanningDto(
        firstDay.toDto(),
        secondDay.toDto(),
        thirdDay.toDto(),
        fourthDay.toDto(),
        fifthDay.toDto(),
        sixthDay.toDto(),
        seventhDay.toDto(),
    )
}

@Parcelize
data class DayPlanning constructor(
    val id: String,
    val dayIndex: Int,
    val lunch: Recipe,
    val dinner: Recipe
) : Parcelable {
    companion object {
        val EMPTY_DAY_PLANNING = DayPlanning("", 0, Recipe.EMPTY_RECIPE, Recipe.EMPTY_RECIPE)
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