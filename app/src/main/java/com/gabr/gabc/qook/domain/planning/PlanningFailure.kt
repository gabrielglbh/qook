package com.gabr.gabc.qook.domain.planning

sealed class PlanningFailure(open val error: String) {
    data class PlanningCreationFailed(override val error: String) : PlanningFailure(error)
    data class PlanningRetrievalFailed(override val error: String) : PlanningFailure(error)
    data class PlanningDoesNotExist(override val error: String) : PlanningFailure(error)
    data class NotAuthenticated(override val error: String) : PlanningFailure(error)
}
