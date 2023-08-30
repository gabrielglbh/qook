package com.gabr.gabc.qook.infrastructure.user

import com.gabr.gabc.qook.domain.user.User
import com.google.firebase.firestore.PropertyName

data class UserDto(
    @PropertyName("name") val name: String = "",
    @PropertyName("email") val email: String = "",
    @PropertyName("resetDay") val resetDay: Int = 1,
    @PropertyName("language") val language: String = ""
)

fun UserDto.toDomain(): User {
    return User(name, email, resetDay)
}

fun UserDto.toMap(): Map<String, Any?> {
    return mapOf(
        Pair("name", name),
        Pair("email", email),
        Pair("resetDay", resetDay),
        Pair("language", language),
    )
}