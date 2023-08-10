package com.gabr.gabc.qook.domain.user

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val name: String,
    val email: String,
    val beginningWeekDay: Int = 1,
    val avatar: String? = null,
)
