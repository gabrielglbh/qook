package com.gabr.gabc.qook.domain.planning

import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.infrastructure.planning.DayPlanningDto
import com.gabr.gabc.qook.infrastructure.planning.PlanningDto

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
            DayPlanning(Recipe.EMPTY_RECIPE, Recipe.EMPTY_RECIPE),
            DayPlanning(Recipe.EMPTY_RECIPE, Recipe.EMPTY_RECIPE),
            DayPlanning(Recipe.EMPTY_RECIPE, Recipe.EMPTY_RECIPE),
            DayPlanning(Recipe.EMPTY_RECIPE, Recipe.EMPTY_RECIPE),
            DayPlanning(Recipe.EMPTY_RECIPE, Recipe.EMPTY_RECIPE),
            DayPlanning(Recipe.EMPTY_RECIPE, Recipe.EMPTY_RECIPE),
            DayPlanning(Recipe.EMPTY_RECIPE, Recipe.EMPTY_RECIPE),
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

data class DayPlanning constructor(
    val lunch: Recipe,
    val dinner: Recipe
)

fun DayPlanning.toDto(): DayPlanningDto {
    return DayPlanningDto(
        lunch.id,
        dinner.id
    )
}