package com.gabr.gabc.qook.infrastructure.planning

import com.gabr.gabc.qook.domain.planning.DayPlanning
import com.gabr.gabc.qook.domain.planning.Planning
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.presentation.shared.Globals
import com.google.firebase.firestore.PropertyName

data class PlanningDto constructor(
    @PropertyName(Globals.OBJ_PLANNING_DAY_PLANNINGS) val dayPlannings: List<DayPlanningDto> = listOf(),
)

fun PlanningDto.toDomain(): Planning {
    return Planning(dayPlannings.map { it.toDomain() })
}

data class DayPlanningDto constructor(
    @PropertyName("lunch") val lunch: String = "",
    @PropertyName("dinner") val dinner: String = "",
)

fun DayPlanningDto.toDomain(): DayPlanning {
    return DayPlanning(
        Recipe.EMPTY_RECIPE,
        Recipe.EMPTY_RECIPE
    )
}