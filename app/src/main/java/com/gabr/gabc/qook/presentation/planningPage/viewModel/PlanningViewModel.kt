package com.gabr.gabc.qook.presentation.planningPage.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabr.gabc.qook.domain.planning.Planning
import com.gabr.gabc.qook.domain.planning.PlanningRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanningViewModel @Inject constructor(
    private val planningRepository: PlanningRepository
) : ViewModel() {
    var planning = mutableStateOf(Planning.EMPTY_PLANNING)
        private set
    var isLoading = mutableStateOf(false)
        private set

    fun loadPlanning(onError: (String) -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            val res = planningRepository.getPlanning()
            res.fold(
                ifLeft = { e -> onError(e.error) },
                ifRight = { p ->
                    planning.value = p
                }
            )
            isLoading.value = false
        }
    }
}