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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
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
import com.gabr.gabc.qook.presentation.planningPage.viewModel.PlanningViewModel
import com.gabr.gabc.qook.presentation.planningSettingsPage.PlanningSettingsPage
import com.gabr.gabc.qook.presentation.recipeDetailsPage.RecipeDetailsPage
import com.gabr.gabc.qook.presentation.recipesPage.RecipesPage
import com.gabr.gabc.qook.presentation.shared.IntentVars.Companion.ALLOW_TO_UPDATE
import com.gabr.gabc.qook.presentation.shared.IntentVars.Companion.FROM_PLANNING
import com.gabr.gabc.qook.presentation.shared.IntentVars.Companion.HAS_REMOVED_SHARED_PLANNING
import com.gabr.gabc.qook.presentation.shared.IntentVars.Companion.HAS_UPDATED_DAY_PLANNING
import com.gabr.gabc.qook.presentation.shared.IntentVars.Companion.HAS_UPDATED_DAY_PLANNING_WITH_RECIPE
import com.gabr.gabc.qook.presentation.shared.IntentVars.Companion.HAS_UPDATED_SHARED_PLANNING
import com.gabr.gabc.qook.presentation.shared.IntentVars.Companion.IS_LUNCH
import com.gabr.gabc.qook.presentation.shared.IntentVars.Companion.PLANNING
import com.gabr.gabc.qook.presentation.shared.IntentVars.Companion.RECIPE
import com.gabr.gabc.qook.presentation.shared.IntentVars.Companion.RECIPES_LIST
import com.gabr.gabc.qook.presentation.shared.IntentVars.Companion.RECIPE_OP
import com.gabr.gabc.qook.presentation.shared.IntentVars.Companion.SHARED_PLANNING
import com.gabr.gabc.qook.presentation.shared.IntentVars.Companion.SHARED_PLANNING_ID
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
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val extras = result.data?.extras

                val isGroupDeleted =
                    extras?.getBoolean(HAS_REMOVED_SHARED_PLANNING) ?: false
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
                    extras?.getParcelable(HAS_UPDATED_DAY_PLANNING, DayPlanning::class.java)
                } else {
                    extras?.getParcelable(HAS_UPDATED_DAY_PLANNING)
                }
                val updatedRecipeForPlanning =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        extras?.getParcelable(
                            HAS_UPDATED_DAY_PLANNING_WITH_RECIPE,
                            Recipe::class.java
                        )
                    } else {
                        extras?.getParcelable(HAS_UPDATED_DAY_PLANNING_WITH_RECIPE)
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

    @Composable
    fun PlanningView() {
        val viewModel: PlanningViewModel by viewModels()
        val planning = viewModel.planning.toList()
        val groupId = viewModel.groupId.value
        val group = viewModel.sharedPlanning.value
        val isSharedPlanning = groupId != null

        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        var showResetDialog by remember { mutableStateOf(false) }

        LaunchedEffect(key1 = Unit, block = {
            val loadedPlanning = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableArrayExtra(PLANNING, DayPlanning::class.java)
            } else {
                intent.getParcelableArrayExtra(PLANNING)
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
                                    HAS_UPDATED_DAY_PLANNING,
                                    viewModel.planning.toTypedArray()
                                )
                                setResult(RESULT_OK, resultIntent)
                            }
                            finish()
                        },
                        actions = if (isSharedPlanning) {
                            listOf(
                                {
                                    IconButton(onClick = {
                                        viewModel.loadPlanning(null, groupId) { e ->
                                            scope.launch {
                                                snackbarHostState.showSnackbar(e)
                                            }
                                        }
                                    }) {
                                        Icon(Icons.Outlined.Refresh, "")
                                    }
                                },
                                {
                                    IconButton(onClick = {
                                        val intent =
                                            Intent(
                                                this@PlanningPage,
                                                PlanningSettingsPage::class.java
                                            )
                                        intent.putExtra(SHARED_PLANNING, group)
                                        resultLauncher.launch(intent)
                                    }) {
                                        Icon(Icons.Outlined.Settings, "")
                                    }
                                }
                            )
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
                            users = group.users,
                            onClearFullDayPlanning = { dp ->
                                viewModel.updatePlanning(
                                    dp.copy(
                                        lunch = MealData.EMPTY,
                                        dinner = MealData.EMPTY,
                                    )
                                ) { }
                            },
                            onClearDayPlanning = { dp, isLunch ->
                                val emptyDp = if (isLunch) {
                                    dp.copy(lunch = MealData.EMPTY)
                                } else {
                                    dp.copy(dinner = MealData.EMPTY)
                                }
                                viewModel.updatePlanning(emptyDp) { }
                            },
                            onAddRecipeToDayPlanning = { dp, isLunch ->
                                val intent =
                                    Intent(this@PlanningPage, RecipesPage::class.java)
                                intent.putExtra(
                                    RECIPES_LIST,
                                    viewModel.recipes.toTypedArray()
                                )
                                intent.putExtra(FROM_PLANNING, dp)
                                intent.putExtra(IS_LUNCH, isLunch)
                                intent.putExtra(SHARED_PLANNING_ID, groupId)
                                resultLauncher.launch(intent)
                            },
                            onRecipeTapped = { recipe, op ->
                                val intent =
                                    Intent(this@PlanningPage, RecipeDetailsPage::class.java)
                                intent.putExtra(RECIPE, recipe)
                                intent.putExtra(ALLOW_TO_UPDATE, false)
                                intent.putExtra(
                                    RECIPE_OP,
                                    group.users.find { u -> u.id == op })
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
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                IconButton(onClick = {
                    showInfoDialog = true
                }) {
                    Icon(Icons.Outlined.Info, "")
                }
                Text(
                    group.name,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                )
                IconButton(
                    onClick = {
                        val intent = Intent().setAction(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.putExtra(
                            Intent.EXTRA_TEXT,
                            "${getString(R.string.shared_planning_message_on_code_sharing)}\n" +
                                    "${getString(R.string.deepLinkJoinGroup)}${group.id}"
                        )
                        startActivity(
                            Intent.createChooser(
                                intent,
                                getString(R.string.shared_planning_group_code)
                            )
                        )
                    }
                ) {
                    Icon(Icons.Outlined.Share, "")
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
                    intent.putExtra(PLANNING, planning.toTypedArray())
                    intent.putExtra(SHARED_PLANNING_ID, group.id)
                    startActivity(intent)
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.home_shopping_bnb),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, "")
                }
            }
        }
    }
}