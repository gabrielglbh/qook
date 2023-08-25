package com.gabr.gabc.qook.presentation.recipesPage.viewModel

import com.gabr.gabc.qook.domain.planning.DayPlanning

data class PlanningState(
    val dayPlanning: DayPlanning = DayPlanning.EMPTY_DAY_PLANNING,
    val isLunch: Boolean? = null
)