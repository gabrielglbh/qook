package com.gabr.gabc.qook.presentation.planningPage.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabr.gabc.qook.domain.planning.DayPlanning
import com.gabr.gabc.qook.domain.planning.PlanningRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanningViewModel @Inject constructor(
    private val planningRepository: PlanningRepository
) : ViewModel() {
    var planning = mutableStateOf<List<DayPlanning>?>(null)
        private set
    var isLoading = mutableStateOf(false)
        private set
    var hasUpdated = mutableStateOf(false)
        private set

    fun setDataForLocalLoading(planning: List<DayPlanning>) {
        this.planning.value = planning
    }

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

    fun resetPlanning(onError: (String) -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            val res = planningRepository.resetPlanning()
            res.fold(
                ifLeft = { e -> onError(e.error) },
                ifRight = {
                    hasUpdated.value = true
                    planning.value = DayPlanning.EMPTY_PLANNING
                }
            )
            isLoading.value = false
        }
    }

    fun updatePlanning(dayPlanning: DayPlanning, onError: (String) -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            val res = planningRepository.updateRecipeFromPlanning(dayPlanning)
            res.fold(
                ifLeft = { e -> onError(e.error) },
                ifRight = {
                    hasUpdated.value = true
                    updatePlanningLocally(dayPlanning)
                }
            )
            isLoading.value = false
        }
    }

    fun updatePlanningLocally(dayPlanning: DayPlanning) {
        val auxPlanning = mutableListOf<DayPlanning>().apply { addAll(planning.value ?: listOf()) }
        val aux = auxPlanning.map { it.id }
        val dayPlanningToUpdateIndex = aux.indexOf(dayPlanning.id)
        auxPlanning[dayPlanningToUpdateIndex] = dayPlanning
        planning.value = auxPlanning
    }
}