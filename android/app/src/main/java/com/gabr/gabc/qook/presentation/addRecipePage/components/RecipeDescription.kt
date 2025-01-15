package com.gabr.gabc.qook.presentation.addRecipePage.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.presentation.addRecipePage.viewModel.AddRecipeViewModel
import com.gabr.gabc.qook.presentation.shared.Validators
import com.gabr.gabc.qook.presentation.shared.components.QDescriptionStep
import com.gabr.gabc.qook.presentation.shared.components.QEmptyBox
import com.gabr.gabc.qook.presentation.shared.components.QTextForm
import com.gabr.gabc.qook.presentation.shared.components.QTextTitle

@Composable
fun RecipeDescription(
    modifier: Modifier, onNavigate: () -> Unit, viewModel: AddRecipeViewModel
) {
    val state = viewModel.recipeState.collectAsState().value
    val focusManager = LocalFocusManager.current

    var descriptionStep by remember { mutableStateOf("") }
    var stepError by remember { mutableStateOf(false) }
    var stepIndexIfUpdating by remember { mutableIntStateOf(-1) }

    var recipeUrl by remember { mutableStateOf(state.recipe.recipeUrl ?: "") }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        QTextTitle(
            title = R.string.add_recipe_description_title,
            subtitle = R.string.add_recipe_description_description
        )
        Spacer(modifier = Modifier.size(8.dp))
        QTextForm(
            labelId = R.string.add_recipe_description_step,
            value = descriptionStep,
            singleLine = false,
            imeAction = if (descriptionStep.isEmpty()) {
                ImeAction.Done
            } else {
                ImeAction.Send
            },
            trailingIcon = if (descriptionStep.isEmpty()) {
                null
            } else {
                {
                    IconButton(onClick = {
                        descriptionStep = ""
                        focusManager.clearFocus()
                        stepIndexIfUpdating = -1
                    }) {
                        Icon(Icons.Outlined.Clear, contentDescription = null)
                    }
                }
            },
            onValueChange = { value ->
                descriptionStep = value
                stepError = Validators.isDescriptionInvalid(value)
            },
            onSubmitWithImeAction = {
                if (descriptionStep.trim().isEmpty() || stepError) return@QTextForm

                if (stepIndexIfUpdating != -1) {
                    if (descriptionStep.trim().isNotEmpty()) {
                        viewModel.updateStepFromDescription(
                            stepIndexIfUpdating,
                            descriptionStep
                        )
                    }
                } else {
                    viewModel.addStepToDescription(descriptionStep)
                }
                descriptionStep = ""
                stepIndexIfUpdating = -1
                stepError = false
            },
            isError = stepError
        )
        Spacer(modifier = Modifier.size(8.dp))
        if (state.recipe.description.isNotEmpty()) LazyColumn(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(state.recipe.description) { index, step ->
                QDescriptionStep(
                    step = step,
                    stepIndex = index,
                    onClick = {
                        stepIndexIfUpdating = index
                        descriptionStep = step
                    }, onClear = {
                        viewModel.deleteStepFromDescription(step)
                    }
                )
            }
        } else {
            QEmptyBox(
                message = R.string.add_recipe_empty_description,
                icon = Icons.AutoMirrored.Outlined.ListAlt,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        QTextForm(
            labelId = R.string.add_recipe_recipe_link,
            value = recipeUrl,
            trailingIcon = if (recipeUrl.isEmpty()) {
                null
            } else {
                {
                    IconButton(onClick = {
                        recipeUrl = ""
                        viewModel.updateMetadata(recipeUrl = recipeUrl)
                        focusManager.clearFocus()
                    }) {
                        Icon(Icons.Outlined.Clear, contentDescription = null)
                    }
                }
            },
            onValueChange = { value ->
                recipeUrl = value
            },
            onSubmitWithImeAction = {
                viewModel.updateMetadata(recipeUrl = recipeUrl)
            },
        )
        Button(
            onClick = {
                onNavigate()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Text(stringResource(R.string.add_recipe_next))
        }
    }
}