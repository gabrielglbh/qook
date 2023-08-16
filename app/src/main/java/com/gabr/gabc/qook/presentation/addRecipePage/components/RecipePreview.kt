package com.gabr.gabc.qook.presentation.addRecipePage.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.presentation.addRecipePage.viewModel.AddRecipeViewModel
import com.gabr.gabc.qook.presentation.shared.components.QRecipeDetail
import com.gabr.gabc.qook.presentation.shared.components.QTextTitle

@Composable
fun RecipePreview(modifier: Modifier, viewModel: AddRecipeViewModel) {
    val state = viewModel.recipeState.collectAsState().value

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        QTextTitle(
            title = R.string.add_recipe_preview_of_the_recipe,
            subtitle = R.string.add_recipe_preview_guideline
        )
        Spacer(modifier = Modifier.size(8.dp))
        QRecipeDetail(recipe = state.recipe, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.size(8.dp))
        Button(
            onClick = {
                // TODO: Upload recipe
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Text(stringResource(R.string.add_recipe_save_recipe))
        }
    }
}