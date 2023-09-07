package com.gabr.gabc.qook.presentation.planningPage.viewModel

import androidx.compose.runtime.mutableStateListOf
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
class PlanningViewModel @Inject constructor(
    private val planningRepository: PlanningRepository,
    private val ingredientsRepository: IngredientsRepository,
    private val recipeRepository: RecipeRepository,
    private val sharedPlanningRepository: SharedPlanningRepository,
) : ViewModel() {
    var sharedPlanning = mutableStateOf(SharedPlanning.EMPTY_SHARED_PLANNING)
        private set
    var planning = mutableStateListOf<DayPlanning>()
        private set
    var recipes = mutableStateListOf<Recipe>()
        private set
    var groupId = mutableStateOf<String?>(null)
        private set
    var isLoading = mutableStateOf(false)
        private set
    var hasUpdated = mutableStateOf(false)
        private set

    init {
        loadRecipes()
    }

    fun loadPlanning(planning: List<DayPlanning>?, groupId: String?, onError: (String) -> Unit) {
        this.groupId.value = groupId
        viewModelScope.launch {
            isLoading.value = true
            if (groupId == null) {
                if (planning == null) {
                    val res = planningRepository.getPlanning()
                    res.fold(
                        ifLeft = { e -> onError(e.error) },
                        ifRight = { p ->
                            this@PlanningViewModel.planning.clear()
                            this@PlanningViewModel.planning.addAll(p)
                        }
                    )
                } else {
                    this@PlanningViewModel.planning.clear()
                    this@PlanningViewModel.planning.addAll(planning)
                }
            } else {
                val res = sharedPlanningRepository.getSharedPlanning(groupId)
                res.fold(
                    ifLeft = { e -> onError(e.error) },
                    ifRight = { sp ->
                        sharedPlanning.value = sp
                        this@PlanningViewModel.planning.clear()
                        this@PlanningViewModel.planning.addAll(sharedPlanning.value.planning)
                    },
                )
            }
            isLoading.value = false
        }
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            val res = recipeRepository.getRecipes()
            res.fold(
                ifLeft = {},
                ifRight = { recipes ->
                    this@PlanningViewModel.recipes.addAll(recipes)
                }
            )
        }
    }

    fun resetPlanning(onError: (String) -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            val res = planningRepository.resetPlanning(groupId.value)
            res.fold(
                ifLeft = { e -> onError(e.error) },
                ifRight = {
                    val iResult = ingredientsRepository.resetIngredients(groupId.value)
                    iResult.fold(
                        ifLeft = { e -> onError(e.error) },
                        ifRight = {
                            hasUpdated.value = true
                            planning.clear()
                            planning.addAll(DayPlanning.EMPTY_PLANNING)
                            sharedPlanning.value.copy(
                                planning = DayPlanning.EMPTY_PLANNING,
                                shoppingList = Ingredients(mapOf())
                            )
                        }
                    )
                }
            )
            isLoading.value = false
        }
    }

    fun updatePlanning(dayPlanning: DayPlanning, onError: (String) -> Unit) {
        viewModelScope.launch {
            isLoading.value = true

            val ingredients = mutableMapOf<String, Boolean>()
            val pastDayPlanning = planning.find { dp -> dp.id == dayPlanning.id }
            pastDayPlanning?.lunch?.meal?.ingredients?.forEach { i -> ingredients[i] = false }
            pastDayPlanning?.dinner?.meal?.ingredients?.forEach { i -> ingredients[i] = false }

            val res =
                planningRepository.updateRecipeFromPlanning(dayPlanning, groupId = groupId.value)
            res.fold(
                ifLeft = { e -> onError(e.error) },
                ifRight = {
                    val iResult = ingredientsRepository.removeIngredients(
                        Ingredients(ingredients),
                        groupId.value
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

    fun addUserToGroup(id: String, onError: (String) -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            val res = sharedPlanningRepository.addUserToSharedPlanning(id)
            res.fold(
                ifLeft = { e -> onError(e.error) },
                ifRight = {
                    loadPlanning(null, id) { e -> onError(e) }
                },
            )
            isLoading.value = false
        }
    }

    fun updatePlanningLocally(dayPlanning: DayPlanning) {
        val auxPlanning = mutableListOf<DayPlanning>().apply { addAll(planning) }
        val aux = planning.map { it.id }
        val dayPlanningToUpdateIndex = aux.indexOf(dayPlanning.id)
        planning[dayPlanningToUpdateIndex] = dayPlanning
        sharedPlanning.value = sharedPlanning.value.copy(planning = auxPlanning)
        hasUpdated.value = true
    }
}