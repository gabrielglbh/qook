package com.gabr.gabc.qook.presentation.homePage.viewModel

import android.net.Uri

data class UserState(
    val name: String = "",
    val avatarUrl: Uri = Uri.EMPTY,
    val error: String = ""
)