package com.gabr.gabc.qook.domain.planning

import arrow.core.Either

interface PlanningRepository {
    suspend fun getPlanning(groupId: String? = null): Either<PlanningFailure, List<DayPlanning>>
    suspend fun updateRecipeFromPlanning(
        dayPlanning: DayPlanning,
        isLunch: Boolean? = null,
        groupId: String? = null
    ): Either<PlanningFailure, DayPlanning>

    suspend fun resetPlanning(groupId: String? = null): Either<PlanningFailure, Unit>
}