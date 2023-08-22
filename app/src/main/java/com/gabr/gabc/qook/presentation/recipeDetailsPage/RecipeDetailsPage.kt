package com.gabr.gabc.qook.presentation.recipeDetailsPage

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ModeEdit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.presentation.addRecipePage.AddRecipePage
import com.gabr.gabc.qook.presentation.recipeDetailsPage.viewModel.RecipeDetailsViewModel
import com.gabr.gabc.qook.presentation.recipesPage.RecipesPage
import com.gabr.gabc.qook.presentation.shared.components.QActionBar
import com.gabr.gabc.qook.presentation.shared.components.QDialog
import com.gabr.gabc.qook.presentation.shared.components.QLoadingScreen
import com.gabr.gabc.qook.presentation.shared.components.QRecipeDetail
import com.gabr.gabc.qook.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipeDetailsPage : ComponentActivity() {
    companion object {
        const val HAS_UPDATED_RECIPE = "HAS_UPDATED_RECIPE"
        const val HAS_DELETED_RECIPE = "HAS_DELETED_RECIPE"
        const val RECIPE_FROM_DETAILS = "RECIPE_FROM_DETAILS"
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val extras = result.data?.extras

                val viewModel: RecipeDetailsViewModel by viewModels()
                val updatedRecipe = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras?.getParcelable(AddRecipePage.RECIPE_UPDATED, Recipe::class.java)
                } else {
                    extras?.getParcelable(AddRecipePage.RECIPE_UPDATED)
                }

                updatedRecipe?.let {
                    viewModel.updateRecipe(it)
                    viewModel.isUpdating(true)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: RecipeDetailsViewModel by viewModels()

        val recipeFromList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(RecipesPage.RECIPE_FROM_LIST, Recipe::class.java)
        } else {
            intent.getParcelableExtra(RecipesPage.RECIPE_FROM_LIST)
        }

        viewModel.updateRecipe(recipeFromList!!)

        setContent {
            AppTheme {
                RecipeDetailsView()
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun RecipeDetailsView() {
        val viewModel: RecipeDetailsViewModel by viewModels()

        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        val showConfirmationDialog = remember { mutableStateOf(false) }

        if (showConfirmationDialog.value)
            QDialog(
                onDismissRequest = { showConfirmationDialog.value = false },
                leadingIcon = Icons.Outlined.Delete,
                title = R.string.recipe_details_remove_recipe,
                content = {
                    Text(
                        stringResource(
                            R.string.recipe_details_delete_warning,
                            viewModel.recipe.value.name
                        )
                    )
                },
                onSubmit = {
                    showConfirmationDialog.value = false
                    viewModel.removeRecipe(
                        onError = {
                            scope.launch {
                                snackbarHostState.showSnackbar(it)
                            }
                        },
                        onSuccess = {
                            val intent = Intent()
                            intent.putExtra(
                                HAS_DELETED_RECIPE,
                                viewModel.recipe.value
                            )
                            setResult(RESULT_OK, intent)
                            finish()
                        }
                    )
                },
            )

        Box {
            Scaffold(
                topBar = {
                    QActionBar(
                        title = R.string.recipe_details,
                        onBack = {
                            if (viewModel.isUpdate.value) {
                                val resultIntent = Intent()
                                resultIntent.putExtra(HAS_UPDATED_RECIPE, viewModel.recipe.value)
                                setResult(RESULT_OK, resultIntent)
                            }
                            finish()
                        },
                        actions = listOf(
                            {
                                IconButton(
                                    modifier = Modifier.padding(end = 8.dp),
                                    onClick = {
                                        showConfirmationDialog.value = true
                                    }
                                ) {
                                    Icon(
                                        Icons.Outlined.Delete,
                                        "",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            {
                                IconButton(
                                    onClick = {
                                        val intent = Intent(
                                            this@RecipeDetailsPage,
                                            AddRecipePage::class.java
                                        )
                                        intent.putExtra(RECIPE_FROM_DETAILS, viewModel.recipe.value)
                                        resultLauncher.launch(intent)
                                    }
                                ) {
                                    Icon(Icons.Outlined.ModeEdit, "")
                                }
                            }
                        )
                    )
                },
                snackbarHost = {
                    SnackbarHost(snackbarHostState)
                }
            ) {
                Box(
                    modifier = Modifier
                        .padding(it)
                        .consumeWindowInsets(it)
                ) {
                    Column {
                        QRecipeDetail(
                            recipe = viewModel.recipe.value,
                            modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
                        )

                    }
                }
            }
            if (viewModel.isLoading.value) QLoadingScreen()
        }
    }
}