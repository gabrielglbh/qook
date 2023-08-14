package com.gabr.gabc.qook.presentation.addRecipePage.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.gabr.gabc.qook.domain.recipe.Easiness
import com.gabr.gabc.qook.domain.recipe.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddRecipeViewModel @Inject constructor() : ViewModel() {
    private val _recipeState = MutableStateFlow(
        Recipe(
            name = "",
            creationDate = LocalDate.now(),
            updateDate = LocalDate.now(),
            easiness = Easiness.EASY,
            time = "",
            photo = Uri.EMPTY,
            description = "",
            ingredients = listOf(),
            tags = listOf()
        )
    )
    val recipeState: StateFlow<Recipe> = _recipeState.asStateFlow()

    fun updateMetadata(
        name: String? = null,
        photo: Uri? = null,
        easiness: Easiness? = null,
        time: String? = null,
    ) {
        val value = recipeState.value
        _recipeState.value = value.copy(
            name = name ?: value.name,
            photo = photo ?: value.photo,
            easiness = easiness ?: value.easiness,
            time = time ?: value.time,
        )
    }
}