package com.gabr.gabc.qook.presentation.planningPage.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabr.gabc.qook.domain.planning.DayPlanning
import com.gabr.gabc.qook.domain.planning.Planning
import com.gabr.gabc.qook.domain.planning.PlanningRepository
import com.gabr.gabc.qook.presentation.shared.Globals
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

    fun updatePlanningLocally(dayPlanning: DayPlanning) {
        when (dayPlanning.id) {
            Globals.OBJ_PLANNING_FIRST_DAY -> planning.value =
                planning.value.copy(firstDay = dayPlanning)

            Globals.OBJ_PLANNING_SECOND_DAY -> planning.value =
                planning.value.copy(secondDay = dayPlanning)

            Globals.OBJ_PLANNING_THIRD_DAY -> planning.value =
                planning.value.copy(thirdDay = dayPlanning)

            Globals.OBJ_PLANNING_FOURTH_DAY -> planning.value =
                planning.value.copy(fourthDay = dayPlanning)

            Globals.OBJ_PLANNING_FIFTH_DAY -> planning.value =
                planning.value.copy(fifthDay = dayPlanning)

            Globals.OBJ_PLANNING_SIXTH_DAY -> planning.value =
                planning.value.copy(sixthDay = dayPlanning)

            Globals.OBJ_PLANNING_SEVENTH_DAY -> planning.value =
                planning.value.copy(seventhDay = dayPlanning)
        }
    }
}