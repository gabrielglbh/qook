package com.gabr.gabc.qook.domain.user

import android.os.Parcelable
import com.gabr.gabc.qook.infrastructure.user.UserDto
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val name: String,
    val email: String,
    val beginningWeekDay: Int = 1
) : Parcelable

fun User.toDto(): UserDto {
    return UserDto(name, email, beginningWeekDay)
}
