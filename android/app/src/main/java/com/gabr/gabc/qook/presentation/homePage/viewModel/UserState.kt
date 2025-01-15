package com.gabr.gabc.qook.presentation.homePage.viewModel

import com.gabr.gabc.qook.domain.user.User

data class UserState(
    val user: User = User.EMPTY,
    val error: String = ""
)