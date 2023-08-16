package com.gabr.gabc.qook.presentation.addRecipePage.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.RemoveShoppingCart
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.presentation.addRecipePage.viewModel.AddRecipeViewModel
import com.gabr.gabc.qook.presentation.shared.components.QEmptyBox
import com.gabr.gabc.qook.presentation.shared.components.QTextForm

@Composable
fun RecipeIngredients(
    modifier: Modifier, onNavigate: () -> Unit, viewModel: AddRecipeViewModel
) {
    val focusManager = LocalFocusManager.current
    val state = viewModel.recipeState.collectAsState().value

    var ingredientNameField by remember { mutableStateOf("") }
    var ingredientIndexIfUpdating by remember { mutableIntStateOf(-1) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            stringResource(R.string.add_recipe_ingredients_title),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(8.dp))
        QTextForm(
            labelId = R.string.add_recipe_ingredient_name,
            value = ingredientNameField,
            onValueChange = {
                ingredientNameField = it
            },
            leadingIcon = Icons.Outlined.ShoppingBasket,
            imeAction = if (ingredientNameField.isEmpty()) {
                ImeAction.Done
            } else {
                ImeAction.Send
            },
            trailingIcon = if (ingredientNameField.isEmpty()) {
                null
            } else {
                {
                    IconButton(onClick = {
                        ingredientNameField = ""
                        focusManager.clearFocus()
                        ingredientIndexIfUpdating = -1
                    }) {
                        Icon(Icons.Outlined.Clear, contentDescription = null)
                    }
                }
            },
            onSubmitWithImeAction = {
                if (ingredientNameField.trim().isEmpty()) return@QTextForm

                if (ingredientIndexIfUpdating != -1) {
                    if (ingredientNameField.trim().isNotEmpty()) {
                        viewModel.updateIngredientFromRecipe(
                            ingredientIndexIfUpdating,
                            ingredientNameField
                        )
                    }
                } else {
                    viewModel.addIngredientToRecipe(ingredientNameField)
                }
                ingredientNameField = ""
                ingredientIndexIfUpdating = -1
            }
        )
        if (state.recipe.ingredients.isEmpty()) {
            QEmptyBox(
                message = R.string.add_recipe_empty_ingredients,
                icon = Icons.Outlined.RemoveShoppingCart,
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp)
            ) {
                items(state.recipe.ingredients.size) { x ->
                    val ingredient = state.recipe.ingredients[x]

                    Surface(
                        onClick = {
                            ingredientIndexIfUpdating = x
                            ingredientNameField = ingredient
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(ingredient)
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(
                                modifier = Modifier.size(24.dp),
                                onClick = {
                                    viewModel.deleteIngredientFromRecipe(ingredient)
                                    focusManager.clearFocus()
                                }
                            ) {
                                Icon(Icons.Outlined.Clear, contentDescription = null)
                            }
                        }
                    }
                }
            }
        }
        Button(
            onClick = {
                if (state.recipe.ingredients.isEmpty()) return@Button
                onNavigate()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text(stringResource(R.string.add_recipe_next))
        }
    }
}