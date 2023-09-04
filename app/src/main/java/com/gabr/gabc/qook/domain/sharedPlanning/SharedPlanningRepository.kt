package com.gabr.gabc.qook.domain.sharedPlanning

import arrow.core.Either

interface SharedPlanningRepository {
    suspend fun createSharedPlanning(): Either<SharedPlanningFailure, SharedPlanning>
    suspend fun updateSharedPlanning(id: String): Either<SharedPlanningFailure, Unit>
    suspend fun deleteSharedPlanning(id: String): Either<SharedPlanningFailure, Unit>
    suspend fun getSharedPlannings(): Either<SharedPlanningFailure, List<SharedPlanning>>
}