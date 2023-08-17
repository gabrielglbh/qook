package com.gabr.gabc.qook.presentation.recipeDetailsPage

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.presentation.recipesPage.RecipesPage
import com.gabr.gabc.qook.presentation.shared.components.QActionBar
import com.gabr.gabc.qook.presentation.shared.components.QRecipeDetail
import com.gabr.gabc.qook.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeDetailsPage : ComponentActivity() {
    companion object {
        const val HAS_UPDATED_RECIPE = "HAS_UPDATED_RECIPE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                RecipeDetailsView()
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun RecipeDetailsView() {
        var recipe by remember { mutableStateOf(Recipe.EMPTY_RECIPE) }

        LaunchedEffect(key1 = Unit, block = {
            val recipeFromList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(RecipesPage.RECIPE_FROM_LIST, Recipe::class.java)
            } else {
                intent.getParcelableExtra(RecipesPage.RECIPE_FROM_LIST)
            }

            recipe = recipeFromList!!
        })

        Box {
            Scaffold(
                topBar = {
                    QActionBar(title = R.string.recipe_details, onBack = {
                        val resultIntent = Intent()
                        // TODO: must change the boolean value
                        resultIntent.putExtra(HAS_UPDATED_RECIPE, false)
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    })
                }
            ) {
                Box(
                    modifier = Modifier
                        .padding(it)
                        .consumeWindowInsets(it)
                ) {
                    QRecipeDetail(recipe = recipe, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}