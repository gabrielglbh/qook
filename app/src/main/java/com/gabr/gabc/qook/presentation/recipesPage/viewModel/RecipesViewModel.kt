package com.gabr.gabc.qook.presentation.recipesPage.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabr.gabc.qook.domain.ingredients.Ingredients
import com.gabr.gabc.qook.domain.ingredients.IngredientsRepository
import com.gabr.gabc.qook.domain.planning.DayPlanning
import com.gabr.gabc.qook.domain.planning.PlanningRepository
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
    private val tagRepository: TagRepository,
    private val planningRepository: PlanningRepository,
    private val ingredientsRepository: IngredientsRepository,
) : ViewModel() {
    private val _recipesState = MutableStateFlow(RecipesState())
    val recipesState: StateFlow<RecipesState> = _recipesState.asStateFlow()

    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    private val _planningState = MutableStateFlow(PlanningState())
    val planningState: StateFlow<PlanningState> = _planningState.asStateFlow()

    var isLoadingRecipes = mutableStateOf(false)
        private set

    private fun alterListForUpdate(recipe: Recipe, list: List<Recipe>): List<Recipe> {
        val aux = mutableListOf<Recipe>().apply { addAll(list) }
        val auxIds = aux.map { it.id }
        if (auxIds.contains(recipe.id)) aux[auxIds.indexOf(recipe.id)] = recipe
        return aux
    }

    private fun alterListForDelete(recipe: Recipe, list: List<Recipe>): List<Recipe> {
        val aux = mutableListOf<Recipe>().apply { addAll(list) }
        val auxIds = aux.map { it.id }
        if (auxIds.contains(recipe.id)) aux.removeAt(auxIds.indexOf(recipe.id))
        return aux
    }

    private fun alterListForAddition(recipe: Recipe, list: List<Recipe>): List<Recipe> {
        return mutableListOf<Recipe>().apply {
            add(recipe)
            addAll(list)
        }
    }

    fun loadRecipesLocallyIfAny(
        recipes: List<Recipe>?,
        errorTags: (String) -> Unit,
        errorRecipes: (String) -> Unit
    ) {
        viewModelScope.launch {
            if (recipes.isNullOrEmpty()) {
                isLoadingRecipes.value = true
                val result = recipeRepository.getRecipes()
                result.fold(
                    ifLeft = { e -> errorRecipes(e.error) },
                    ifRight = { recipes ->
                        _recipesState.value = _recipesState.value.copy(
                            recipes = recipes,
                            searchedRecipes = recipes
                        )
                    }
                )
                isLoadingRecipes.value = false
            } else {
                _recipesState.value = _recipesState.value.copy(
                    recipes = recipes,
                    searchedRecipes = recipes,
                )
            }
            val result = tagRepository.getTags()
            result.fold(
                ifLeft = { e -> errorTags(e.error) },
                ifRight = { tags ->
                    _recipesState.value = _recipesState.value.copy(
                        tags = tags
                    )
                }
            )
        }
    }

    fun onSearch() {
        viewModelScope.launch {
            isLoadingRecipes.value = true
            val result = recipeRepository.getRecipes(
                orderBy = searchState.value.orderBy,
                query = searchState.value.query,
                tagId = searchState.value.tag?.id
            )
            result.fold(
                ifLeft = {
                    clearSearch()
                },
                ifRight = { recipes ->
                    _recipesState.value = _recipesState.value.copy(
                        searchedRecipes = recipes
                    )
                }
            )
            isLoadingRecipes.value = false
        }
    }

    fun clearSearch() {
        _recipesState.value = _recipesState.value.copy(
            searchedRecipes = _recipesState.value.recipes
        )
    }

    fun updateSearchState(searchState: SearchState) {
        _searchState.value = searchState
    }

    fun updatePlanning(value: PlanningState) {
        _planningState.value = value
    }

    fun updatePlanningWith(
        recipe: Recipe,
        onError: (String) -> Unit,
        onSuccess: (Recipe, DayPlanning) -> Unit
    ) {
        viewModelScope.launch {
            isLoadingRecipes.value = true

            val groupId = _planningState.value.groupId
            val ingredients = mutableMapOf<String, Boolean>()
            recipe.ingredients.forEach { i -> ingredients[i] = false }

            _planningState.value.isLunch?.let { isLunch ->
                val dayPlanning = if (isLunch) {
                    _planningState.value.dayPlanning.copy(lunch = recipe)
                } else {
                    _planningState.value.dayPlanning.copy(dinner = recipe)
                }

                val result = planningRepository.updateRecipeFromPlanning(dayPlanning, groupId)
                result.fold(
                    ifLeft = { e -> onError(e.error) },
                    ifRight = {
                        val iResult =
                            ingredientsRepository.updateIngredients(
                                Ingredients(ingredients),
                                groupId
                            )
                        iResult.fold(
                            ifLeft = { e -> onError(e.error) },
                            ifRight = { onSuccess(recipe, dayPlanning) }
                        )
                    }
                )
            }
            isLoadingRecipes.value = false
        }
    }

    fun updateRecipeLocally(recipe: Recipe) {
        val value = recipesState.value
        _recipesState.value = value.copy(
            recipes = alterListForUpdate(recipe, value.recipes),
            searchedRecipes = alterListForUpdate(recipe, value.searchedRecipes)
        )
    }

    fun deleteRecipeLocally(recipe: Recipe) {
        val value = recipesState.value
        _recipesState.value = value.copy(
            recipes = alterListForDelete(recipe, value.recipes),
            searchedRecipes = alterListForDelete(recipe, value.searchedRecipes)
        )
    }

    fun addRecipeLocally(recipe: Recipe) {
        val value = recipesState.value
        _recipesState.value = value.copy(
            recipes = alterListForAddition(recipe, value.recipes),
            searchedRecipes = alterListForAddition(recipe, value.searchedRecipes)
        )
    }
}