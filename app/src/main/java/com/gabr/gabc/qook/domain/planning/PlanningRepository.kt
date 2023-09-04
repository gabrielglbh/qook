package com.gabr.gabc.qook.domain.planning

import arrow.core.Either

interface PlanningRepository {
    suspend fun getPlanning(): Either<PlanningFailure, List<DayPlanning>>
    suspend fun updateRecipeFromPlanning(dayPlanning: DayPlanning): Either<PlanningFailure, Unit>
    suspend fun resetPlanning(): Either<PlanningFailure, Unit>
    suspend fun getPlanningFromGroup(id: String): Either<PlanningFailure, List<DayPlanning>>
    suspend fun updateRecipeFromPlanningFromGroup(
        id: String,
        dayPlanning: DayPlanning
    ): Either<PlanningFailure, Unit>

    suspend fun resetPlanningFromGroup(id: String): Either<PlanningFailure, Unit>
}