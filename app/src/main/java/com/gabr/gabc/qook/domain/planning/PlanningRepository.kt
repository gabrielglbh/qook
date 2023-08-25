package com.gabr.gabc.qook.domain.planning

import arrow.core.Either

interface PlanningRepository {
    suspend fun getPlanning(): Either<PlanningFailure, Planning>
    suspend fun updateRecipeFromPlanning(dayPlanning: DayPlanning): Either<PlanningFailure, Unit>
    suspend fun resetPlanning(): Either<PlanningFailure, Unit>
}