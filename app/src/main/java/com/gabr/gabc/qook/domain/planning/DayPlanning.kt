package com.gabr.gabc.qook.domain.planning

import android.os.Parcelable
import com.gabr.gabc.qook.infrastructure.planning.DayPlanningDto
import com.gabr.gabc.qook.presentation.shared.Globals
import kotlinx.parcelize.Parcelize

@Parcelize
data class DayPlanning constructor(
    val id: String,
    val dayIndex: Int,
    val lunch: MealData,
    val dinner: MealData,
) : Parcelable {
    companion object {
        val EMPTY =
            DayPlanning(
                "",
                0,
                MealData.EMPTY,
                MealData.EMPTY
            )
        val EMPTY_PLANNING = listOf(
            EMPTY.copy(id = Globals.OBJ_PLANNING_FIRST_DAY, dayIndex = 0),
            EMPTY.copy(id = Globals.OBJ_PLANNING_SECOND_DAY, dayIndex = 1),
            EMPTY.copy(id = Globals.OBJ_PLANNING_THIRD_DAY, dayIndex = 2),
            EMPTY.copy(id = Globals.OBJ_PLANNING_FOURTH_DAY, dayIndex = 3),
            EMPTY.copy(id = Globals.OBJ_PLANNING_FIFTH_DAY, dayIndex = 4),
            EMPTY.copy(id = Globals.OBJ_PLANNING_SIXTH_DAY, dayIndex = 5),
            EMPTY.copy(id = Globals.OBJ_PLANNING_SEVENTH_DAY, dayIndex = 6)
        )
    }
}

fun DayPlanning.toDto(): DayPlanningDto {
    return DayPlanningDto(
        id,
        dayIndex,
        lunch.toMap(),
        dinner.toMap()
    )
}