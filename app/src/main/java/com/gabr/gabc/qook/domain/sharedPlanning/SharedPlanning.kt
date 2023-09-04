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
)

fun SharedPlanning.toDto(): SharedPlanningDto {
    return SharedPlanningDto(
        id,
        name,
        resetDay,
        users = users.map { it.id }
    )
}