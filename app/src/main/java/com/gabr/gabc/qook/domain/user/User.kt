package com.gabr.gabc.qook.domain.user

import com.gabr.gabc.qook.infrastructure.user.UserDto

data class User(
    val name: String,
    val email: String,
    val beginningWeekDay: Int = 1,
    val avatar: String? = null,
)

fun User.toDto(): UserDto {
    return UserDto(name, email, beginningWeekDay, avatar)
}
