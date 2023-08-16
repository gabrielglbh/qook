package com.gabr.gabc.qook.presentation.addRecipePage.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiFoodBeverage
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.presentation.addRecipePage.viewModel.AddRecipeViewModel
import com.gabr.gabc.qook.presentation.shared.Validators
import com.gabr.gabc.qook.presentation.shared.components.QDialog
import com.gabr.gabc.qook.presentation.shared.components.QRecipeDetail
import com.gabr.gabc.qook.presentation.shared.components.QTextTitle

@Composable
fun RecipePreview(modifier: Modifier, viewModel: AddRecipeViewModel, onError: () -> Unit) {
    val state = viewModel.recipeState.collectAsState().value
    val showValidationDialog = remember { mutableStateOf(false) }

    if (showValidationDialog.value)
        QDialog(
            onDismissRequest = { showValidationDialog.value = false },
            leadingIcon = Icons.Outlined.EmojiFoodBeverage,
            title = R.string.add_recipe_ready_dialog_title,
            content = {
                Text(stringResource(R.string.add_recipe_ready_dialog_subtitle, state.recipe.name))
            },
            onSubmit = {
                viewModel.uploadRecipe()
                showValidationDialog.value = false
            },
        )

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        QTextTitle(
            title = R.string.add_recipe_preview_of_the_recipe,
            subtitle = R.string.add_recipe_preview_guideline
        )
        Spacer(modifier = Modifier.size(12.dp))
        QRecipeDetail(recipe = state.recipe, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.size(12.dp))
        Button(
            onClick = {
                if (Validators.isRecipeInvalid(state.recipe)) {
                    onError()
                    return@Button
                }
                showValidationDialog.value = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.add_recipe_save_recipe))
        }
    }
}