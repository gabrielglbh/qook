package com.gabr.gabc.qook.presentation.recipeDetailsPage.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.gabr.gabc.qook.domain.recipe.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecipeDetailsViewModel @Inject constructor() : ViewModel() {
    var recipe = mutableStateOf(Recipe.EMPTY_RECIPE)
        private set
    var isUpdate = mutableStateOf(false)
        private set

    fun updateRecipe(r: Recipe) {
        recipe.value = r
    }

    fun isUpdating(value: Boolean) {
        isUpdate.value = value
    }
}