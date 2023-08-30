package com.gabr.gabc.qook.domain.user

import android.net.Uri
import android.os.Parcelable
import com.gabr.gabc.qook.infrastructure.user.UserDto
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val name: String,
    val email: String,
    val resetDay: Int = 0,
    val photo: Uri = Uri.EMPTY,
    val language: String,
    val messagingToken: String,
) : Parcelable {
    companion object {
        val EMPTY_USER = User("", "", 0, Uri.EMPTY, "", "")
    }
}

fun User.toDto(): UserDto {
    return UserDto(name, email, resetDay, language, messagingToken)
}
