package com.gabr.gabc.qook.presentation.recipeDetailsPage.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.domain.recipe.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailsViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
) : ViewModel() {
    var recipe = mutableStateOf(Recipe.EMPTY_RECIPE)
        private set
    var isUpdate = mutableStateOf(false)
        private set
    var isLoading = mutableStateOf(false)
        private set

    fun updateRecipe(r: Recipe) {
        recipe.value = r
    }

    fun isUpdating(value: Boolean) {
        isUpdate.value = value
    }

    fun removeRecipe(onError: (String) -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            val res = recipeRepository.removeRecipe(recipe.value)
            res.fold(
                ifLeft = { e -> onError(e.error) },
                ifRight = { onSuccess() }
            )
            isLoading.value = false
        }
    }
}