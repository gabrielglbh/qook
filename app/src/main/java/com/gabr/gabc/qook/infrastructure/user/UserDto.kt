package com.gabr.gabc.qook.infrastructure.user

import com.gabr.gabc.qook.domain.user.User

data class UserDto(
    val name: String,
    val email: String,
    val avatar: String?,
    val beginningWeekDay: Int,
)

fun UserDto.toDomain(): User {
    return User(name, email, avatar, beginningWeekDay)
}