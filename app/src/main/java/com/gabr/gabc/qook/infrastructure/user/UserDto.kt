package com.gabr.gabc.qook.infrastructure.user

import android.net.Uri
import com.gabr.gabc.qook.domain.user.User
import com.google.firebase.firestore.PropertyName

data class UserDto(
    @PropertyName("id") val id: String = "",
    @PropertyName("name") val name: String = "",
    @PropertyName("email") val email: String = "",
    @PropertyName("resetDay") val resetDay: Int = 1,
    @PropertyName("language") val language: String = "",
    @PropertyName("messagingToken") val messagingToken: String = "",
    @PropertyName("hasPhoto") val hasPhoto: Boolean = false,
)

fun UserDto.toDomain(): User {
    return User(id, name, email, resetDay, Uri.EMPTY, language, messagingToken, hasPhoto)
}

fun UserDto.toMap(): Map<String, Any?> {
    return mapOf(
        Pair("id", id),
        Pair("name", name),
        Pair("email", email),
        Pair("resetDay", resetDay),
        Pair("language", language),
        Pair("messagingToken", messagingToken),
        Pair("hasPhoto", hasPhoto),
    )
}