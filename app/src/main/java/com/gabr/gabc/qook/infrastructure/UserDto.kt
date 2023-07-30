package com.gabr.gabc.qook.infrastructure

import com.gabr.gabc.qook.domain.User

data class UserDto(
    val name: String,
    val email: String,
    val avatar: String,
    val beginningWeekDay: Int,
)

fun UserDto.toDomain(): User {
    return User(name, email, avatar, beginningWeekDay)
}
