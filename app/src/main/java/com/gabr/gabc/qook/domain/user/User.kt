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
    val hasPhoto: Boolean,
) : Parcelable {
    companion object {
        val EMPTY = User("", "", "", 0, Uri.EMPTY, "", listOf(), "", false)
    }
}

fun User.toDto(): UserDto {
    return UserDto(id, name, email, resetDay, language, adminOf, messagingToken, hasPhoto)
}
