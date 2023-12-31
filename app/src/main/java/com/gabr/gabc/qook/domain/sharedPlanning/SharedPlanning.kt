package com.gabr.gabc.qook.domain.sharedPlanning

import android.net.Uri
import android.os.Parcelable
import com.gabr.gabc.qook.domain.user.User
import com.gabr.gabc.qook.infrastructure.sharedPlanning.SharedPlanningDto
import kotlinx.parcelize.Parcelize

@Parcelize
data class SharedPlanning(
    val id: String,
    val name: String,
    val photo: Uri,
    val resetDay: Int,
    val admin: String,
    val users: List<User>,
) : Parcelable {
    companion object {
        val EMPTY =
            SharedPlanning("", "", Uri.EMPTY, 0, "", listOf())
    }
}

fun SharedPlanning.toDto(): SharedPlanningDto {
    return SharedPlanningDto(
        id,
        name,
        resetDay,
        photo != Uri.EMPTY,
        admin,
        users.map { it.id }
    )
}