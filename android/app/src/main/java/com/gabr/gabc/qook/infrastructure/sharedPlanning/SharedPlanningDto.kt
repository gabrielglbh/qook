package com.gabr.gabc.qook.infrastructure.sharedPlanning

import android.net.Uri
import com.gabr.gabc.qook.domain.sharedPlanning.SharedPlanning
import com.gabr.gabc.qook.presentation.shared.Globals
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class SharedPlanningDto constructor(
    @DocumentId val id: String = "",
    @PropertyName("name") val name: String = "",
    @PropertyName("resetDay") val resetDay: Int = 0,
    @PropertyName("hasPhoto") val hasPhoto: Boolean = false,
    @PropertyName(Globals.OBJ_SHARED_PLANNING_ADMIN) val admin: String = "",
    @PropertyName(Globals.OBJ_SHARED_PLANNING_USERS) val users: List<String> = listOf(),
)

fun SharedPlanningDto.toDomain(): SharedPlanning {
    return SharedPlanning(
        id,
        name,
        Uri.EMPTY,
        resetDay,
        admin,
        listOf(),
    )
}

fun SharedPlanningDto.toMap(): Map<String, Any?> {
    return mapOf(
        Pair("name", name),
        Pair("resetDay", resetDay),
        Pair("hasPhoto", hasPhoto),
        Pair(Globals.OBJ_SHARED_PLANNING_ADMIN, admin),
        Pair(Globals.OBJ_SHARED_PLANNING_USERS, users),
    )
}