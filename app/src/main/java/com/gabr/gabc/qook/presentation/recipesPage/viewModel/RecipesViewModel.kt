package com.gabr.gabc.qook.presentation.recipesPage.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.domain.recipe.RecipeRepository
import com.gabr.gabc.qook.domain.tag.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val tagRepository: TagRepository
) : ViewModel() {
    private val _recipesState = MutableStateFlow(RecipesState())
    val recipesState: StateFlow<RecipesState> = _recipesState.asStateFlow()

    var isLoading = mutableStateOf(false)
        private set

    private fun alterListForUpdate(recipe: Recipe, list: List<Recipe>): List<Recipe> {
        val aux = mutableListOf<Recipe>().apply { addAll(list) }
        val auxIds = aux.map { it.id }
        if (auxIds.contains(recipe.id)) aux[auxIds.indexOf(recipe.id)] = recipe
        return aux
    }

    fun getRecipes(onError: (String) -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            val result = recipeRepository.getRecipes()
            result.fold(
                ifLeft = { e -> onError(e.error) },
                ifRight = { recipes ->
                    _recipesState.value = _recipesState.value.copy(
                        recipes = recipes,
                        searchedRecipes = recipes
                    )
                }
            )
            isLoading.value = false
        }
    }

    fun getTags(onError: (String) -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            val result = tagRepository.getTags()
            result.fold(
                ifLeft = { e -> onError(e.error) },
                ifRight = { tags ->
                    _recipesState.value = _recipesState.value.copy(
                        tags = tags
                    )
                }
            )
            isLoading.value = false
        }
    }

    fun onSearchUpdate(query: String) {
        val value = recipesState.value

        if (query.trim().isEmpty()) {
            _recipesState.value = value.copy(
                searchedRecipes = value.recipes
            )
        } else {
            // TODO: Do search online?
            val aux = mutableListOf<Recipe>().apply { addAll(value.searchedRecipes) }
            val filtered = aux.filter { recipe ->
                recipe.name.contains(query, ignoreCase = true) ||
                        recipe.description.contains(query, ignoreCase = true)
            }

            _recipesState.value = value.copy(
                searchedRecipes = filtered
            )
        }
    }

    fun updateRecipeLocally(recipe: Recipe) {
        val value = recipesState.value
        _recipesState.value = value.copy(
            recipes = alterListForUpdate(recipe, value.recipes),
            searchedRecipes = alterListForUpdate(recipe, value.searchedRecipes)
        )
    }
}