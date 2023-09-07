package com.gabr.gabc.qook.presentation.planningPage

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gabr.gabc.qook.R
import com.gabr.gabc.qook.domain.planning.DayPlanning
import com.gabr.gabc.qook.domain.planning.MealData
import com.gabr.gabc.qook.domain.recipe.Recipe
import com.gabr.gabc.qook.domain.sharedPlanning.SharedPlanning
import com.gabr.gabc.qook.presentation.homePage.HomePage
import com.gabr.gabc.qook.presentation.planningPage.viewModel.PlanningViewModel
import com.gabr.gabc.qook.presentation.planningSettingsPage.PlanningSettingsPage
import com.gabr.gabc.qook.presentation.recipeDetailsPage.RecipeDetailsPage
import com.gabr.gabc.qook.presentation.recipesPage.RecipesPage
import com.gabr.gabc.qook.presentation.shared.components.QActionBar
import com.gabr.gabc.qook.presentation.shared.components.QContentCard
import com.gabr.gabc.qook.presentation.shared.components.QDialog
import com.gabr.gabc.qook.presentation.shared.components.QImageContainer
import com.gabr.gabc.qook.presentation.shared.components.QLoadingScreen
import com.gabr.gabc.qook.presentation.shared.components.QPlanning
import com.gabr.gabc.qook.presentation.shoppingListPage.ShoppingListPage
import com.gabr.gabc.qook.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlanningPage : ComponentActivity() {

    // TODO: Refactor all Intent Keys to own class
    companion object {
        const val FROM_PLANNING = "FROM_PLANNING"
        const val IS_LUNCH = "IS_LUNCH"
        const val HAS_UPDATED_PLANNING = "HAS_UPDATED_PLANNING"
        const val HAS_UPDATED_SHARED_PLANNING = "HAS_UPDATED_SHARED_PLANNING"
        const val SHARED_PLANNING_ID = "SHARED_PLANNING_ID"
        const val SHARED_PLANNING = "SHARED_PLANNING"
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val extras = result.data?.extras

                val isGroupDeleted =
                    extras?.getBoolean(PlanningSettingsPage.SHARED_PLANNING_REMOVED) ?: false
                if (isGroupDeleted) {
                    finish()
                }

                val viewModel: PlanningViewModel by viewModels()

                val updatedSharedPlanning =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        extras?.getParcelable(
                            HAS_UPDATED_SHARED_PLANNING,
                            SharedPlanning::class.java
                        )
                    } else {
                        extras?.getParcelable(HAS_UPDATED_SHARED_PLANNING)
                    }
                updatedSharedPlanning?.let {
                    viewModel.updateSharedPlanningMetadataLocally(it)
                }

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
                                dayPlanning.copy(lunch = dayPlanning.lunch.copy(meal = recipe))
                            } else {
                                dayPlanning.copy(dinner = dayPlanning.dinner.copy(meal = recipe))
                            }
                        )
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: .well-known/assetlinks.json must be updated with keystore SHA-256
        handleAppLinkIntent(intent)

        setContent {
            AppTheme {
                PlanningView()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleAppLinkIntent(intent)
    }

    private fun handleAppLinkIntent(intent: Intent) {
        val viewModel: PlanningViewModel by viewModels()
        val appLinkAction = intent.action
        val appLinkData = intent.data
        if (Intent.ACTION_VIEW == appLinkAction) {
            appLinkData?.lastPathSegment?.also { groupId ->
                viewModel.addUserToGroup(groupId) {
                    finish()
                }
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun PlanningView() {
        val viewModel: PlanningViewModel by viewModels()
        val planning = viewModel.planning.toList()
        val groupId = viewModel.groupId.value
        val isSharedPlanning = groupId != null

        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        var showResetDialog by remember { mutableStateOf(false) }

        LaunchedEffect(key1 = Unit, block = {
            val loadedPlanning = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableArrayExtra(HomePage.HOME_PLANNING, DayPlanning::class.java)
            } else {
                intent.getParcelableArrayExtra(HomePage.HOME_PLANNING)
            }

            val id = intent.getStringExtra(SHARED_PLANNING_ID)

            viewModel.loadPlanning(loadedPlanning?.map { it as DayPlanning }, id) { error ->
                scope.launch {
                    snackbarHostState.showSnackbar(
                        error
                    )
                }
            }
        })

        if (showResetDialog) {
            QDialog(
                onDismissRequest = { showResetDialog = false },
                leadingIcon = Icons.Outlined.RestartAlt,
                title = R.string.reset_planning,
                content = {
                    Text(stringResource(R.string.reset_planning_description))
                },
                buttonTitle = R.string.planning_reset_button,
                onSubmit = {
                    viewModel.resetPlanning { e ->
                        scope.launch {
                            snackbarHostState.showSnackbar(e)
                        }
                    }
                    showResetDialog = false
                },
            )
        }

        Box {
            Scaffold(
                topBar = {
                    QActionBar(
                        title = if (isSharedPlanning) {
                            R.string.shared_planning_title
                        } else {
                            R.string.home_planning_bnb
                        },
                        onBack = {
                            if (viewModel.hasUpdated.value && !isSharedPlanning) {
                                val resultIntent = Intent()
                                resultIntent.putExtra(
                                    HAS_UPDATED_PLANNING,
                                    viewModel.planning.toTypedArray()
                                )
                                setResult(RESULT_OK, resultIntent)
                            }
                            finish()
                        },
                        actions = if (isSharedPlanning) {
                            if (viewModel.sharedPlanning.value == SharedPlanning.EMPTY_SHARED_PLANNING) {
                                null
                            } else {
                                listOf {
                                    IconButton(onClick = {
                                        val intent =
                                            Intent(
                                                this@PlanningPage,
                                                PlanningSettingsPage::class.java
                                            )
                                        intent.putExtra(
                                            SHARED_PLANNING,
                                            viewModel.sharedPlanning.value
                                        )
                                        resultLauncher.launch(intent)
                                    }) {
                                        Icon(Icons.Outlined.Settings, "")
                                    }
                                }
                            }
                        } else {
                            listOf {
                                IconButton(onClick = {
                                    showResetDialog = true
                                }) {
                                    Icon(Icons.Outlined.RestartAlt, "")
                                }
                            }
                        }
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
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        if (!isSharedPlanning) {
                            Text(
                                stringResource(R.string.planning_description),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = MaterialTheme.colorScheme.outline
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        } else {
                            SharedPlanningHeaders()
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        QPlanning(
                            planning,
                            onClearFullDayPlanning = { dp ->
                                viewModel.updatePlanning(
                                    dp.copy(
                                        lunch = MealData.EMPTY_MEAL_DATA,
                                        dinner = MealData.EMPTY_MEAL_DATA,
                                    )
                                ) { }
                            },
                            onClearDayPlanning = { dp, isLunch ->
                                val emptyDp = if (isLunch) {
                                    dp.copy(lunch = MealData.EMPTY_MEAL_DATA)
                                } else {
                                    dp.copy(dinner = MealData.EMPTY_MEAL_DATA)
                                }
                                viewModel.updatePlanning(emptyDp) { }
                            },
                            onAddRecipeToDayPlanning = { dp, isLunch ->
                                val intent =
                                    Intent(this@PlanningPage, RecipesPage::class.java)
                                intent.putExtra(
                                    RecipesPage.RECIPES_LIST,
                                    viewModel.recipes.toTypedArray()
                                )
                                intent.putExtra(FROM_PLANNING, dp)
                                intent.putExtra(IS_LUNCH, isLunch)
                                intent.putExtra(SHARED_PLANNING_ID, groupId)
                                resultLauncher.launch(intent)
                            },
                            onRecipeTapped = { recipe ->
                                val intent =
                                    Intent(this@PlanningPage, RecipeDetailsPage::class.java)
                                intent.putExtra(RecipeDetailsPage.RECIPE, recipe)
                                intent.putExtra(RecipeDetailsPage.ALLOW_TO_UPDATE, false)
                                startActivity(intent)
                            },
                        )
                    }
                }
            }
            if (viewModel.isLoading.value) QLoadingScreen()
        }
    }

    @Composable
    fun SharedPlanningHeaders() {
        val viewModel: PlanningViewModel by viewModels()
        val planning = viewModel.planning.toList()
        val group = viewModel.sharedPlanning.value

        var showInfoDialog by remember { mutableStateOf(false) }

        if (showInfoDialog) {
            QDialog(
                onDismissRequest = { showInfoDialog = false },
                leadingIcon = Icons.Outlined.Info,
                title = R.string.planning_group_info,
                content = {
                    Text(stringResource(R.string.shared_planning_info_display))
                },
                buttonTitle = R.string.ok,
                onSubmit = {
                    showInfoDialog = false
                },
            )
        }

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            QImageContainer(
                uri = group.photo,
                placeholder = Icons.Outlined.Group,
                size = 100.dp
            )
            Spacer(modifier = Modifier.size(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    group.name,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 60.dp)
                )
                Spacer(modifier = Modifier.size(12.dp))
                IconButton(onClick = {
                    showInfoDialog = true
                }) {
                    Icon(Icons.Outlined.Info, "")
                }
            }
            Spacer(modifier = Modifier.size(8.dp))
            QContentCard(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                    .fillMaxWidth(),
                arrangement = Arrangement.Top,
                alignment = Alignment.CenterHorizontally,
                onClick = {
                    val intent =
                        Intent(this@PlanningPage, ShoppingListPage::class.java)
                    intent.putExtra(
                        HomePage.HOME_PLANNING,
                        planning.toTypedArray()
                    )
                    intent.putExtra(SHARED_PLANNING_ID, group.id)
                    startActivity(intent)
                }
            ) {
                Row {
                    Text(
                        stringResource(R.string.home_shopping_bnb),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Outlined.KeyboardArrowRight, "")
                }
            }
        }
    }
}