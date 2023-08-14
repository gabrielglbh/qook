package com.gabr.gabc.qook.presentation.addRecipePage.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R

@Composable
fun RecipeDescription(onNavigate: () -> Unit) {
    Button(
        onClick = onNavigate,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp, start = 32.dp, end = 32.dp)
    ) {
        Text(stringResource(R.string.add_recipe_ready))
    }
}