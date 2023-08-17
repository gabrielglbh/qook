package com.gabr.gabc.qook.presentation.homePage.viewModel

import com.gabr.gabc.qook.domain.user.User

data class UserState(
    val user: User? = null,
    val error: String = ""
)