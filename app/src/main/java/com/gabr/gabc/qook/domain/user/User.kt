package com.gabr.gabc.qook.domain.user

import android.net.Uri
import android.os.Parcelable
import com.gabr.gabc.qook.infrastructure.user.UserDto
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String,
    val name: String,
    val email: String,
    val resetDay: Int = 0,
    val photo: Uri = Uri.EMPTY,
    val language: String,
    val adminOf: List<String>,
    val messagingToken: String,
) : Parcelable {
    companion object {
        val EMPTY_USER = User("", "", "", 0, Uri.EMPTY, "", listOf(), "")
    }
}

fun User.toDto(): UserDto {
    return UserDto(id, name, email, resetDay, language, adminOf, messagingToken)
}
