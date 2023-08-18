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
import androidx.compose.material.icons.outlined.ModeEdit
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import arrow.core.Either
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.presentation.addRecipePage.AddRecipePage
import com.gabr.gabc.qook.presentation.recipeDetailsPage.viewModel.RecipeDetailsViewModel
import com.gabr.gabc.qook.presentation.recipesPage.RecipesPage
import com.gabr.gabc.qook.presentation.shared.components.QActionBar
import com.gabr.gabc.qook.presentation.shared.components.QRecipeDetail
import com.gabr.gabc.qook.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeDetailsPage : ComponentActivity() {
    companion object {
        const val HAS_UPDATED_RECIPE = "HAS_UPDATED_RECIPE"
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
                        actionBehaviour = {
                            val intent = Intent(this@RecipeDetailsPage, AddRecipePage::class.java)
                            intent.putExtra(RECIPE_FROM_DETAILS, viewModel.recipe.value)
                            resultLauncher.launch(intent)
                        },
                        action = Either.Right(Icons.Outlined.ModeEdit)
                    )
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
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }
        }
    }
}