package com.gabr.gabc.qook.presentation.homePage.viewModel

data class UserState(
    val name: String = "",
    val avatarUrl: String? = null,
    val error: String = ""
)