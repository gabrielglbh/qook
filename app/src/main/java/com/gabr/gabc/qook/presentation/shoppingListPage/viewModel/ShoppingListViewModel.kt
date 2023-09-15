package com.gabr.gabc.qook.presentation.shoppingListPage.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabr.gabc.qook.domain.ingredients.Ingredients
import com.gabr.gabc.qook.domain.ingredients.IngredientsRepository
import com.gabr.gabc.qook.domain.planning.DayPlanning
import com.gabr.gabc.qook.domain.planning.PlanningRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    private val planningRepository: PlanningRepository,
    private val ingredientsRepository: IngredientsRepository,
) : ViewModel() {
    var ingredients = mutableStateMapOf<String, Boolean>()
        private set
    var planning = mutableStateListOf<DayPlanning>()
        private set
    var groupId = mutableStateOf<String?>(null)
        private set
    var isLoading = mutableStateOf(false)
        private set

    private fun setIngredients(i: Ingredients) {
        ingredients.clear()
        i.list.forEach { (key, value) ->
            ingredients[key] = value
        }
    }

    fun loadShoppingList(list: List<DayPlanning>?, groupId: String? = null) {
        this.groupId.value = groupId

        list?.let { planning ->
            this@ShoppingListViewModel.planning.addAll(planning)
        }
        viewModelScope.launch {
            if (groupId == null) {
                val result = ingredientsRepository.getIngredientsOfShoppingList()
                result.fold(
                    ifLeft = {},
                    ifRight = { i ->
                        setIngredients(i)
                    }
                )
            } else {
                ingredientsRepository.getIngredientsOfShoppingListFromSharedPlanning(groupId)
                    .collect { res ->
                        res.fold(
                            ifLeft = {},
                            ifRight = { i ->
                                setIngredients(i)
                            }
                        )
                    }
            }
        }
    }

    fun reloadIngredientsFromPlanning() {
        viewModelScope.launch {
            isLoading.value = true

            val ingredientsMapped = mutableMapOf<String, Boolean>()
            val auxPlanning = planning

            if (auxPlanning.isEmpty()) {
                if (groupId.value == null) {
                    val res = planningRepository.getPlanning()
                    res.fold(
                        ifLeft = {},
                        ifRight = { p ->
                            auxPlanning.addAll(p)
                        }
                    )
                } else {
                    planningRepository.getPlanningFromSharedPlanning(groupId.value!!)
                        .collect { res ->
                            res.fold(
                                ifLeft = {},
                                ifRight = { p ->
                                    auxPlanning.addAll(p)
                                }
                            )
                        }
                }

            }

            auxPlanning.forEach { day ->
                day.lunch.meal.ingredients.forEach { i ->
                    ingredientsMapped[i] = false
                }
                day.dinner.meal.ingredients.forEach { i ->
                    ingredientsMapped[i] = false
                }
            }

            ingredients.forEach { (key, value) ->
                ingredientsMapped[key] = value
            }

            val result = ingredientsRepository.updateIngredients(
                Ingredients(ingredientsMapped),
                groupId.value
            )
            result.fold(
                ifLeft = {},
                ifRight = {
                    setIngredients(Ingredients(ingredientsMapped))
                }
            )

            isLoading.value = false
        }
    }

    fun addIngredientToList(ingredient: String) {
        viewModelScope.launch {
            val result =
                ingredientsRepository.updateIngredient(Pair(ingredient, false), groupId.value)
            result.fold(
                ifLeft = {},
                ifRight = {
                    ingredients[ingredient] = false
                }
            )
        }
    }

    fun removeIngredientFromList(ingredient: Pair<String, Boolean>) {
        viewModelScope.launch {
            val result = ingredientsRepository.removeIngredient(ingredient, groupId.value)
            result.fold(
                ifLeft = {},
                ifRight = {
                    ingredients.remove(ingredient.first)
                }
            )
        }
    }

    fun updateIngredient(ingredient: Pair<String, Boolean>) {
        viewModelScope.launch {
            val result = ingredientsRepository.updateIngredient(
                ingredient.copy(second = !ingredient.second), groupId.value
            )
            result.fold(
                ifLeft = {},
                ifRight = {
                    ingredients[ingredient.first] = !ingredient.second
                }
            )
        }
    }
}