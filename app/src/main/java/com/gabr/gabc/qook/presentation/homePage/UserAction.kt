package com.gabr.gabc.qook.presentation.homePage

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.RamenDining
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import com.gabr.gabc.qook.R

enum class UserAction(val title: Int, val icon: ImageVector) {
    RECIPES(R.string.home_recipes_bnb, Icons.Outlined.Receipt),
    RANDOM(R.string.home_get_random_recipe, Icons.Outlined.RamenDining),
    PLANNING(R.string.home_planning_bnb, Icons.Outlined.CalendarMonth),
    SHOPPING(R.string.home_shopping_bnb, Icons.Outlined.ShoppingCart)
}