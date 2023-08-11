package com.gabr.gabc.qook.infrastructure.user

import com.gabr.gabc.qook.domain.user.User
import com.google.firebase.firestore.PropertyName

data class UserDto(
    @PropertyName("name") val name: String,
    @PropertyName("email") val email: String,
    @PropertyName("beginningWeekDay") val beginningWeekDay: Int = 1,
    @PropertyName("avatar") val avatar: String? = null,
)

fun UserDto.toDomain(): User {
    return User(name, email, beginningWeekDay, avatar)
}