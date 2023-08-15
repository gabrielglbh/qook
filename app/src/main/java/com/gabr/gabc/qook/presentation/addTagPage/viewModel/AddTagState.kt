package com.gabr.gabc.qook.presentation.addTagPage.viewModel

import androidx.compose.ui.graphics.Color
import com.gabr.gabc.qook.domain.tag.Tag

data class AddTagState(
    val tag: Tag = Tag("Tag name", Color.White, Color.Transparent),
    val error: String = ""
)