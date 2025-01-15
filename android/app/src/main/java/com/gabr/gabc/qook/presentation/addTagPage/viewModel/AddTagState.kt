package com.gabr.gabc.qook.presentation.addTagPage.viewModel

import com.gabr.gabc.qook.domain.tag.Tag
import com.gabr.gabc.qook.presentation.theme.seed

data class AddTagState(
    val tag: Tag = Tag("", "", seed),
    val isUpdate: Boolean = false,
    val error: String = ""
)