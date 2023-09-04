package com.gabr.gabc.qook.infrastructure.sharedPlanning

import com.gabr.gabc.qook.domain.ingredients.Ingredients
import com.gabr.gabc.qook.domain.sharedPlanning.SharedPlanning
import com.google.firebase.firestore.PropertyName

data class SharedPlanningDto constructor(
    @PropertyName("id") val id: String = "",
    @PropertyName("name") val name: String = "",
    @PropertyName("resetDay") val resetDay: Int = 0,
    @PropertyName("users") val users: List<String> = listOf(),
)

fun SharedPlanningDto.toDomain(): SharedPlanning {
    return SharedPlanning(
        id,
        name,
        resetDay,
        listOf(),
        Ingredients(mapOf()),
        listOf(),
    )
}

fun SharedPlanningDto.toMap(): Map<String, Any?> {
    return mapOf(
        Pair("id", id),
        Pair("name", name),
        Pair("resetDay", resetDay),
        Pair("users", users),
    )
}