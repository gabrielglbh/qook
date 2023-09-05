package com.gabr.gabc.qook.domain.sharedPlanning

import com.gabr.gabc.qook.domain.ingredients.Ingredients
import com.gabr.gabc.qook.domain.planning.DayPlanning
import com.gabr.gabc.qook.domain.user.User
import com.gabr.gabc.qook.infrastructure.sharedPlanning.SharedPlanningDto

data class SharedPlanning(
    val id: String,
    val name: String,
    val resetDay: Int,
    val planning: List<DayPlanning>,
    val shoppingList: Ingredients,
    val users: List<User>,
) {
    companion object {
        val EMPTY_SHARED_PLANNING =
            SharedPlanning("", "", 0, listOf(), Ingredients(mapOf()), listOf())
    }
}

fun SharedPlanning.toDto(): SharedPlanningDto {
    return SharedPlanningDto(
        id,
        name,
        resetDay,
    )
}