package com.gabr.gabc.qook.domain.planning

import android.os.Parcelable
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.infrastructure.planning.DayPlanningDto
import com.gabr.gabc.qook.infrastructure.planning.PlanningDto
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
            DayPlanning("", 0, Recipe.EMPTY_RECIPE, Recipe.EMPTY_RECIPE),
            DayPlanning("", 1, Recipe.EMPTY_RECIPE, Recipe.EMPTY_RECIPE),
            DayPlanning("", 2, Recipe.EMPTY_RECIPE, Recipe.EMPTY_RECIPE),
            DayPlanning("", 3, Recipe.EMPTY_RECIPE, Recipe.EMPTY_RECIPE),
            DayPlanning("", 4, Recipe.EMPTY_RECIPE, Recipe.EMPTY_RECIPE),
            DayPlanning("", 5, Recipe.EMPTY_RECIPE, Recipe.EMPTY_RECIPE),
            DayPlanning("", 6, Recipe.EMPTY_RECIPE, Recipe.EMPTY_RECIPE),
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