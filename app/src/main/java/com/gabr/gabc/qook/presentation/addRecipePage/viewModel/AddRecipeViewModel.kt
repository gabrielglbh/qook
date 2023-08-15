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

    fun addTag(tag: Tag) {
        val value = recipeState.value
        _recipeState.value = value.copy(
            recipe = value.recipe.copy(
                tags = value.recipe.tags + tag,
            )
        )
    }

    fun deleteTag(tag: Tag) {
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

    fun gatherTags() {
        viewModelScope.launch(Dispatchers.IO) {
            val res = tagsRepository.getTags()
            res.fold(
                ifLeft = {},
                ifRight = {
                    _recipeState.value = _recipeState.value.copy(
                        createdTags = it
                    )
                }
            )
        }
    }
}