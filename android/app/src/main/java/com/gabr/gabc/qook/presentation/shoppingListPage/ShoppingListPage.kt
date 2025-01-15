package com.gabr.gabc.qook.presentation.shoppingListPage

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.os.BundleCompat
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.planning.DayPlanning
import com.gabr.gabc.qook.presentation.shared.IntentVars.Companion.PLANNING
import com.gabr.gabc.qook.presentation.shared.IntentVars.Companion.SHARED_PLANNING_ID
import com.gabr.gabc.qook.presentation.shared.Validators
import com.gabr.gabc.qook.presentation.shared.components.QActionBar
import com.gabr.gabc.qook.presentation.shared.components.QDialog
import com.gabr.gabc.qook.presentation.shared.components.QIngredient
import com.gabr.gabc.qook.presentation.shared.components.QLoadingScreen
import com.gabr.gabc.qook.presentation.shared.components.QShimmer
import com.gabr.gabc.qook.presentation.shared.components.QTextForm
import com.gabr.gabc.qook.presentation.shoppingListPage.viewModel.ShoppingListViewModel
import com.gabr.gabc.qook.presentation.theme.AppTheme
import com.gabr.gabc.qook.presentation.theme.seed
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
        var showReloadDialog by remember { mutableStateOf(false) }

        LaunchedEffect(key1 = Unit, block = {
            val planning = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.extras?.let { bundle ->
                    BundleCompat.getParcelableArray(bundle, PLANNING, DayPlanning::class.java)
                }
            } else {
                intent.getParcelableArrayExtra(PLANNING)
            }
            val groupId = intent.getStringExtra(SHARED_PLANNING_ID)

            viewModel.loadShoppingList(planning?.map { it as DayPlanning }, groupId)
        })

        if (showReloadDialog)
            QDialog(
                onDismissRequest = { showReloadDialog = false },
                leadingIcon = Icons.Outlined.RestartAlt,
                title = R.string.ingredients_reload_dialog_title,
                buttonTitle = R.string.ingredients_dialog_reload_button,
                content = {
                    Text(stringResource(R.string.ingredients_reload_dialog_description))
                },
                onSubmit = {
                    viewModel.reloadIngredientsFromPlanning()
                    showReloadDialog = false
                },
            )

        Box {
            Scaffold(
                topBar = {
                    QActionBar(
                        onBack = {
                            finish()
                        },
                        title = R.string.home_shopping_bnb,
                        actions = listOf {
                            IconButton(onClick = {
                                showReloadDialog = true
                            }) {
                                Icon(Icons.Outlined.RestartAlt, "")
                            }
                        }
                    )
                }
            ) {
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .consumeWindowInsets(it)
                ) {
                    Body(viewModel)
                }
            }
            if (viewModel.isLoading.value) QLoadingScreen()
        }
    }

    @Composable
    fun Body(viewModel: ShoppingListViewModel) {
        val focusManager = LocalFocusManager.current
        var ingredientNameField by remember { mutableStateOf("") }
        var ingredientNameError by remember { mutableStateOf(false) }

        fun updateIngredient(ingredient: Pair<String, Boolean>) {
            focusManager.clearFocus()
            viewModel.updateIngredient(ingredient)
        }

        Column(
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(
                stringResource(R.string.ingredients_description),
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.outline
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(8.dp))
            QTextForm(
                labelId = R.string.add_recipe_ingredient_name,
                value = ingredientNameField,
                onValueChange = {
                    ingredientNameField = it
                    ingredientNameError = Validators.isIngredientNameInvalid(it)
                },
                leadingIcon = Icons.Outlined.ShoppingBasket,
                isError = ingredientNameError,
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
                        }) {
                            Icon(Icons.Outlined.Clear, contentDescription = null)
                        }
                    }
                },
                onSubmitWithImeAction = {
                    if (ingredientNameField.trim()
                            .isEmpty() || ingredientNameError
                    ) return@QTextForm

                    viewModel.addIngredientToList(ingredientNameField)

                    ingredientNameField = ""
                    ingredientNameError = false
                }
            )
            QShimmer(controller = viewModel.ingredients.isNotEmpty()) {
                LazyColumn(
                    modifier = it
                        .weight(1f)
                        .padding(top = 8.dp)
                ) {
                    items(
                        viewModel.ingredients.toSortedMap().toList(),
                        key = { index ->
                            index
                        }
                    ) { ingredient ->
                        Surface(
                            onClick = {
                                updateIngredient(ingredient)
                            },
                            shape = MaterialTheme.shapes.small
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(4.dp)
                            ) {
                                QIngredient(
                                    ingredient.first,
                                    strikeThrough = ingredient.second,
                                    leadingIcon = {
                                        Surface(
                                            modifier = Modifier.size(24.dp),
                                            shape = CircleShape,
                                            border = if (ingredient.second) {
                                                BorderStroke(2.dp, seed)
                                            } else {
                                                BorderStroke(
                                                    2.dp,
                                                    MaterialTheme.colorScheme.outline
                                                )
                                            }
                                        ) {
                                            if (ingredient.second) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    contentDescription = "",
                                                    tint = seed,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        updateIngredient(ingredient)
                                    },
                                    onClear = {
                                        viewModel.removeIngredientFromList(ingredient)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}