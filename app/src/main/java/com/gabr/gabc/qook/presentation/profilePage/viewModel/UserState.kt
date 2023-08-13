package com.gabr.gabc.qook.presentation.profilePage.viewModel

import android.net.Uri
import com.gabr.gabc.qook.domain.user.User

data class UserState(
    val user: User? = null,
    val avatarUrl: Uri = Uri.EMPTY,
    val error: String = ""
)