package com.gabr.gabc.qook.presentation.sharedPlanningPage.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabr.gabc.qook.domain.ingredients.Ingredients
import com.gabr.gabc.qook.domain.ingredients.IngredientsRepository
import com.gabr.gabc.qook.domain.planning.DayPlanning
import com.gabr.gabc.qook.domain.planning.PlanningRepository
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.domain.recipe.RecipeRepository
import com.gabr.gabc.qook.domain.sharedPlanning.SharedPlanning
import com.gabr.gabc.qook.domain.sharedPlanning.SharedPlanningRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedPlanningViewModel @Inject constructor(
    private val sharedPlanningRepository: SharedPlanningRepository,
    private val recipeRepository: RecipeRepository,
    private val planningRepository: PlanningRepository,
    private val ingredientsRepository: IngredientsRepository,
) : ViewModel() {
    var sharedPlanning = mutableStateOf(SharedPlanning.EMPTY_SHARED_PLANNING)
        private set
    var recipes = mutableStateOf<List<Recipe>?>(null)
        private set
    var isLoading = mutableStateOf(false)
        private set

    init {
        loadRecipes()
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            val res = recipeRepository.getRecipes()
            res.fold(
                ifLeft = {},
                ifRight = { r ->
                    recipes.value = r
                }
            )
        }
    }

    fun getSharedPlanning(id: String, onError: (String) -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            val res = sharedPlanningRepository.getSharedPlanning(id)
            res.fold(
                ifLeft = { e -> onError(e.error) },
                ifRight = { sp -> sharedPlanning.value = sp },
            )
            isLoading.value = false
        }
    }

    fun addUserToGroup(id: String, onError: (String) -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            val res = sharedPlanningRepository.addUserToSharedPlanning(id)
            res.fold(
                ifLeft = { e -> onError(e.error) },
                ifRight = {
                    getSharedPlanning(id) {}
                },
            )
            isLoading.value = false
        }
    }

    fun updatePlanning(dayPlanning: DayPlanning, onError: (String) -> Unit) {
        viewModelScope.launch {
            isLoading.value = true

            val ingredients = mutableMapOf<String, Boolean>()
            val pastDayPlanning =
                sharedPlanning.value.planning.find { dp -> dp.id == dayPlanning.id }
            val groupId = sharedPlanning.value.id

            pastDayPlanning?.lunch?.ingredients?.forEach { i -> ingredients[i] = false }
            pastDayPlanning?.dinner?.ingredients?.forEach { i -> ingredients[i] = false }

            val res = planningRepository.updateRecipeFromPlanning(dayPlanning, groupId = groupId)
            res.fold(
                ifLeft = { e -> onError(e.error) },
                ifRight = {
                    val iResult = ingredientsRepository.removeIngredients(
                        Ingredients(ingredients),
                        groupId = groupId
                    )
                    iResult.fold(
                        ifLeft = { e -> onError(e.error) },
                        ifRight = {
                            updatePlanningLocally(dayPlanning)
                        }
                    )
                }
            )
            isLoading.value = false
        }
    }

    fun updatePlanningLocally(dayPlanning: DayPlanning) {
        val auxPlanning =
            mutableListOf<DayPlanning>().apply { addAll(sharedPlanning.value.planning) }
        val aux = auxPlanning.map { it.id }
        val dayPlanningToUpdateIndex = aux.indexOf(dayPlanning.id)
        auxPlanning[dayPlanningToUpdateIndex] = dayPlanning
        sharedPlanning.value = sharedPlanning.value.copy(planning = auxPlanning)
        // hasUpdated.value = true
    }
}