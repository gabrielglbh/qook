package com.gabr.gabc.qook.infrastructure.planning

import com.gabr.gabc.qook.domain.planning.DayPlanning
import com.gabr.gabc.qook.domain.planning.Planning
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.presentation.shared.Globals
import com.google.firebase.firestore.PropertyName

data class PlanningDto constructor(
    @PropertyName(Globals.OBJ_PLANNING_FIRST_DAY) val firstDay: DayPlanningDto = DayPlanningDto(),
    @PropertyName(Globals.OBJ_PLANNING_SECOND_DAY) val secondDay: DayPlanningDto = DayPlanningDto(),
    @PropertyName(Globals.OBJ_PLANNING_THIRD_DAY) val thirdDay: DayPlanningDto = DayPlanningDto(),
    @PropertyName(Globals.OBJ_PLANNING_FOURTH_DAY) val fourthDay: DayPlanningDto = DayPlanningDto(),
    @PropertyName(Globals.OBJ_PLANNING_FIFTH_DAY) val fifthDay: DayPlanningDto = DayPlanningDto(),
    @PropertyName(Globals.OBJ_PLANNING_SIXTH_DAY) val sixthDay: DayPlanningDto = DayPlanningDto(),
    @PropertyName(Globals.OBJ_PLANNING_SEVENTH_DAY) val seventhDay: DayPlanningDto = DayPlanningDto(),
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

fun PlanningDto.toMap(): Map<String, Any> {
    return mapOf(
        Pair(Globals.OBJ_PLANNING_FIRST_DAY, firstDay),
        Pair(Globals.OBJ_PLANNING_SECOND_DAY, secondDay),
        Pair(Globals.OBJ_PLANNING_THIRD_DAY, thirdDay),
        Pair(Globals.OBJ_PLANNING_FOURTH_DAY, fourthDay),
        Pair(Globals.OBJ_PLANNING_FIFTH_DAY, fifthDay),
        Pair(Globals.OBJ_PLANNING_SIXTH_DAY, sixthDay),
        Pair(Globals.OBJ_PLANNING_SEVENTH_DAY, seventhDay),
    )
}

data class DayPlanningDto constructor(
    @PropertyName("id") val id: String = "",
    @PropertyName("dayIndex") val dayIndex: Int = 0,
    @PropertyName("lunch") val lunch: String = "",
    @PropertyName("dinner") val dinner: String = "",
)

fun DayPlanningDto.toDomain(): DayPlanning {
    return DayPlanning(
        id,
        dayIndex,
        Recipe.EMPTY_RECIPE,
        Recipe.EMPTY_RECIPE
    )
}