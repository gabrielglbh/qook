package com.gabr.gabc.qook.presentation.recipesPage.viewModel

import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.domain.tag.Tag

data class RecipesState(
    val recipes: List<Recipe> = listOf(),
    val tags: List<Tag> = listOf(),
    val searchedRecipes: List<Recipe> = listOf()
)
