package com.gabr.gabc.qook.presentation.addTagPage.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabr.gabc.qook.domain.tag.Tag
import com.gabr.gabc.qook.domain.tag.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddTagViewModel @Inject constructor(
    private val tagsRepository: TagRepository
) : ViewModel() {
    private val _formState = MutableStateFlow(AddTagState())
    val formState: StateFlow<AddTagState> = _formState.asStateFlow()

    var isLoading by mutableStateOf(false)
        private set

    private fun setIsLoading(value: Boolean) {
        isLoading = value
    }

    fun updateForm(state: AddTagState) {
        _formState.value = state
    }

    fun deleteTag(ifError: (String) -> Unit, ifSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { setIsLoading(true) }
            val res = tagsRepository.removeTag(_formState.value.tag.id)
            res.fold(
                ifLeft = {
                    ifError(it.error)
                },
                ifRight = {
                    ifSuccess()
                }
            )
            withContext(Dispatchers.Main) { setIsLoading(false) }
        }
    }

    fun createTag(tag: Tag, ifError: (String) -> Unit, ifSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { setIsLoading(true) }
            val res = tagsRepository.createTag(tag)
            res.fold(
                ifLeft = {
                    ifError(it.error)
                },
                ifRight = {
                    ifSuccess()
                }
            )
            withContext(Dispatchers.Main) { setIsLoading(false) }
        }
    }

    fun updateTag(tag: Tag, ifError: (String) -> Unit, ifSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { setIsLoading(true) }
            val res = tagsRepository.updateTag(tag)
            res.fold(
                ifLeft = {
                    ifError(it.error)
                },
                ifRight = {
                    ifSuccess()
                }
            )
            withContext(Dispatchers.Main) { setIsLoading(false) }
        }
    }
}