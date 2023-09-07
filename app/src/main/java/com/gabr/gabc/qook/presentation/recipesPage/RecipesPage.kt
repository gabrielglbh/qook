package com.gabr.gabc.qook.presentation.recipesPage

import android.app.Activity
import android.content.Intent
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.PostAdd
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.planning.DayPlanning
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.domain.tag.Tag
import com.gabr.gabc.qook.presentation.addRecipePage.AddRecipePage
import com.gabr.gabc.qook.presentation.planningPage.PlanningPage
import com.gabr.gabc.qook.presentation.recipeDetailsPage.RecipeDetailsPage
import com.gabr.gabc.qook.presentation.recipesPage.viewModel.RecipesViewModel
import com.gabr.gabc.qook.presentation.shared.QDateUtils
import com.gabr.gabc.qook.presentation.shared.components.QActionBar
import com.gabr.gabc.qook.presentation.shared.components.QDialog
import com.gabr.gabc.qook.presentation.shared.components.QEmptyBox
import com.gabr.gabc.qook.presentation.shared.components.QLoadingScreen
import com.gabr.gabc.qook.presentation.shared.components.QRecipeItem
import com.gabr.gabc.qook.presentation.shared.components.QShimmer
import com.gabr.gabc.qook.presentation.shared.components.QTag
import com.gabr.gabc.qook.presentation.shared.components.QTextForm
import com.gabr.gabc.qook.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesPage : ComponentActivity() {
    companion object {
        const val HAS_UPDATED_PLANNING = "HAS_UPDATED_PLANNING"
        const val HAS_UPDATED_PLANNING_RECIPE = "HAS_UPDATED_PLANNING_RECIPE"
        const val RECIPES_LIST = "RECIPES_LIST"
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val extras = result.data?.extras

                val viewModel: RecipesViewModel by viewModels()
                val updatedRecipe = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras?.getParcelable(RecipeDetailsPage.HAS_UPDATED_RECIPE, Recipe::class.java)
                } else {
                    extras?.getParcelable(RecipeDetailsPage.HAS_UPDATED_RECIPE)
                }

                // TODO: Maintain the scroll? --- possible with lazy loading?
                if (updatedRecipe != null) {
                    updatedRecipe.let {
                        viewModel.updateRecipeLocally(it)
                    }
                } else {
                    val toBeAddedRecipe =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            extras?.getParcelable(AddRecipePage.RECIPE_UPDATED, Recipe::class.java)
                        } else {
                            extras?.getParcelable(AddRecipePage.RECIPE_UPDATED)
                        }

                    toBeAddedRecipe?.let { recipe ->
                        viewModel.addRecipeLocally(recipe)
                    }
                }

                val deletedRecipe = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras?.getParcelable(RecipeDetailsPage.HAS_DELETED_RECIPE, Recipe::class.java)
                } else {
                    extras?.getParcelable(RecipeDetailsPage.HAS_DELETED_RECIPE)
                }

                deletedRecipe?.let {
                    viewModel.deleteRecipeLocally(it)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: RecipesViewModel by viewModels()

        val dayPlanning = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(PlanningPage.FROM_PLANNING, DayPlanning::class.java)
        } else {
            intent.getParcelableExtra(PlanningPage.FROM_PLANNING)
        }
        val isLunchFromPlanning = intent.getBooleanExtra(PlanningPage.IS_LUNCH, false)
        val id = intent.getStringExtra(PlanningPage.SHARED_PLANNING_ID)
        dayPlanning?.let {
            viewModel.updatePlanning(
                viewModel.planningState.value.copy(
                    dayPlanning = it,
                    isLunch = isLunchFromPlanning,
                    groupId = id,
                )
            )
        }

        setContent {
            AppTheme {
                RecipesView()
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun RecipesView() {
        val viewModel: RecipesViewModel by viewModels()
        val state = viewModel.recipesState.collectAsState().value

        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        val searchState = viewModel.searchState.collectAsState().value
        val planningState = viewModel.planningState.collectAsState().value

        val focusManager = LocalFocusManager.current
        var selectedFilterTag by remember { mutableStateOf<Tag?>(null) }
        var selectedRecipeForPlanning by remember { mutableStateOf(Recipe.EMPTY_RECIPE) }

        fun clearSearch() {
            viewModel.updateSearchState(searchState.copy(query = ""))
            viewModel.clearSearch()
            focusManager.clearFocus()
        }

        LaunchedEffect(key1 = Unit, block = {
            val recipes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableArrayExtra(RECIPES_LIST, Recipe::class.java)
            } else {
                intent.getParcelableArrayExtra(RECIPES_LIST)
            }
            viewModel.loadRecipesLocallyIfAny(
                recipes?.map { r -> r as Recipe },
                errorRecipes = { err ->
                    scope.launch {
                        snackbarHostState.showSnackbar(err)
                    }
                },
                errorTags = { err ->
                    scope.launch {
                        snackbarHostState.showSnackbar(err)
                    }
                }
            )
        })

        if (selectedRecipeForPlanning != Recipe.EMPTY_RECIPE)
            QDialog(
                onDismissRequest = { selectedRecipeForPlanning = Recipe.EMPTY_RECIPE },
                leadingIcon = Icons.Outlined.CalendarMonth,
                title = R.string.add_recipe_to_planning,
                buttonTitle = R.string.planning_add_to_planning_button,
                content = {
                    Text(
                        stringResource(
                            R.string.planning_confirmation_msg,
                            selectedRecipeForPlanning.name,
                            stringResource(QDateUtils.getWeekDayStringRes(planningState.dayPlanning.dayIndex))
                        )
                    )
                },
                onSubmit = {
                    viewModel.updatePlanningWith(
                        selectedRecipeForPlanning,
                        onError = { e ->
                            scope.launch {
                                snackbarHostState.showSnackbar(e)
                            }
                        },
                        onSuccess = { recipe, dayPlanning ->
                            val intent = Intent()
                            intent.putExtra(HAS_UPDATED_PLANNING, dayPlanning)
                            intent.putExtra(PlanningPage.IS_LUNCH, planningState.isLunch!!)
                            intent.putExtra(HAS_UPDATED_PLANNING_RECIPE, recipe)
                            setResult(RESULT_OK, intent)
                            finish()
                        },
                    )
                    selectedRecipeForPlanning = Recipe.EMPTY_RECIPE
                },
            )

        Box {
            Scaffold(
                topBar = {
                    QActionBar(
                        title = R.string.recipes_title,
                        onBack = { finish() },
                        actions = if (planningState.dayPlanning == DayPlanning.EMPTY_DAY_PLANNING) {
                            listOf {
                                IconButton(
                                    onClick = {
                                        val intent =
                                            Intent(this@RecipesPage, AddRecipePage::class.java)
                                        resultLauncher.launch(intent)
                                    }
                                ) {
                                    Icon(Icons.Outlined.PostAdd, "")
                                }
                            }
                        } else {
                            null
                        }
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
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp)
                    ) {
                        QTextForm(
                            labelId = R.string.recipes_search_recipes,
                            value = searchState.query,
                            onValueChange = {
                                viewModel.updateSearchState(searchState.copy(query = it))
                            },
                            leadingIcon = Icons.Outlined.Search,
                            trailingIcon = if (searchState.query.isEmpty()) {
                                null
                            } else {
                                {
                                    IconButton(onClick = { clearSearch() }) {
                                        Icon(Icons.Outlined.Clear, contentDescription = null)
                                    }
                                }
                            },
                            imeAction = ImeAction.Search,
                            onSubmitWithImeAction = {
                                selectedFilterTag = null
                                viewModel.onSearch()
                                focusManager.clearFocus()
                            }
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        QShimmer(controller = state.tags.isNotEmpty()) { modifier ->
                            LazyRow(
                                modifier = modifier.fillMaxWidth(),
                                content = {
                                    items(state.tags) { tag ->
                                        QTag(
                                            tag = tag,
                                            modifier = Modifier.padding(4.dp),
                                            isActive = selectedFilterTag == tag,
                                            onClick = {
                                                selectedFilterTag = if (selectedFilterTag == tag) {
                                                    null
                                                } else {
                                                    tag
                                                }
                                                viewModel.updateSearchState(
                                                    searchState.copy(
                                                        tag = selectedFilterTag,
                                                        query = ""
                                                    )
                                                )
                                                viewModel.onSearch()
                                            }
                                        )
                                    }
                                }
                            )
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = if (state.recipes.isEmpty() || state.searchedRecipes.isEmpty()) {
                                Alignment.Center
                            } else {
                                Alignment.TopCenter
                            }
                        ) {
                            QShimmer(controller = state.recipes.isEmpty() || state.searchedRecipes.isEmpty()) { modifier ->
                                QEmptyBox(
                                    message = R.string.recipes_empty,
                                    icon = Icons.Outlined.Receipt,
                                    modifier = modifier
                                )
                            }
                            QShimmer(controller = state.searchedRecipes.isNotEmpty()) { modifier ->
                                LazyColumn(
                                    modifier = modifier
                                ) {
                                    itemsIndexed(state.searchedRecipes) { x, recipe ->
                                        Column {
                                            QRecipeItem(
                                                recipe = recipe,
                                                modifier = Modifier.padding(8.dp),
                                                onClick = {
                                                    if (planningState.dayPlanning != DayPlanning.EMPTY_DAY_PLANNING || planningState.isLunch != null) {
                                                        selectedRecipeForPlanning = recipe
                                                    } else {
                                                        val intent =
                                                            Intent(
                                                                this@RecipesPage,
                                                                RecipeDetailsPage::class.java
                                                            )
                                                        intent.putExtra(
                                                            RecipeDetailsPage.RECIPE,
                                                            recipe
                                                        )
                                                        resultLauncher.launch(intent)
                                                    }
                                                }
                                            )
                                            if (x < state.searchedRecipes.size - 1) Divider(color = MaterialTheme.colorScheme.outlineVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (viewModel.isLoadingRecipes.value) QLoadingScreen()
        }
    }
}