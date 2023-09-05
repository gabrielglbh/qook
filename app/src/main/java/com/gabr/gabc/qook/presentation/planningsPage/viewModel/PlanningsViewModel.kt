package com.gabr.gabc.qook.presentation.planningsPage.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabr.gabc.qook.domain.sharedPlanning.SharedPlanning
import com.gabr.gabc.qook.domain.sharedPlanning.SharedPlanningRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanningsViewModel @Inject constructor(
    private val sharedPlanningRepository: SharedPlanningRepository,
) : ViewModel() {
    var groups = mutableStateListOf<SharedPlanning>()
        private set
    var isLoading = mutableStateOf(false)
        private set

    fun loadGroups(onError: (String) -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            val res = sharedPlanningRepository.getSharedPlannings()
            res.fold(
                ifLeft = { e -> onError(e.error) },
                ifRight = { p ->
                    groups.addAll(p)
                }
            )
            isLoading.value = false
        }
    }
}