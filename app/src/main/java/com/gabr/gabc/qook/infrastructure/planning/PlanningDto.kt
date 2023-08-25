package com.gabr.gabc.qook.infrastructure.planning

import com.gabr.gabc.qook.domain.planning.DayPlanning
import com.gabr.gabc.qook.domain.planning.Planning
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.google.firebase.firestore.PropertyName

data class PlanningDto constructor(
    @PropertyName("0") val firstDay: DayPlanningDto = DayPlanningDto(),
    @PropertyName("1") val secondDay: DayPlanningDto = DayPlanningDto(),
    @PropertyName("2") val thirdDay: DayPlanningDto = DayPlanningDto(),
    @PropertyName("3") val fourthDay: DayPlanningDto = DayPlanningDto(),
    @PropertyName("4") val fifthDay: DayPlanningDto = DayPlanningDto(),
    @PropertyName("5") val sixthDay: DayPlanningDto = DayPlanningDto(),
    @PropertyName("6") val seventhDay: DayPlanningDto = DayPlanningDto(),
)

fun PlanningDto.toDomain(): Planning {
    return Planning(
        firstDay.toDomain(),
        secondDay.toDomain(),
        thirdDay.toDomain(),
        fourthDay.toDomain(),
        fifthDay.toDomain(),
        sixthDay.toDomain(),
        seventhDay.toDomain(),
    )
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