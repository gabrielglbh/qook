package com.gabr.gabc.qook.infrastructure.user

import com.gabr.gabc.qook.domain.user.User
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val name: String,
    val email: String,
    val beginningWeekDay: Int = 1,
    val avatar: String? = null,
)

fun UserDto.toDomain(): User {
    return User(name, email, beginningWeekDay, avatar)
}