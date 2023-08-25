package com.gabr.gabc.qook.presentation.planningPage

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.NightlightRound
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.planning.DayPlanning
import com.gabr.gabc.qook.domain.planning.Planning
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.presentation.planningPage.viewModel.PlanningViewModel
import com.gabr.gabc.qook.presentation.recipeDetailsPage.RecipeDetailsPage
import com.gabr.gabc.qook.presentation.recipesPage.RecipesPage
import com.gabr.gabc.qook.presentation.shared.QDateUtils
import com.gabr.gabc.qook.presentation.shared.components.QActionBar
import com.gabr.gabc.qook.presentation.shared.components.QImageContainer
import com.gabr.gabc.qook.presentation.shared.components.QLoadingScreen
import com.gabr.gabc.qook.presentation.shared.components.QRecipeItem
import com.gabr.gabc.qook.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlanningPage : ComponentActivity() {
    companion object {
        const val FROM_PLANNING = "FROM_PLANNING"
        const val IS_LUNCH = "IS_LUNCH"
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val extras = result.data?.extras

                val viewModel: PlanningViewModel by viewModels()
                val updatedPlanning = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras?.getParcelable(RecipesPage.HAS_UPDATED_PLANNING, DayPlanning::class.java)
                } else {
                    extras?.getParcelable(RecipesPage.HAS_UPDATED_PLANNING)
                }
                val updatedRecipeForPlanning =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        extras?.getParcelable(
                            RecipesPage.HAS_UPDATED_PLANNING_RECIPE,
                            Recipe::class.java
                        )
                    } else {
                        extras?.getParcelable(RecipesPage.HAS_UPDATED_PLANNING_RECIPE)
                    }
                val isLunch = extras?.getBoolean(IS_LUNCH) ?: false
                updatedPlanning?.let { dayPlanning ->
                    updatedRecipeForPlanning?.let { recipe ->
                        viewModel.updatePlanningLocally(
                            if (isLunch) {
                                dayPlanning.copy(lunch = recipe)
                            } else {
                                dayPlanning.copy(dinner = recipe)
                            }
                        )
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                PlanningView()
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun PlanningView() {
        val viewModel: PlanningViewModel by viewModels()
        val planning = viewModel.planning.value

        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(key1 = Unit, block = {
            viewModel.loadPlanning { error -> scope.launch { snackbarHostState.showSnackbar(error) } }
        })

        Box {
            Scaffold(
                topBar = {
                    QActionBar(
                        title = R.string.home_planning_bnb,
                        onBack = { finish() }
                    )
                },
                snackbarHost = {
                    SnackbarHost(snackbarHostState)
                }
            ) {
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .consumeWindowInsets(it)
                ) {
                    Body(planning)
                }
            }
            if (viewModel.isLoading.value) QLoadingScreen()
        }
    }

    @Composable
    fun Body(planning: Planning) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PlanningDay(dayPlanning = planning.firstDay)
            PlanningDay(dayPlanning = planning.secondDay)
            PlanningDay(dayPlanning = planning.thirdDay)
            PlanningDay(dayPlanning = planning.fourthDay)
            PlanningDay(dayPlanning = planning.fifthDay)
            PlanningDay(dayPlanning = planning.sixthDay)
            PlanningDay(dayPlanning = planning.seventhDay)
        }
    }

    @Composable
    fun PlanningDay(dayPlanning: DayPlanning) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
        ) {
            Text(
                stringResource(QDateUtils.getWeekDayStringRes(dayPlanning.dayIndex)),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.size(12.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                PlanningDayRecipe(dayPlanning, dayPlanning.lunch, true)
                PlanningDayRecipe(dayPlanning, dayPlanning.dinner, false)
            }
        }
    }

    // TODO: Remove lunch or dinner
    @Composable
    fun PlanningDayRecipe(dayPlanning: DayPlanning, recipe: Recipe, isLunch: Boolean) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = if (isLunch) {
                    Icons.Outlined.WbSunny
                } else {
                    Icons.Outlined.NightlightRound
                }, contentDescription = ""
            )
            Spacer(modifier = Modifier.size(12.dp))
            if (recipe == Recipe.EMPTY_RECIPE) {
                QImageContainer(
                    uri = Uri.EMPTY,
                    placeholder = Icons.Outlined.Add,
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f),
                    size = 128f.dp,
                ) {
                    val intent = Intent(this@PlanningPage, RecipesPage::class.java)
                    intent.putExtra(FROM_PLANNING, dayPlanning)
                    intent.putExtra(IS_LUNCH, isLunch)
                    resultLauncher.launch(intent)
                }
            } else {
                QRecipeItem(
                    recipe = recipe,
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f)
                ) {
                    val intent = Intent(this@PlanningPage, RecipeDetailsPage::class.java)
                    intent.putExtra(RecipesPage.RECIPE_FROM_LIST, recipe)
                    startActivity(intent)
                    // TODO: If updated, must reload planning to refresh data
                }
            }
        }
    }
}