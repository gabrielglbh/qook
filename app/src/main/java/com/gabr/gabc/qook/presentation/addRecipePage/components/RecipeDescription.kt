package com.gabr.gabc.qook.presentation.addRecipePage.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.presentation.addRecipePage.viewModel.AddRecipeViewModel
import com.gabr.gabc.qook.presentation.shared.Validators
import com.gabr.gabc.qook.presentation.shared.components.QTextForm

@Composable
fun RecipeDescription(
    modifier: Modifier, onNavigate: () -> Unit, viewModel: AddRecipeViewModel
) {
    val state = viewModel.recipeState.collectAsState().value
    val configuration = LocalConfiguration.current

    var descriptionError by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            stringResource(R.string.add_recipe_description_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(12.dp))
        QTextForm(
            labelId = R.string.add_recipe_description,
            modifier = Modifier
                .height(configuration.screenHeightDp.dp / 1.5f)
                .weight(1f),
            value = state.recipe.description,
            singleLine = false,
            imeAction = ImeAction.Default,
            onValueChange = {
                viewModel.updateMetadata(description = it)
                descriptionError = Validators.isDescriptionInvalid(it)
            },
            onSubmitWithImeAction = {
                viewModel.updateMetadata(description = state.recipe.description)
            },
            isError = descriptionError
        )
        Button(
            onClick = {
                if (state.recipe.description.trim().isNotEmpty() && !descriptionError) {
                    onNavigate()
                } else {
                    descriptionError = Validators.isDescriptionInvalid(state.recipe.description)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Text(stringResource(R.string.add_recipe_next))
        }
    }
}