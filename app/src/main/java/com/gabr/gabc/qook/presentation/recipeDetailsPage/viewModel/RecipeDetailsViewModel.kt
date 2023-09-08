package com.gabr.gabc.qook.presentation.recipeDetailsPage.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.domain.recipe.RecipeRepository
import com.gabr.gabc.qook.domain.user.User
import com.gabr.gabc.qook.domain.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RecipeDetailsViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    var recipe = mutableStateOf(Recipe.EMPTY_RECIPE)
        private set
    var op = mutableStateOf<User?>(null)
        private set
    var currentUserUid = mutableStateOf<String?>(null)
        private set
    var isUpdate = mutableStateOf(false)
        private set
    var isLoading = mutableStateOf(false)
        private set
    var canUpdate = mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            currentUserUid.value = userRepository.getCurrentUser()?.uid
        }
    }

    fun updateRecipe(r: Recipe) {
        recipe.value = r
    }

    fun updateOp(op: User?) {
        this.op.value = op
    }

    fun isUpdating(value: Boolean) {
        isUpdate.value = value
    }

    fun canUpdate(value: Boolean) {
        canUpdate.value = value
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

    fun addToMyOwnRecipes(onError: (String) -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            val res = recipeRepository.createRecipe(
                recipe.value.copy(
                    creationDate = Date(),
                    updateDate = Date(),
                )
            )
            res.fold(
                ifLeft = { e -> onError(e.error) },
                ifRight = {}
            )
            isLoading.value = false
        }
    }
}