package com.gabr.gabc.qook.presentation.addSharedPlanningPage.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabr.gabc.qook.domain.sharedPlanning.SharedPlanning
import com.gabr.gabc.qook.domain.sharedPlanning.SharedPlanningRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddSharedPlanningViewModel @Inject constructor(
    private val sharedPlanningRepository: SharedPlanningRepository,
) : ViewModel() {
    var sharedPlanning = mutableStateOf(SharedPlanning.EMPTY_SHARED_PLANNING)
        private set
    var isLoading = mutableStateOf(false)
        private set

    fun updateSharedPlanning(
        name: String? = null,
        resetDay: Int? = null,
    ) {
        val value = sharedPlanning.value
        sharedPlanning.value = value.copy(
            name = name ?: value.name,
            resetDay = resetDay ?: value.resetDay
        )
    }

    fun createSharedPlanning(onError: (String) -> Unit, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            val res = sharedPlanningRepository.createSharedPlanning(sharedPlanning.value)
            res.fold(
                ifLeft = { e -> onError(e.error) },
                ifRight = { sp -> onSuccess(sp.id) },
            )
            isLoading.value = false
        }
    }
}