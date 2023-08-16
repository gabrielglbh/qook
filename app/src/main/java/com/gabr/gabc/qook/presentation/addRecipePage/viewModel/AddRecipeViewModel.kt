package com.gabr.gabc.qook.presentation.addRecipePage.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabr.gabc.qook.domain.recipe.Easiness
import com.gabr.gabc.qook.domain.tag.Tag
import com.gabr.gabc.qook.domain.tag.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddRecipeViewModel @Inject constructor(
    private val tagsRepository: TagRepository
) : ViewModel() {
    private val _recipeState = MutableStateFlow(AddRecipeState())
    val recipeState: StateFlow<AddRecipeState> = _recipeState.asStateFlow()

    init {
        gatherTags()
    }

    private fun gatherTags() {
        viewModelScope.launch(Dispatchers.IO) {
            val res = tagsRepository.getTags()
            res.fold(
                ifLeft = {},
                ifRight = {
                    _recipeState.value = _recipeState.value.copy(
                        createdTags = it,
                        searchedTags = it
                    )
                }
            )
        }
    }

    private fun alterListForDeletion(id: String, list: List<Tag>): List<Tag> {
        val aux = mutableListOf<Tag>().apply { addAll(list) }
        val auxIds = aux.map { it.id }
        if (auxIds.contains(id)) aux.removeAt(auxIds.indexOf(id))
        return aux
    }

    private fun alterListForUpdate(tag: Tag, list: List<Tag>): List<Tag> {
        val aux = mutableListOf<Tag>().apply { addAll(list) }
        val auxIds = aux.map { it.id }
        if (auxIds.contains(tag.id)) aux[auxIds.indexOf(tag.id)] = tag
        return aux
    }

    fun updateMetadata(
        name: String? = null,
        photo: Uri? = null,
        easiness: Easiness? = null,
        time: String? = null,
        ingredients: List<String>? = null
    ) {
        val value = recipeState.value
        val recipe = value.recipe
        _recipeState.value = value.copy(
            recipe = recipe.copy(
                name = name ?: recipe.name,
                photo = photo ?: recipe.photo,
                easiness = easiness ?: recipe.easiness,
                time = time ?: recipe.time,
                ingredients = ingredients ?: recipe.ingredients
            )
        )
    }

    fun addTagToRecipe(tag: Tag) {
        val value = recipeState.value
        _recipeState.value = value.copy(
            recipe = value.recipe.copy(
                tags = value.recipe.tags + tag,
            )
        )
    }

    fun deleteTagFromRecipe(tag: Tag) {
        val value = recipeState.value
        val aux = mutableListOf<Tag>().apply {
            addAll(value.recipe.tags)
            remove(tag)
        }
        _recipeState.value = value.copy(
            recipe = value.recipe.copy(
                tags = aux
            )
        )
    }

    fun addIngredientToRecipe(ingredient: String) {
        val value = recipeState.value
        _recipeState.value = value.copy(
            recipe = value.recipe.copy(
                ingredients = value.recipe.ingredients + ingredient,
            )
        )
    }

    fun deleteIngredientFromRecipe(ingredient: String) {
        val value = recipeState.value
        val aux = mutableListOf<String>().apply {
            addAll(value.recipe.ingredients)
            remove(ingredient)
        }
        _recipeState.value = value.copy(
            recipe = value.recipe.copy(
                ingredients = aux
            )
        )
    }

    fun updateIngredientFromRecipe(index: Int, ingredient: String) {
        val value = recipeState.value
        val aux = mutableListOf<String>().apply {
            addAll(value.recipe.ingredients)
        }
        aux[index] = ingredient
        _recipeState.value = value.copy(
            recipe = value.recipe.copy(
                ingredients = aux
            )
        )
    }

    fun deleteTagForLocalLoading(id: String) {
        val value = recipeState.value
        val newCreatedTags = alterListForDeletion(id, value.createdTags)
        _recipeState.value = value.copy(
            recipe = value.recipe.copy(
                tags = alterListForDeletion(id, value.recipe.tags)
            ),
            createdTags = newCreatedTags,
            searchedTags = newCreatedTags
        )
    }

    fun updateTagForLocalLoading(tag: Tag) {
        val value = recipeState.value
        val newCreatedTags = alterListForUpdate(tag, value.createdTags)
        _recipeState.value = value.copy(
            recipe = value.recipe.copy(
                tags = alterListForUpdate(tag, value.recipe.tags)
            ),
            createdTags = newCreatedTags,
            searchedTags = newCreatedTags
        )
    }

    fun createTagForLocalLoading(tag: Tag) {
        val value = recipeState.value
        val newCreatedTags = mutableListOf<Tag>().apply {
            add(tag)
            addAll(value.createdTags)
        }
        _recipeState.value = value.copy(
            createdTags = newCreatedTags,
            searchedTags = newCreatedTags
        )
    }

    fun onSearchUpdate(query: String) {
        val value = recipeState.value

        if (query.trim().isEmpty()) {
            _recipeState.value = value.copy(
                searchedTags = value.createdTags
            )
        } else {
            val aux = mutableListOf<Tag>().apply { addAll(value.searchedTags) }
            val filtered = aux.filter { tag -> tag.text.contains(query, ignoreCase = true) }

            _recipeState.value = value.copy(
                searchedTags = filtered
            )
        }
    }
}