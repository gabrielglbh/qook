package com.gabr.gabc.qook.domain.sharedPlanning

sealed class SharedPlanningFailure(open val error: String) {
    data class SharedPlanningCreationFailed(override val error: String) :
        SharedPlanningFailure(error)

    data class SharedPlanningRetrievalFailed(override val error: String) :
        SharedPlanningFailure(error)

    data class SharedPlanningDoesNotExist(override val error: String) : SharedPlanningFailure(error)
    data class NotAuthenticated(override val error: String) : SharedPlanningFailure(error)
}
