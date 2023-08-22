package com.gabr.gabc.qook.presentation.recipesPage.viewModel

import com.gabr.gabc.qook.domain.tag.Tag
import com.gabr.gabc.qook.presentation.shared.Globals

data class SearchState(
    val orderBy: String = Globals.OBJ_RECIPE_CREATION,
    val ascending: Boolean = true,
    val query: String = "",
    val tag: Tag? = null
)