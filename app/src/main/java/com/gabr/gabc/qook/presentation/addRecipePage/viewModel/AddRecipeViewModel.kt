package com.gabr.gabc.qook.presentation.addRecipePage.viewModel

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.gabr.gabc.qook.domain.recipe.Easiness
import com.gabr.gabc.qook.domain.tag.Tag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AddRecipeViewModel @Inject constructor() : ViewModel() {
    private val _recipeState = MutableStateFlow(AddRecipeState())
    val recipeState: StateFlow<AddRecipeState> = _recipeState.asStateFlow()

    fun updateMetadata(
        name: String? = null,
        photo: Uri? = null,
        easiness: Easiness? = null,
        time: String? = null,
    ) {
        val value = recipeState.value
        val recipe = value.recipe
        _recipeState.value = value.copy(
            recipe = recipe.copy(
                name = name ?: recipe.name,
                photo = photo ?: recipe.photo,
                easiness = easiness ?: recipe.easiness,
                time = time ?: recipe.time,
            )
        )
    }

    fun updateTags(tags: List<Tag>) {
        val value = recipeState.value
        _recipeState.value = value.copy(
            recipe = value.recipe.copy(
                tags = tags,
            )
        )
    }

    fun gatherTags() {
        _recipeState.value = _recipeState.value.copy(
            createdTags = listOf(
                Tag("Burger", Color.White, Color.Black),
                Tag("Fish", Color.White, Color.Blue),
                Tag("Veggie", Color.Black, Color.Green),
                Tag("Lovers Lovers Lovers", Color.White, Color.Red),
                Tag("Burger", Color.White, Color.Black),
                Tag("Fish", Color.White, Color.Blue),
                Tag("Veggie", Color.Black, Color.Green),
                Tag("Lovers Lovers Lovers", Color.White, Color.Red),
                Tag("Burger", Color.White, Color.Black),
                Tag("Fish", Color.White, Color.Blue),
                Tag("Veggie", Color.Black, Color.Green),
                Tag("Lovers Lovers Lovers", Color.White, Color.Red),
                Tag("Burger", Color.White, Color.Black),
                Tag("Fish", Color.White, Color.Blue),
                Tag("Veggie", Color.Black, Color.Green),
                Tag("Lovers Lovers Lovers", Color.White, Color.Red)
            )
        )
    }
}