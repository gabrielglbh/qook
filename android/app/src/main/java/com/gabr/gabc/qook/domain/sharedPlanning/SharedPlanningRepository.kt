package com.gabr.gabc.qook.domain.sharedPlanning

import arrow.core.Either
import kotlinx.coroutines.flow.Flow

interface SharedPlanningRepository {
    suspend fun createSharedPlanning(sharedPlanning: SharedPlanning): Either<SharedPlanningFailure, SharedPlanning>
    suspend fun updateSharedPlanning(
        sharedPlanning: SharedPlanning,
        id: String
    ): Either<SharedPlanningFailure, Unit>

    suspend fun addUserToSharedPlanning(id: String): Either<SharedPlanningFailure, Unit>
    suspend fun removeUserFromSharedPlanning(
        id: String,
        uid: String? = null
    ): Either<SharedPlanningFailure, Unit>

    suspend fun deleteSharedPlanning(sharedPlanning: SharedPlanning): Either<SharedPlanningFailure, Unit>
    fun getSharedPlannings(): Flow<Either<SharedPlanningFailure, List<SharedPlanning>>>
    fun getSharedPlanning(id: String): Flow<Either<SharedPlanningFailure, SharedPlanning>>
}