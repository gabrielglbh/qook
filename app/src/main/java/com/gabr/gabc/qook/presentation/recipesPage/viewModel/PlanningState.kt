package com.gabr.gabc.qook.presentation.recipesPage.viewModel

import com.gabr.gabc.qook.domain.planning.DayPlanning

data class PlanningState(
    val dayPlanning: DayPlanning = DayPlanning.EMPTY,
    val isLunch: Boolean? = null,
    val groupId: String? = null,
)
