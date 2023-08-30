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
    val photo: Uri = Uri.EMPTY
) : Parcelable {
    companion object {
        val EMPTY_USER = User("", "")
    }
}

fun User.toDto(): UserDto {
    return UserDto(name, email, resetDay)
}
