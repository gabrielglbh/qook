package com.gabr.gabc.qook.presentation.homePage.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabr.gabc.qook.domain.planning.DayPlanning
import com.gabr.gabc.qook.domain.planning.PlanningRepository
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.domain.recipe.RecipeRepository
import com.gabr.gabc.qook.domain.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val planningRepository: PlanningRepository,
    private val recipeRepository: RecipeRepository,
) : ViewModel() {
    private val _userState = MutableStateFlow(UserState())
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    var planning = mutableStateListOf<DayPlanning>()
        private set

    fun getUser(onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = userRepository.getUser()
            result.fold(
                ifLeft = {
                    _userState.value = _userState.value.copy(error = it.error)
                    onError(it.error)
                },
                ifRight = { user ->
                    userRepository.updateFCM(user)
                    _userState.value = _userState.value.copy(user = user)
                }
            )
        }
    }

    fun getPlanning() {
        viewModelScope.launch {
            val result = planningRepository.getPlanning()
            result.fold(
                ifLeft = {},
                ifRight = { p ->
                    if (p.isEmpty()) {
                        getPlanning()
                    } else {
                        updatePlanningLocally(p)
                    }
                }
            )
        }
    }

    fun getRandomRecipe(onSuccess: (Recipe) -> Unit, onEmptyRecipes: () -> Unit) {
        viewModelScope.launch {
            val result = recipeRepository.getRecipes()
            result.fold(
                ifLeft = {},
                ifRight = { recipes ->
                    if (recipes.isNotEmpty()) {
                        val random = Random.nextInt(0, recipes.size)
                        onSuccess(recipes[random])
                    } else {
                        onEmptyRecipes()
                    }
                }
            )
        }
    }

    fun updatePlanningLocally(planning: List<DayPlanning>) {
        this.planning.clear()
        this.planning.addAll(planning)
    }
}