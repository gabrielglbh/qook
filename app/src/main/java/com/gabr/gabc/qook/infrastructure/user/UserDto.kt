package com.gabr.gabc.qook.infrastructure.user

import com.gabr.gabc.qook.domain.user.User
import com.google.firebase.firestore.PropertyName

data class UserDto(
    @PropertyName("name") val name: String = "",
    @PropertyName("email") val email: String = "",
    @PropertyName("beginningWeekDay") val beginningWeekDay: Int = 1
)

fun UserDto.toDomain(): User {
    return User(name, email, beginningWeekDay)
}

fun UserDto.toMap(): Map<String, Any?> {
    return mapOf(
        Pair("name", name),
        Pair("email", email),
        Pair("beginningWeekDay", beginningWeekDay),
    )
}