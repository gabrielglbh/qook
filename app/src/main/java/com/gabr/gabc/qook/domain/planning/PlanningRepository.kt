package com.gabr.gabc.qook.domain.planning

import arrow.core.Either
import kotlinx.coroutines.flow.Flow

interface PlanningRepository {
    suspend fun getPlanning(): Either<PlanningFailure, List<DayPlanning>>
    fun getPlanningFromSharedPlanning(groupId: String): Flow<Either<PlanningFailure, List<DayPlanning>>>
    suspend fun updateRecipeFromPlanning(
        dayPlanning: DayPlanning,
        isLunch: Boolean? = null,
        groupId: String? = null
    ): Either<PlanningFailure, DayPlanning>

    suspend fun resetPlanning(groupId: String? = null): Either<PlanningFailure, Unit>
}