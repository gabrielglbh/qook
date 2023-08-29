package com.gabr.gabc.qook.presentation.shoppingListPage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import com.gabr.gabc.qook.presentation.shoppingListPage.viewModel.ShoppingListViewModel
import com.gabr.gabc.qook.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShoppingListPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                ShoppingListView()
            }
        }
    }

    @Composable
    fun ShoppingListView() {
        val viewModel: ShoppingListViewModel by viewModels()

    }
}