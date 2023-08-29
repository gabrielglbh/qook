package com.gabr.gabc.qook.presentation.shoppingListPage.viewModel

import androidx.lifecycle.ViewModel
import com.gabr.gabc.qook.domain.ingredients.IngredientsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    private val ingredientsRepository: IngredientsRepository,
) : ViewModel() {

}