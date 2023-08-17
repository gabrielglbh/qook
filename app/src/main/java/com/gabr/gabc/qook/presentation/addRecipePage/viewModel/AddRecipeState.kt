package com.gabr.gabc.qook.presentation.addRecipePage.viewModel

import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.domain.tag.Tag

data class AddRecipeState(
    val recipe: Recipe = Recipe.EMPTY_RECIPE,
    val createdTags: List<Tag> = listOf(),
    val searchedTags: List<Tag> = listOf(),
    val originalRecipe: Recipe = Recipe.EMPTY_RECIPE
)