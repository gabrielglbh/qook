package com.gabr.gabc.qook.presentation.sharedPlanningPage.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabr.gabc.qook.domain.sharedPlanning.SharedPlanning
import com.gabr.gabc.qook.domain.sharedPlanning.SharedPlanningRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedPlanningViewModel @Inject constructor(
    private val sharedPlanningRepository: SharedPlanningRepository,
) : ViewModel() {
    var sharedPlanning = mutableStateOf(SharedPlanning.EMPTY_SHARED_PLANNING)
        private set
    var isLoading = mutableStateOf(false)
        private set

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
}