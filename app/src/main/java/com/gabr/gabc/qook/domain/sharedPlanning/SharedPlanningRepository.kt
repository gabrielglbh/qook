package com.gabr.gabc.qook.domain.sharedPlanning

import arrow.core.Either

interface SharedPlanningRepository {
    suspend fun createSharedPlanning(sharedPlanning: SharedPlanning): Either<SharedPlanningFailure, SharedPlanning>
    suspend fun updateSharedPlanning(
        sharedPlanning: SharedPlanning,
        id: String
    ): Either<SharedPlanningFailure, Unit>

    suspend fun addUserToSharedPlanning(id: String): Either<SharedPlanningFailure, Unit>
    suspend fun removeUserToSharedPlanning(
        id: String,
        uid: String
    ): Either<SharedPlanningFailure, Unit>

    suspend fun deleteSharedPlanning(id: String): Either<SharedPlanningFailure, Unit>
    suspend fun getSharedPlannings(): Either<SharedPlanningFailure, List<SharedPlanning>>
    suspend fun getSharedPlanning(id: String): Either<SharedPlanningFailure, SharedPlanning>
}