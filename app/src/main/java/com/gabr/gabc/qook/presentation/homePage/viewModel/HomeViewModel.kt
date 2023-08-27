package com.gabr.gabc.qook.presentation.homePage.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gabr.gabc.qook.domain.planning.DayPlanning
import com.gabr.gabc.qook.domain.planning.PlanningRepository
import com.gabr.gabc.qook.domain.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val planningRepository: PlanningRepository,
) : ViewModel() {
    private val _userState = MutableStateFlow(UserState())
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    var planning = mutableListOf<DayPlanning>()
        private set

    fun getUser(onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = userRepository.getUser()
            result.fold(
                ifLeft = {
                    _userState.value = _userState.value.copy(error = it.error)
                    onError(it.error)
                },
                ifRight = {
                    _userState.value = _userState.value.copy(user = it)
                }
            )
        }
    }

    fun getPlanning() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = planningRepository.getPlanning()
            result.fold(
                ifLeft = {},
                ifRight = { p -> planning.addAll(p) }
            )
        }
    }
}