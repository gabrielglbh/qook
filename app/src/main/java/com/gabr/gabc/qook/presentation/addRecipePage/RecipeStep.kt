package com.gabr.gabc.qook.presentation.addRecipePage

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Preview
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.ui.graphics.vector.ImageVector

enum class RecipeStep(val icon: ImageVector) {
    DATA(Icons.AutoMirrored.Outlined.ReceiptLong),
    INGREDIENTS(Icons.Outlined.ShoppingBasket),
    DESCRIPTION(Icons.AutoMirrored.Outlined.ListAlt),
    TAGS(Icons.Outlined.BookmarkBorder),
    PREVIEW(Icons.Outlined.Preview)
}