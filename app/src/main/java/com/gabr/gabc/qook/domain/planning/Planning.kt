package com.gabr.gabc.qook.domain.planning

import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.infrastructure.planning.DayPlanningDto
import com.gabr.gabc.qook.infrastructure.planning.PlanningDto

data class Planning constructor(
    val dayPlannings: List<DayPlanning>
) {
    companion object {
        val EMPTY_PLANNING = Planning(mutableListOf<DayPlanning>().apply {
            add(DayPlanning(Recipe.EMPTY_RECIPE, Recipe.EMPTY_RECIPE))
            add(DayPlanning(Recipe.EMPTY_RECIPE, Recipe.EMPTY_RECIPE))
            add(DayPlanning(Recipe.EMPTY_RECIPE, Recipe.EMPTY_RECIPE))
            add(DayPlanning(Recipe.EMPTY_RECIPE, Recipe.EMPTY_RECIPE))
            add(DayPlanning(Recipe.EMPTY_RECIPE, Recipe.EMPTY_RECIPE))
            add(DayPlanning(Recipe.EMPTY_RECIPE, Recipe.EMPTY_RECIPE))
            add(DayPlanning(Recipe.EMPTY_RECIPE, Recipe.EMPTY_RECIPE))
        })
    }
}

fun Planning.toDto(): PlanningDto {
    return PlanningDto(dayPlannings.map { it.toDto() })
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